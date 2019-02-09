package frc.robot.hardware;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SerialPort.Port;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class RobotMap{
    public static final TalonSRX leftDrivetrain = new TalonSRX(1);
    public static final VictorSPX leftSlave = new VictorSPX(2);
    public static final TalonSRX rightDrivetrain = new TalonSRX(3);
    public static final VictorSPX rightSlave = new VictorSPX(4);
    public static final PowerDistributionPanel pdp = new PowerDistributionPanel(0);
    public static final Joystick joy1 = new Joystick(0);
    public static final Port lidarPort = Port.kUSB;
    public static final I2C.Port pixyI2C =I2C.Port.kMXP;
    
    public static void initialize(){
        leftSlave.follow(leftDrivetrain);
        rightSlave.follow(rightDrivetrain);

        leftSlave.setInverted(InvertType.FollowMaster);
        rightSlave.setInverted(InvertType.FollowMaster);

        leftDrivetrain.setInverted(false);
        rightDrivetrain.setInverted(true);


    }
}