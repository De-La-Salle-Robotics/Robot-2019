package frc.robot.hardware;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.hardware.configuration.ArmConfiguration;
import frc.robot.hardware.configuration.LeftDriveConfiguration;
import frc.robot.hardware.configuration.LiftConfiguration;
import frc.robot.hardware.configuration.RightDriveConfiguration;
import edu.wpi.first.wpilibj.Servo;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class RobotMap {
    /* Drivetrain motor controllers */
    public static final TalonSRX leftDrivetrain = new TalonSRX(1);
    public static final VictorSPX leftSlave = new VictorSPX(1);
    public static final TalonSRX rightDrivetrain = new TalonSRX(2);
    public static final VictorSPX rightSlave = new VictorSPX(2);

    /* Arm motor controllers/servos */
    public static final VictorSPX arm = new VictorSPX(3);
    public static final Servo claw1 = new Servo(0);
    public static final Servo claw2 = new Servo(1);

    /* Lift motor controllers */
    public static final VictorSPX liftMaster = new VictorSPX(4);
    public static final VictorSPX lift2 = new VictorSPX(5);
    public static final VictorSPX lift3 = new VictorSPX(6);
    public static final VictorSPX lift4 = new VictorSPX(7);

    /* Miscellaneous Items */
    public static final PowerDistributionPanel pdp = new PowerDistributionPanel(0);
    public static final CANifier can1 = new CANifier(0);

    /* Joysticks */
    public static final Joystick joy1 = new Joystick(0);

    /* Configuration helpers */
    public static final LeftDriveConfiguration leftDriveHelper = new LeftDriveConfiguration(leftDrivetrain);
    public static final RightDriveConfiguration rightDriveHelper = new RightDriveConfiguration(rightDrivetrain);
    public static final ArmConfiguration armHelper = new ArmConfiguration(arm, can1);
    public static final LiftConfiguration liftHelper = new LiftConfiguration(liftMaster);

    public static void initialize() {
        /* Drivetrain Initialization */
        leftDrivetrain.configAllSettings(leftDriveHelper);
        rightDrivetrain.configAllSettings(rightDriveHelper);

        leftDriveHelper.masterSetter();
        rightDriveHelper.masterSetter();

        leftDriveHelper.slaveSetter(leftSlave);
        rightDriveHelper.slaveSetter(rightSlave);

        /* Arm Initialization */
        arm.configAllSettings(armHelper);
        
        armHelper.masterSetter();

        /* Lift Initialization */
        liftMaster.configAllSettings(liftHelper);

        liftHelper.masterSetter();
        liftHelper.slaveSetter(lift2);
        liftHelper.slaveSetter(lift3);
        liftHelper.slaveSetter(lift4);
    }
}