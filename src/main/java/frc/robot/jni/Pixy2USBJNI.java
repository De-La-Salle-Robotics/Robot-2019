package frc.robot.jni;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation taken from Bullbots TestPixy2USBCameraServer repo
 * Seen here:
 * https://github.com/bullbots/TestPixy2USBCameraServer
 */

public class Pixy2USBJNI {
    static {
       System.loadLibrary("pixy2_usb");
    }
  
    // Declare an instance native method sayHello() which receives no parameter and returns void
    private native int pixy2USBInit();

    private native void pixy2USBGetVersion();

    private native void pixy2USBLampOn();

    private native void pixy2USBLampOff();

    private native void pixy2USBStartCameraServer();

    private native void pixy2USBLoopCameraServer();

    private native int getX(int blockIndex);

    public AtomicBoolean toggleLamp = new AtomicBoolean(false);
    private boolean lampOn = false;
    private boolean goodInit;

    public Pixy2USBJNI()
    {
        int initResult = pixy2USBInit();
        if(initResult == 0) goodInit = true;
        else goodInit = false;

    }

    public int getBlockX(int index)
    {
        if(goodInit)
            return getX(index);
        return -1;
    }
}
