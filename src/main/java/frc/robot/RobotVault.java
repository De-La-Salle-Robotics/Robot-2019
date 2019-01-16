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
        double throttle = RobotMap.joy1.getThrottle();
        double wheel = RobotMap.joy1.getTwist();

        drivetrain.arcadeDrive(throttle, wheel);
    }
}