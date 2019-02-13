package frc.robot.hardware.configuration;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.*;

public class LeftDriveConfiguration extends TalonSRXConfiguration {
    /* Reference to master device */
    private TalonSRX masterReference;
    /* Member variables for master device */
    private boolean setInvert = false;
    private boolean sensorPhase = false;

    public LeftDriveConfiguration(TalonSRX masterRef) {
        /* Set Default Configs */
        super();

        /* Set configs unique to this */
        primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder;
        neutralDeadband = 0.001;

        /* Set Reference */
        masterReference = masterRef;

        /* Set non configs */
        masterSetter();
    }

    public void masterSetter() {
        masterReference.setInverted(setInvert);
        masterReference.setSensorPhase(sensorPhase);
    }

    public void slaveSetter(BaseMotorController slaveReference) {
        slaveReference.follow(masterReference);
        slaveReference.setInverted(InvertType.FollowMaster);
    }

    public boolean masterReset() {
        return masterReference.hasResetOccurred();
    }
}