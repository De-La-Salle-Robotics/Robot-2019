
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import edu.wpi.first.networktables.*;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import java.util.ArrayList;


public class UsefulStuff implements Runnable {

    static class UdpData
    {
        public double distanceValue;
        public double angleValue;
    }
    
	private static double MAX_CAMERA_VALUE = 320;
	private static double DISTANCE_COEFFICIENT = 3092;
	private static double PIXEL_DEGREE_COEFFICIENT = 0.1720141821143758644330723260584;

	
	public static NetworkTableEntry distanceEntry;
	public static NetworkTableEntry angleEntry;
	public static NetworkTableEntry validDataEntry;

	private static DatagramSocket computerSocket;
	private static byte[] buf;
    private static InetAddress computerAddress;
    private static int port;

	private static boolean establishedConnection = false;

	public static void handleUDP()
	{
        if(buf == null)
            buf = new byte[256];

        try
        {
            if(computerSocket == null)
                computerSocket = new DatagramSocket(5800);
        }
        catch (Exception e)
        {
            return;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try
        {
            computerSocket.receive(packet);
            byte[] receivedPacket = packet.getData();
            if(receivedPacket.length > 0 && receivedPacket[0] == 0x33)
            {
                computerAddress = packet.getAddress();
                port = packet.getPort();

                packet = new DatagramPacket(buf, buf.length, computerAddress, port);
                packet.setData(new byte[] { 0x77, 0x62 });
                computerSocket.send(packet);
                System.out.println("=========================Got UDP Frame========================");
                establishedConnection = true;
            }
        }
        catch(Exception ex) { System.out.println(ex.toString()); return; }
    }

    public static void sendUdpData(UdpData data)
    {
        ByteBuffer bb = ByteBuffer.allocate(256);
        bb.putDouble(data.distanceValue).putDouble(data.angleValue);

        byte[] sendData = bb.array();
        
        DatagramPacket packet = new DatagramPacket(sendData, bb.capacity(), computerAddress, port);
        try
        {
            computerSocket.send(packet);
        } catch (Exception ex) { }
    }
    
    public void run()
    {
        while(true)
        {
            handleUDP();
            try
            {
				Thread.sleep(100);
            }
            catch(Exception ex) { }
        }
    }

	public static boolean calculateDistAndAngle(ArrayList<MatOfPoint> contours)
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
        if(establishedConnection)
        {
            UdpData dat = new UdpData();
            dat.distanceValue = distance;
            dat.angleValue = angle;
            sendUdpData(dat);
        }

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