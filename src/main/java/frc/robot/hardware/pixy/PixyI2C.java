package frc.robot.hardware.pixy;

public class PixyI2C extends TPixy2
{
    public PixyI2C(edu.wpi.first.wpilibj.I2C portDef)
    {
        super();
        m_link = new LinkI2C();
        m_link.open(portDef);
    }
}