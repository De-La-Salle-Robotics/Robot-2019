package frc.robot;

import frc.robot.hardware.RobotMap;
import frc.robot.subsystem.*; 
import frc.robot.subsystem.Arm;

public class RobotVault{
    private Drivetrain drivetrain;
    private Arm arm;

    public RobotVault(){
        RobotMap.initialize();

        drivetrain = new Drivetrain(RobotMap.leftDrivetrain, RobotMap.rightDrivetrain);
        arm = new Arm(RobotMap.arm);
    }
    public void periodicTasks(){
        double throttle = -RobotMap.joy1.getRawAxis(1);
        double wheel = RobotMap.joy1.getRawAxis(2);
        boolean armUpButton = RobotMap.joy1.getRawButton(4);
        boolean armDownButton = RobotMap.joy1.getRawButton(2);
        

        drivetrain.arcadeDrive(throttle, wheel);
        arm.armControl(armUpButton, armDownButton);
    }
}