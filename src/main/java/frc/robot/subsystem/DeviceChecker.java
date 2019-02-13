package frc.robot.subsystem;

import frc.robot.hardware.configuration.ArmConfiguration;
import frc.robot.hardware.configuration.LeftDriveConfiguration;
import frc.robot.hardware.configuration.LiftConfiguration;
import frc.robot.hardware.configuration.RightDriveConfiguration;

public class DeviceChecker {
    private LiftConfiguration liftHelper;
    private ArmConfiguration armHelper;
    private LeftDriveConfiguration leftDriveHelper;
    private RightDriveConfiguration rightDriveHelper;

    public DeviceChecker(LiftConfiguration liftHelper, ArmConfiguration armHelper,
            LeftDriveConfiguration leftDriveHelper, RightDriveConfiguration rightDriveHelper) {
        this.liftHelper = liftHelper;
        this.armHelper = armHelper;
        this.leftDriveHelper = leftDriveHelper;
        this.rightDriveHelper = rightDriveHelper;    
    }

    /* Call this periodically to check if any devices reset */
    public void checkForResets()
    {
        if(liftHelper.masterReset()) {
            liftHelper.masterSetter();
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