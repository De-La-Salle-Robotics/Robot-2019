package frc.robot;

import frc.robot.hardware.RobotMap;
import frc.robot.subsystem.*;

public class RobotVault{
    private Drivetrain drivetrain;

    public RobotVault(){
        RobotMap.initialize();

        drivetrain = new Drivetrain(RobotMap.leftDrivetrain, RobotMap.rightDrivetrain);
    }

    public void periodicTasks(){
        double throttle = -RobotMap.joy1.getRawAxis(1);
        double wheel = RobotMap.joy1.getRawAxis(2);

        drivetrain.arcadeDrive(throttle, wheel);
    }
}