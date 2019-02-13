package frc.robot.hardware.configuration;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.can.*;

public class ArmConfiguration extends VictorSPXConfiguration 
{
    /* Reference to master device */
    private VictorSPX masterReference;
    /* Member variables for master device */
    private boolean setInvert = false;

    public ArmConfiguration(VictorSPX masterRef, CANifier limitSwitchCANifier)
    {
        /* Set Default Configs */
        super();

        /* Set configs unique to this */
        /* Using remote limit switches is a maybe atm, so it's commented out */
        //forwardLimitSwitchSource = RemoteLimitSwitchSource.RemoteCANifier;
        //forwardLimitSwitchDeviceID = limitSwitchCANifier.getDeviceID();

        /* Set Reference */
        masterReference = masterRef;

        /* Set non configs */
        masterSetter();
    }

    public void masterSetter()
    {
        masterReference.setInverted(setInvert);
    }
}