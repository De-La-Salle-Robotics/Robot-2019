package frc.robot.subsystem;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class Drivetrain {
    public VictorSPX leftside;
    public VictorSPX rightside;

    public Drivetrain(VictorSPX leftside, VictorSPX rightside){
        this.leftside = leftside;
        this.rightside = rightside;
    }
    public void arcadeDrive(double throttle, double wheel){
        double leftthrottle = throttle + wheel; 
        double rightthrottle = throttle - wheel;
        leftside.set(ControlMode.PercentOutput,leftthrottle);
        rightside.set(ControlMode.PercentOutput,rightthrottle);
    }
}