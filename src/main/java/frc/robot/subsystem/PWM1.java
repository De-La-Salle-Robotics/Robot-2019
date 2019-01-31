package frc.robot.subsystem;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Servo;

public class PWM1{
    public Servo servo1;
    public Servo servo2;
    public PWM1( Servo servo1 , Servo servo2){ 
        this.servo1 = servo1;
        this.servo2 = servo2;}
    
        
      

       
    final int kAngleMax = 0; 
     final int kAngleMin = 0;
   
  


public void pwmServoControl(boolean clawClose, boolean clawOpen) {
    double pwmServoPower = 0;

    if(clawClose) {
        servo1.setAngle(kAngleMax);
        servo2.setAngle(kAngleMax);
    } else if(clawOpen){
       servo1.setAngle(kAngleMin);
       servo2.setAngle(kAngleMin);
    }else{
        servo1.setAngle(kAngleMin);
        servo2.setAngle(kAngleMin);
        
    }
 }
}



     
    




        
       
    



