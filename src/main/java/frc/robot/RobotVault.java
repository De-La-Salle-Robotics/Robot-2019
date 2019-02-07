package frc.robot;

import frc.robot.hardware.RobotMap;
import frc.robot.hardware.lidar.Lidar;
import frc.robot.hardware.pixy.Pixy2CCC;
import frc.robot.hardware.pixy.PixyI2C;
import frc.robot.subsystem.*;

public class RobotVault{
    private Drivetrain drivetrain;
    private Lidar lidar;
    private Pixy2CCC pixyCam;

    public RobotVault(){
        RobotMap.initialize();

        drivetrain = new Drivetrain(RobotMap.leftDrivetrain, RobotMap.rightDrivetrain);
        lidar = new Lidar(RobotMap.lidarPort);
        pixyCam = new Pixy2CCC(new PixyI2C(RobotMap.pixyI2C));
    }

    public void periodicTasks(){
        /* First we call any methods that should get called once a loop */
        lidar.updateValues();
        int blockNumber = pixyCam.parseBlocks();

        /* Then we get driver controller values */
        double throttle = -RobotMap.joy1.getRawAxis(1);
        double wheel = RobotMap.joy1.getRawAxis(2);

        /* Then we print out anything important */
        System.out.println("Lidar distance is: " + lidar.getDistance());
        if(blockNumber > 0) //If we have a block, print it
            System.out.println("Pixy Block is: " + pixyCam.blocks[0].toString());

        /* Finally we run our subsystem tasks */
        drivetrain.arcadeDrive(throttle, wheel);
    }
}