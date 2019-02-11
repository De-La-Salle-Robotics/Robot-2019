package frc.robot.subsystem;

import edu.wpi.first.wpilibj.*;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Servo;


public class Arm{

    public VictorSPX arm;
    public Arm(VictorSPX arm){
        this.arm = arm; 
    }

    
    public void armControl(boolean armUp , boolean armDown){
        double armPower = 0;
    
        
        if(armUp) {
            armPower = 0.5;
        } else if(armDown){
            armPower = -0.5;
        }else{
            armPower = 0;
        }
        arm.set(ControlMode.PercentOutput,armPower);{
        }
    }
}

  