package frc.robot.hardware.configuration;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.sensors.PigeonIMU;

public class RightDriveConfiguration extends TalonSRXConfiguration {
    /* Reference to master device */
    private TalonSRX masterReference;
    /* Member variables for master device */
    private boolean setInvert = true;
    private boolean sensorPhase = true;

    public RightDriveConfiguration(TalonSRX masterRef, TalonSRX leftTalon, PigeonIMU pigeon) {
        /* Set Default Configs */
        super();

        /* Set configs unique to this */
        primaryPID.selectedFeedbackSensor = FeedbackDevice.SensorSum;
        auxiliaryPID.selectedFeedbackSensor = FeedbackDevice.RemoteSensor1;

        remoteFilter0.remoteSensorDeviceID = leftTalon.getDeviceID();
        remoteFilter0.remoteSensorSource = RemoteSensorSource.TalonSRX_SelectedSensor;

        remoteFilter1.remoteSensorDeviceID = pigeon.getDeviceID();
        remoteFilter1.remoteSensorSource = RemoteSensorSource.Pigeon_Yaw;

        /* Slot 0 gains */
        slot0.kP = 0.5;
        slot0.kI = 0.0;
        slot0.kD = 20.0;

        /* Slot 1 gains */
        slot1.kP = 1.0;
        slot1.kI = 0.0;
        slot1.kD = 10.0;

        neutralDeadband = 0.001;

        /* Set Reference */
        masterReference = masterRef;

        /* Set non configs */
        masterSetter();
    }

    public void masterSetter() {
        masterReference.setInverted(setInvert);
        masterReference.setSensorPhase(sensorPhase);
        masterReference.selectProfileSlot(0, 0);
        masterReference.selectProfileSlot(1, 1);
    }

    public void slaveSetter(BaseMotorController slaveReference) {
        slaveReference.follow(masterReference);
        slaveReference.setInverted(InvertType.FollowMaster);
    }

    public boolean masterReset() {
        return masterReference.hasResetOccurred();
    }
}