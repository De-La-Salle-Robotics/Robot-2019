package frc.robot.hardware.pixy;

public interface LinkType
{
    abstract public int open(edu.wpi.first.wpilibj.I2C.Port portDef);
    abstract public int recv(byte[] buf, int len);
    abstract public int send(byte[] buf, int len);
    abstract public void close();
    abstract public void clearBuffer();

    abstract public int getChecksum();
}