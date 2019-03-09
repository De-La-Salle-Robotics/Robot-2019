package frc.robot.subsystem;

import frc.robot.hardware.configuration.ArmConfiguration;
import frc.robot.hardware.configuration.LeftDriveConfiguration;
import frc.robot.hardware.configuration.LiftConfiguration;
import frc.robot.hardware.configuration.RightDriveConfiguration;

public class DeviceChecker {
    private LiftConfiguration liftLeftHelper;
    private LiftConfiguration liftRightHelper;
    private ArmConfiguration armHelper;
    private LeftDriveConfiguration leftDriveHelper;
    private RightDriveConfiguration rightDriveHelper;

    public DeviceChecker(LiftConfiguration liftLeftHelper, LiftConfiguration liftRightHelper, ArmConfiguration armHelper,
            LeftDriveConfiguration leftDriveHelper, RightDriveConfiguration rightDriveHelper) {
        this.liftLeftHelper = liftLeftHelper;
        this.liftRightHelper = liftRightHelper;
        this.armHelper = armHelper;
        this.leftDriveHelper = leftDriveHelper;
        this.rightDriveHelper = rightDriveHelper;    
    }

    /* Call this periodically to check if any devices reset */
    public void checkForResets()
    {
        if(liftLeftHelper.masterReset()) {
            liftLeftHelper.masterSetter();
        }
        if(liftRightHelper.masterReset()) {
            liftRightHelper.masterSetter();
        }
        if(armHelper.masterReset()) {
            armHelper.masterSetter();
        }
        if(leftDriveHelper.masterReset()) {
            leftDriveHelper.masterSetter();
        }
        if(rightDriveHelper.masterReset()) {
            rightDriveHelper.masterSetter();
        }
    }
}