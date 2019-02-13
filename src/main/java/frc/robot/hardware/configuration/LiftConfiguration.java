package frc.robot.hardware.configuration;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.*;

public class LiftConfiguration extends VictorSPXConfiguration 
{
    /* Reference to master device */
    private VictorSPX masterReference;
    /* Member variables for master device */
    private boolean setInvert = false;

    public LiftConfiguration(VictorSPX masterRef)
    {
        /* Set Default Configs */
        super();

        /* Set configs unique to this */
        // Nothing special for this

        /* Set Reference */
        masterReference = masterRef;

        /* Set non configs */
        masterSetter();
    }

    public void masterSetter()
    {
        masterReference.setInverted(setInvert);
    }

    public void slaveSetter(BaseMotorController slaveReference)
    {
        slaveReference.follow(masterReference);
        slaveReference.setInverted(InvertType.FollowMaster);
    }
}