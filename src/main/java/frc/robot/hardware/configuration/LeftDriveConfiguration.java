package frc.robot.hardware.configuration;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

public class LeftDriveConfiguration extends TalonSRXConfiguration {

    public LeftDriveConfiguration()
    {
        primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder;
    }

}