package frc.robot.hardware;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.hardware.configuration.LeftDriveConfiguration;
import edu.wpi.first.wpilibj.Servo;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class RobotMap{
    public static final TalonSRX leftDrivetrain = new TalonSRX(1);
    public static final VictorSPX leftSlave = new VictorSPX(1);
    public static final TalonSRX rightDrivetrain = new TalonSRX(2);
    public static final VictorSPX rightSlave = new VictorSPX(2);

    public static final VictorSPX liftMaster = new VictorSPX(4);
    public static final VictorSPX lift2 = new VictorSPX(5);
    public static final VictorSPX lift3 = new VictorSPX(6);
    public static final VictorSPX lift4 = new VictorSPX(7);

    public static final PowerDistributionPanel pdp = new PowerDistributionPanel(0);

    public static final Joystick joy1 = new Joystick(0);

    public static final CANifier can1 = new CANifier(0);

    public static final VictorSPX arm = new VictorSPX(3);
    public static final Servo claw1 = new Servo(0);
    public static final Servo claw2 = new Servo(1);
    
    public static void initialize(){
        leftDrivetrain.configAllSettings(new LeftDriveConfiguration());

        leftSlave.follow(leftDrivetrain);
        rightSlave.follow(rightDrivetrain);

        leftSlave.setInverted(InvertType.FollowMaster);
        rightSlave.setInverted(InvertType.FollowMaster);

        leftDrivetrain.setInverted(false);
        rightDrivetrain.setInverted(true);

        lift2.follow(liftMaster);
        lift3.follow(liftMaster);
        lift4.follow(liftMaster);

        liftMaster.setInverted(false);
        lift2.setInverted(InvertType.FollowMaster);
        lift3.setInverted(InvertType.FollowMaster);
        lift4.setInverted(InvertType.FollowMaster);

    }
}