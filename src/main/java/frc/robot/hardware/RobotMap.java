package frc.robot.hardware;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SerialPort.Port;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class RobotMap{
    public static VictorSPX leftDrivetrain = new VictorSPX(1);
    public static VictorSPX leftSlave = new VictorSPX(2);
    public static VictorSPX rightDrivetrain = new VictorSPX(3);
    public static VictorSPX rightSlave = new VictorSPX(4);
    public static PowerDistributionPanel pdp = new PowerDistributionPanel(0);
    public static Joystick joy1 = new Joystick(0);
    public static Port lidarPort = Port.kUSB;
    public static I2C.Port pixyI2C =I2C.Port.kMXP;
    
    public static void initialize(){
        CameraServer.getInstance().startAutomaticCapture();

        leftSlave.follow(leftDrivetrain);
        rightSlave.follow(rightDrivetrain);

        leftSlave.setInverted(InvertType.FollowMaster);
        rightSlave.setInverted(InvertType.FollowMaster);

        leftDrivetrain.setInverted(false);
        rightDrivetrain.setInverted(true);


    }
}