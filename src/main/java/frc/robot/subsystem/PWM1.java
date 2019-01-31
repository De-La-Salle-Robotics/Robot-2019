package frc.robot.subsystem;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Servo;

public class PWM1{
    public Servo servo1;
    public Servo servo2;

    final int kAngleMax1 = 120; 
    final int kAngleMax2 = 120;
    final int kAngleMin1 = 40;
    final int kAngleMin2 = 40;


    public PWM1( Servo servo1 , Servo servo2){ 
        this.servo1 = servo1;
        this.servo2 = servo2;
    }
       
   
  


public void pwmServoControl(boolean clawClose, boolean clawOpen) {

    if(clawClose) {
        servo1.setAngle(kAngleMax1);
        servo2.setAngle(kAngleMax2);
    } else if(clawOpen){
       servo1.setAngle(kAngleMin1);
       servo2.setAngle(kAngleMin2);
    }else{
        
    }
 }
}



     
    




        
       
    



