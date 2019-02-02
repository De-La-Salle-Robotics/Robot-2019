package frc.robot;

import frc.robot.hardware.RobotMap;
import frc.robot.subsystem.*;

public class RobotVault{
    private Drivetrain drivetrain;
    private Arm arm;
    private Claw claw;

    public RobotVault(){
        RobotMap.initialize();

        //drivetrain = new Drivetrain(RobotMap.leftDrivetrain, RobotMap.rightDrivetrain);
        arm = new Arm(RobotMap.rightDrivetrain);
        claw = new Claw(RobotMap.claw1, RobotMap.claw2);
    }
    public void periodicTasks(){
        double throttle = -RobotMap.joy1.getRawAxis(1);
        double wheel = RobotMap.joy1.getRawAxis(2);
        
        boolean up = RobotMap.joy1.getRawButton(4);
        boolean down = RobotMap.joy1.getRawButton(2);

        boolean open = RobotMap.joy1.getRawButton(1);
        boolean close = RobotMap.joy1.getRawButton(3);

        //drivetrain.arcadeDrive(throttle, wheel);
        arm.armControl(up, down);
        claw.clawControl(close, open);
    }
}