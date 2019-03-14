package frc.robot.dashboard;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import frc.robot.pathfinder.Point;

public class Dashboard implements Runnable{
    private final int PORT = 5801;
    private DatagramSocket dashboardSocket;
    private byte[] buf;
    private InetAddress computerAddress;
    private int port;

    private boolean connectedWithComputer;

    public static DataStruct dataStruct;

    public class DataStruct
    {
        public Point p1 = new Point(1,1);
        public Point p2 = new Point(1,1);
        public Point p3 = new Point(1,1);
        public Point p4 = new Point(1,1);

        public double leftDist = 0;
        public double rightDist = 0;
        public double yaw = 0;

        public boolean pigeonReady = false;

        public int stringLength = 0;
        public String arbitraryString = "";
    }

    public Dashboard()
    {
        buf = new byte[256];
        connectedWithComputer = false;
        dataStruct = new DataStruct();

        new Thread(this).start();
    }

    public void connectWithDs()
    {
        if(dashboardSocket == null)
        {
            try
            {
                dashboardSocket = new DatagramSocket(PORT);
            }
            catch (Exception e) { return; }
        }

        try
        {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            dashboardSocket.receive(packet);
            byte[] receivedData = packet.getData();
            if(receivedData.length > 0 && receivedData[0] == 0x33)
            {
                computerAddress = packet.getAddress();
                port = packet.getPort();

                packet = new DatagramPacket(buf, buf.length, computerAddress, port);
                packet.setData(new byte[] { 0x77, 0x62 });
                dashboardSocket.send(packet);
                connectedWithComputer = true;
                System.out.println("===============Established Connection with Dashboard================");
            }
        }
        catch(Exception e) { }
    }
    public void run()
    {
        while(true)
        {
            connectWithDs();
            try
            {
                Thread.sleep(100);
            }
            catch(Exception e) { }
        }
    }

    public void executePeriodically()
    {
        if(!connectedWithComputer) return;

        dataStruct.stringLength = dataStruct.arbitraryString.length();

        byte[] bytesOfString = new byte[dataStruct.stringLength];
        char[] charsOfString = dataStruct.arbitraryString.toCharArray();
        for(int i = 0; i < dataStruct.stringLength; i++)
        {
            bytesOfString[i] = (byte)charsOfString[i];
        }
        
        ByteBuffer bb = ByteBuffer.allocate(256);

        bb.putDouble(dataStruct.p1.x).
        putDouble(dataStruct.p1.y).
        putDouble(dataStruct.p2.x).
        putDouble(dataStruct.p2.y).
        putDouble(dataStruct.p3.x).
        putDouble(dataStruct.p3.y).
        putDouble(dataStruct.p4.x).
        putDouble(dataStruct.p4.y).
        putDouble(dataStruct.leftDist).
        putDouble(dataStruct.rightDist).
        putDouble(dataStruct.yaw).
        put((byte)(dataStruct.pigeonReady ? 1 : 0)).
        putInt(dataStruct.stringLength).
        put(bytesOfString);

        byte[] dataToSend = bb.array();

        DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length, computerAddress, port);

        try
        {
            dashboardSocket.send(packetToSend);
        }
        catch(Exception e) { }
    }
}