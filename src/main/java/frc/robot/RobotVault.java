package frc.robot;

import frc.robot.dashboard.Dashboard;
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

    private Dashboard dashboardRef;

    public RobotVault() {
        RobotMap.initialize();

        led = new LED(RobotMap.can1);
        arm = new Arm(RobotMap.arm);
        claw = new Claw(RobotMap.claw1, RobotMap.claw2);
        hablift = new HabLift(RobotMap.liftLeftMaster, RobotMap.liftRightMaster);
        cameraLocalization = new CameraLocalization(RobotMap.ntInst);
        /* Drivetrain depends on previous instantiations */
        drivetrain = new Drivetrain(RobotMap.leftDrivetrain, RobotMap.rightDrivetrain, RobotMap.pigeon, cameraLocalization , led);

        deviceChecker = new DeviceChecker(RobotMap.liftLeftHelper, RobotMap.liftRightHelper, RobotMap.armHelper, 
            RobotMap.leftDriveHelper, RobotMap.rightDriveHelper);

        dashboardRef = new Dashboard();
    }

    public void periodicTasks() {
        cameraLocalization.onLoop();

        double throttle = -RobotMap.joy1.getRawAxis(1);
        double wheel = RobotMap.joy1.getRawAxis(2);
        boolean driverAssist = RobotMap.joy1.getRawButton(6);
        int pov = RobotMap.joy2.getPOV();

        boolean up = RobotMap.joy2.getRawButton(4);
        boolean down = RobotMap.joy2.getRawButton(1);

        boolean open = RobotMap.joy2.getRawButton(2);
        boolean close = RobotMap.joy2.getRawButton(3);

        boolean raiseLeft = RobotMap.joy2.getRawButton(5);
        boolean raiseRight = RobotMap.joy2.getRawButton(6);
        boolean lowerLeft = RobotMap.joy2.getRawAxis(2) > 0.3; /* These may actually be axis, IDK what joy2 is */
        boolean lowerRight = RobotMap.joy2.getRawAxis(3) > 0.3;

        throttle = ((throttle > 0) ? 1 : -1) * Math.pow(throttle, 2);
        wheel *= 0.5;

        drivetrain.driveControl(throttle, wheel, driverAssist, pov);
        arm.armControl(up, down);
        claw.clawControl(close, open);
        hablift.liftControl(raiseLeft, raiseRight, lowerLeft, lowerRight);    

        if(RobotMap.joy1.getRawButton(1))
        {
            RobotMap.pigeon.setYaw(0);
            RobotMap.leftDrivetrain.getSensorCollection().setQuadraturePosition(0,0);
            RobotMap.rightDrivetrain.getSensorCollection().setQuadraturePosition(0,0);
        }
        
        deviceChecker.checkForResets();
        dashboardRef.executePeriodically();
    }
}