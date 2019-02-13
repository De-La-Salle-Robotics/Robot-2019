package frc.robot;

import frc.robot.hardware.RobotMap;
import frc.robot.subsystem.*;
import frc.robot.subsystem.Arm;
import frc.robot.subsystem.HabLift;

public class RobotVault {
    private Drivetrain drivetrain;
    private LED led;
    private Arm arm;
    private Claw claw;
    private HabLift hablift;

    private DeviceChecker deviceChecker;

    public RobotVault() {
        RobotMap.initialize();

        drivetrain = new Drivetrain(RobotMap.leftDrivetrain, RobotMap.rightDrivetrain);
        led = new LED(RobotMap.can1);
        arm = new Arm(RobotMap.arm);
        claw = new Claw(RobotMap.claw1, RobotMap.claw2);
        hablift = new HabLift(RobotMap.liftMaster);

        deviceChecker = new DeviceChecker(RobotMap.liftHelper, RobotMap.armHelper, 
            RobotMap.leftDriveHelper, RobotMap.rightDriveHelper);
    }

    public void periodicTasks() {
        double throttle = -RobotMap.joy1.getRawAxis(1);
        double wheel = RobotMap.joy1.getRawAxis(2);

        boolean up = RobotMap.joy1.getRawButton(4);
        boolean down = RobotMap.joy1.getRawButton(2);

        boolean open = RobotMap.joy1.getRawButton(1);
        boolean close = RobotMap.joy1.getRawButton(3);

        boolean raise = RobotMap.joy1.getRawButton(5);
        boolean lower = RobotMap.joy1.getRawButton(6);

        drivetrain.arcadeDrive(throttle, wheel);
        arm.armControl(up, down);
        claw.clawControl(close, open);
        led.lighting(.5, .5, 0);
        hablift.liftControl(raise, lower);

        deviceChecker.checkForResets();
    }
}