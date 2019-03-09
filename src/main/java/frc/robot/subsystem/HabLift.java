package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class HabLift {
    private VictorSPX liftLeft;
    private VictorSPX liftRight;

    public HabLift(VictorSPX liftLeft, VictorSPX liftRight) {
        this.liftLeft = liftLeft;
        this.liftRight = liftRight;
    }

    public void liftControl(boolean raiseLeft, boolean raiseRight, boolean lowerLeft, boolean lowerRight) {
        if (raiseLeft) {
            liftLeft.set(ControlMode.PercentOutput, 0.5);
        } else if (lowerLeft) {
            liftLeft.set(ControlMode.PercentOutput, -0.5);
        } else {
            liftLeft.set(ControlMode.PercentOutput, 0);
        }
        if (raiseRight) {
            liftRight.set(ControlMode.PercentOutput, 0.5);
        } else if (lowerRight) {
            liftRight.set(ControlMode.PercentOutput, -0.5);
        } else {
            liftRight.set(ControlMode.PercentOutput, 0);
        }
    }
}
