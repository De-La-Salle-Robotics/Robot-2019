
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import edu.wpi.first.networktables.*;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import java.util.ArrayList;
import java.util.Comparator;


public class UsefulStuff implements Runnable {
    public static final int CAMERA_PORT = 1182;

    static class UdpData
    {
        public double distanceValue;
        public double angleValue;
        public double rawSeperation;
        public double rawMidpoint;
    }
    static class GroupedTarget
    {
        public IndividualTarget leftTarget;
        public IndividualTarget rightTarget;
        public Point topLeft;
        public Point bottomRight;
    }
    static class IndividualTarget implements Comparator<IndividualTarget>
    {
        public double midPoint;
        public boolean skewedLeft;
        
        public double farthestLeft;
        public double farthestRight;
        public double farthestUp;
        public double farthestDown;

        public int compare(IndividualTarget a, IndividualTarget b)
        {
            return (int)((a.midPoint - b.midPoint) * 100);
        }
    }
    
	private static double MAX_CAMERA_VALUE = 480;
	private static double DISTANCE_COEFFICIENT = 3092;
	private static double PIXEL_DEGREE_COEFFICIENT = 0.1720141821143758644330723260584;

	
	public static NetworkTableEntry distanceEntry;
	public static NetworkTableEntry angleEntry;
    public static NetworkTableEntry validDataEntry;

    public static ArrayList<GroupedTarget> targets = new ArrayList<GroupedTarget>();

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
                packet.setData(new byte[] { 0x77, 0x62, (byte)CAMERA_PORT, (byte)(CAMERA_PORT >> 8) });
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
        bb.putDouble(data.distanceValue).
        putDouble(data.angleValue).
        putDouble(data.rawSeperation).
        putDouble(data.rawMidpoint);

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

        targets.clear();

        ArrayList<IndividualTarget> tmp = new ArrayList<IndividualTarget>();
        for(MatOfPoint mat : contours)
        {
            tmp.add(getTargetInfo(mat));
        }
        tmp.sort(new IndividualTarget());
        for(int i = 0; i < tmp.size() - 1; i++)
        {
            IndividualTarget first = tmp.get(i);
            IndividualTarget second = tmp.get(i+1);
            if(!first.skewedLeft && second.skewedLeft)
            {
                GroupedTarget group = new GroupedTarget();
                group.leftTarget = first;
                group.rightTarget = second;

                group.topLeft = new Point(first.farthestLeft, Math.min(first.farthestUp, second.farthestUp));
                group.bottomRight = new Point(second.farthestRight, Math.max(first.farthestDown, second.farthestDown));

                targets.add(group);

                i++;
            }
        }
        if(targets.size() == 0) return false;
        IndividualTarget leftTarget = targets.get(0).leftTarget;
        IndividualTarget rightTarget = targets.get(0).rightTarget;

		double seperation = Math.abs(leftTarget.midPoint - rightTarget.midPoint);
		double distance = DISTANCE_COEFFICIENT / seperation;
		
		double midpoint = ((leftTarget.midPoint + rightTarget.midPoint) / 2);
		double angle = (midpoint - (MAX_CAMERA_VALUE / 2)) * PIXEL_DEGREE_COEFFICIENT;

		distanceEntry.setDouble(distance);
        angleEntry.setDouble(angle);
        if(establishedConnection)
        {
            UdpData dat = new UdpData();
            dat.distanceValue = distance;
            dat.angleValue = angle;
            dat.rawSeperation = seperation;
            dat.rawMidpoint = midpoint;
            sendUdpData(dat);
        }

		return true;
	}

	private static IndividualTarget getTargetInfo(MatOfPoint mat)
	{
        double leftPoint = 10000;
        double leftPointY = 0;
        double topY = 10000;
        double rightPoint = 0;
        double rightPointY = 0;
        double bottomY = 0;

		Point[] array = mat.toArray();

		for(Point p : array)
		{
            if(p.y < topY) topY = p.y;
            if(p.y > bottomY) bottomY = p.y;

			if(p.x < leftPoint){ leftPoint = p.x; leftPointY = p.y; }
			if(p.x > rightPoint) { rightPoint = p.x; rightPointY = p.y; }
		}
        IndividualTarget ret = new IndividualTarget();
        ret.midPoint = (leftPoint + rightPoint) / 2;
        ret.skewedLeft = leftPointY < rightPointY; // Less than because (0,0) is from upper left corner

        ret.farthestLeft = leftPoint;
        ret.farthestRight = rightPoint;
        ret.farthestUp = topY;
        ret.farthestDown = bottomY;
		return ret;
	}
}