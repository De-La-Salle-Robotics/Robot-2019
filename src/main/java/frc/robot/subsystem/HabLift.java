package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class HabLift {
    private VictorSPX lift;

    public HabLift(VictorSPX lift) {
        this.lift = lift;
    }

    public void liftControl(boolean raiseLift, boolean lowerLift) {
        if (raiseLift) {
            lift.set(ControlMode.PercentOutput, 1);
        } else if (lowerLift) {
            lift.set(ControlMode.PercentOutput, -1);
        } else {
            lift.set(ControlMode.PercentOutput , 0);

        }
    }
}
