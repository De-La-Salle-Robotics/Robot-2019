package frc.robot;

import frc.robot.hardware.RobotMap;
import frc.robot.subsystem.*;


public class RobotVault{
    private Drivetrain drivetrain;
    private LED led;

    public RobotVault(){
        RobotMap.initialize();

        drivetrain = new Drivetrain(RobotMap.leftDrivetrain, RobotMap.rightDrivetrain);
        led = new LED(RobotMap.can1);
    }

    public void periodicTasks(){
        double throttle = -RobotMap.joy1.getRawAxis(1);
        double wheel = RobotMap.joy1.getRawAxis(2);
        led.lighting(.5, .5, 0);

        drivetrain.arcadeDrive(throttle, wheel);
    }
}