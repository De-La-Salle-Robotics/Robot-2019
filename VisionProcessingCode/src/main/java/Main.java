
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.*;
import edu.wpi.first.vision.VisionThread;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

/*
   JSON format:
   {
       "team": <team number>,
       "ntmode": <"client" or "server", "client" if unspecified>
       "cameras": [
           {
               "name": <camera name>
               "path": <path, e.g. "/dev/video0">
               "pixel format": <"MJPEG", "YUYV", etc>   // optional
               "width": <video mode width>              // optional
               "height": <video mode height>            // optional
               "fps": <video mode fps>                  // optional
               "brightness": <percentage brightness>    // optional
               "white balance": <"auto", "hold", value> // optional
               "exposure": <"auto", "hold", value>      // optional
               "properties": [                          // optional
                   {
                       "name": <property name>
                       "value": <property value>
                   }
               ],
               "stream": {                              // optional
                   "properties": [
                       {
                           "name": <stream property name>
                           "value": <stream property value>
                       }
                   ]
               }
           }
       ]
   }
 */

public final class Main {
	private static double MAX_CAMERA_VALUE = 320;
	private static double DISTANCE_COEFFICIENT = 3092;
	private static double PIXEL_DEGREE_COEFFICIENT = 0.1720141821143758644330723260584;

	
	private static NetworkTableEntry distanceEntry;
	private static NetworkTableEntry angleEntry;
	private static NetworkTableEntry validDataEntry;


	private static String configFile = "/boot/frc.json";

	@SuppressWarnings("MemberName")
	public static class CameraConfig {
		public String name;
		public String path;
		public JsonObject config;
		public JsonElement streamConfig;
	}

	public static int team;
	public static boolean server;
	public static List<CameraConfig> cameraConfigs = new ArrayList<>();

	private Main() {
	}

	/**
	 * Report parse error.
	 */
	public static void parseError(String str) {
		System.err.println("config error in '" + configFile + "': " + str);
	}

	/**
	 * Read single camera configuration.
	 */
	public static boolean readCameraConfig(JsonObject config) {
		CameraConfig cam = new CameraConfig();

		// name
		JsonElement nameElement = config.get("name");
		if (nameElement == null) {
			parseError("could not read camera name");
			return false;
		}
		cam.name = nameElement.getAsString();

		// path
		JsonElement pathElement = config.get("path");
		if (pathElement == null) {
			parseError("camera '" + cam.name + "': could not read path");
			return false;
		}
		cam.path = pathElement.getAsString();

		// stream properties
		cam.streamConfig = config.get("stream");

		cam.config = config;

		cameraConfigs.add(cam);
		return true;
	}

	/**
	 * Read configuration file.
	 */
	@SuppressWarnings("PMD.CyclomaticComplexity")
	public static boolean readConfig() {
		// parse file
		JsonElement top;
		try {
			top = new JsonParser().parse(Files.newBufferedReader(Paths.get(configFile)));
		} catch (IOException ex) {
			System.err.println("could not open '" + configFile + "': " + ex);
			return false;
		}

		// top level must be an object
		if (!top.isJsonObject()) {
			parseError("must be JSON object");
			return false;
		}
		JsonObject obj = top.getAsJsonObject();

		// team number
		JsonElement teamElement = obj.get("team");
		if (teamElement == null) {
			parseError("could not read team number");
			return false;
		}
		team = teamElement.getAsInt();

		// ntmode (optional)
		if (obj.has("ntmode")) {
			String str = obj.get("ntmode").getAsString();
			if ("client".equalsIgnoreCase(str)) {
				server = false;
			} else if ("server".equalsIgnoreCase(str)) {
				server = true;
			} else {
				parseError("could not understand ntmode value '" + str + "'");
			}
		}

		// cameras
		JsonElement camerasElement = obj.get("cameras");
		if (camerasElement == null) {
			parseError("could not read cameras");
			return false;
		}
		JsonArray cameras = camerasElement.getAsJsonArray();
		for (JsonElement camera : cameras) {
			if (!readCameraConfig(camera.getAsJsonObject())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Start running the camera.
	 */
	public static VideoSource startCamera(CameraConfig config) {
		System.out.println("Starting camera '" + config.name + "' on " + config.path);
		CameraServer inst = CameraServer.getInstance();
		UsbCamera camera = new UsbCamera(config.name, config.path);
		MjpegServer server = inst.startAutomaticCapture(camera);

		Gson gson = new GsonBuilder().create();

		camera.setConfigJson(gson.toJson(config.config));
		camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

		if (config.streamConfig != null) {
			server.setConfigJson(gson.toJson(config.streamConfig));
		}

		return camera;
	}

	/**
	 * Main.
	 */
	public static void main(String... args) {
		if (args.length > 0) {
			configFile = args[0];
		}

		// read configuration
		if (!readConfig()) {
			return;
		}

		// start NetworkTables
		NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
		if (server) {
			System.out.println("Setting up NetworkTables server");
			ntinst.startServer();
		} else {
			System.out.println("Setting up NetworkTables client for team " + team);
			ntinst.startClientTeam(team);
		}

		distanceEntry = ntinst.getEntry("DistanceEntry");
		angleEntry = ntinst.getEntry("AngleEntry");
		validDataEntry = ntinst.getEntry("ValidDataEntry");

		// start cameras
		List<VideoSource> cameras = new ArrayList<>();
		for (CameraConfig cameraConfig : cameraConfigs) {
			cameras.add(startCamera(cameraConfig));
		}

		VisionTargetPipeline ballPipe = new VisionTargetPipeline();

		// start image processing on camera 0 if present
		if (cameras.size() >= 1) {
			VisionThread visionThread = new VisionThread(cameras.get(0), ballPipe, pipeline -> {
				// do something with pipeline results
				ArrayList<MatOfPoint> arr = ballPipe.filterContoursOutput();

				validDataEntry.setBoolean(calculateDistAndAngle(arr));

			});
			/*
			 * something like this for GRIP: VisionThread visionThread = new
			 * VisionThread(cameras.get(0), new GripPipeline(), pipeline -> { ... });
			 */
			visionThread.start();
		}

		// loop forever
		for (;;) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ex) {
				return;
			}
		}
	}

	private static boolean calculateDistAndAngle(ArrayList<MatOfPoint> contours)
	{
		if(contours.size() < 2) return false; //Return if there isn't at least 2 contours

		double mid1 = getMiddleOfMat(contours.get(0));
		double mid2 = getMiddleOfMat(contours.get(1));

		double seperation = Math.abs(mid1 - mid2);
		double distance = DISTANCE_COEFFICIENT / seperation;
		
		double direction = ((mid1 + mid2) / 2) - (MAX_CAMERA_VALUE / 2);
		double angle = direction * PIXEL_DEGREE_COEFFICIENT;

		distanceEntry.setDouble(distance);
		angleEntry.setDouble(angle);

		System.out.println("Distance is: " + distance + "\tAngle is: " + angle);
		System.out.println();

		return true;
	}

	private static double getMiddleOfMat(MatOfPoint mat)
	{
		double smallest = 10000;
		double largest = 0;

		Point[] array = mat.toArray();

		for(Point p : array)
		{
			if(p.x < smallest) smallest = p.x;
			if(p.x > largest) largest = p.x;
		}

		return (smallest + largest) / 2;
	}
}
