package frc.robot.hardware;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SerialPort.Port;
import frc.robot.hardware.configuration.ArmConfiguration;
import frc.robot.hardware.configuration.LeftDriveConfiguration;
import frc.robot.hardware.configuration.LiftConfiguration;
import frc.robot.hardware.configuration.RightDriveConfiguration;
import edu.wpi.first.wpilibj.Servo;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;

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
    public static final VictorSPX liftLeftMaster = new VictorSPX(4);
    public static final VictorSPX liftLeftSlave = new VictorSPX(5);
    public static final VictorSPX liftRightMaster = new VictorSPX(6);
    public static final VictorSPX liftRightSlave = new VictorSPX(7);

    /* Miscellaneous Items */
    public static final PowerDistributionPanel pdp = new PowerDistributionPanel(0);
    public static final CANifier can1 = new CANifier(0);
    public static final PigeonIMU pigeon = new PigeonIMU(0);

    /* Joysticks */
    public static final Joystick joy1 = new Joystick(0);
    public static final Joystick joy2 = new Joystick(1);

    /* External devices */
    public static final Port lidarPort = Port.kUSB;
    public static final NetworkTableInstance ntInst = NetworkTableInstance.create();

    /* Configuration helpers */
    public static final LeftDriveConfiguration leftDriveHelper = new LeftDriveConfiguration(leftDrivetrain);
    public static final RightDriveConfiguration rightDriveHelper = new RightDriveConfiguration(rightDrivetrain, leftDrivetrain, pigeon);
    public static final ArmConfiguration armHelper = new ArmConfiguration(arm, can1);
    public static final LiftConfiguration liftLeftHelper = new LiftConfiguration(liftLeftMaster);
    public static final LiftConfiguration liftRightHelper = new LiftConfiguration(liftRightMaster);

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
        liftLeftMaster.configAllSettings(liftLeftHelper);
        liftRightMaster.configAllSettings(liftRightHelper);

        liftLeftHelper.masterSetter();
        liftRightHelper.masterSetter();
        
        liftLeftHelper.slaveSetter(liftLeftSlave);
        liftRightHelper.slaveSetter(liftRightSlave);
    }
}