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
    private CameraLocalization cameraLocalization;

    private DeviceChecker deviceChecker;

    public RobotVault() {
        RobotMap.initialize();

        //led = new LED(RobotMap.can1);
        arm = new Arm(RobotMap.arm);
        claw = new Claw(RobotMap.claw1, RobotMap.claw2);
        hablift = new HabLift(RobotMap.liftMaster);
        cameraLocalization = new CameraLocalization(RobotMap.pixyCam);
        /* Drivetrain depends on previous instantiations */
        drivetrain = new Drivetrain(RobotMap.leftDrivetrain, RobotMap.rightDrivetrain, RobotMap.pigeon, cameraLocalization);

        deviceChecker = new DeviceChecker(RobotMap.liftHelper, RobotMap.armHelper, 
            RobotMap.leftDriveHelper, RobotMap.rightDriveHelper);
    }

    public void periodicTasks() {
        cameraLocalization.onLoop();

        double throttle = -RobotMap.joy1.getRawAxis(1);
        double wheel = RobotMap.joy1.getRawAxis(2);
        boolean driverAssist = RobotMap.joy1.getRawButton(6);

        boolean up = RobotMap.joy2.getRawButton(4);
        boolean down = RobotMap.joy2.getRawButton(1);

        boolean open = RobotMap.joy2.getRawButton(2);
        boolean close = RobotMap.joy2.getRawButton(3);

        boolean raise = RobotMap.joy2.getRawButton(5);
        boolean lower = RobotMap.joy2.getRawButton(6);

        drivetrain.driveControl(throttle, wheel, driverAssist);
        arm.armControl(up, down);
        claw.clawControl(close, open);
        //led.lighting(.5, .5, 0);
        hablift.liftControl(raise, lower);

        if(RobotMap.joy1.getRawButton(1))
        {
            RobotMap.pigeon.setYaw(0);
            RobotMap.leftDrivetrain.getSensorCollection().setQuadraturePosition(0,0);
            RobotMap.rightDrivetrain.getSensorCollection().setQuadraturePosition(0,0);
        }
        
        deviceChecker.checkForResets();
    }
}