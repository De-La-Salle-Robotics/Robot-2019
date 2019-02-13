package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.can.*;

import frc.robot.pathfinder.BezierCurve;
import frc.robot.pathfinder.Localization;
import frc.robot.pathfinder.Point;
import frc.robot.pathfinder.PointGenerator;

public class Drivetrain {
    private final double DEGREES_TO_PIGEON = 8192.0;

    private TalonSRX leftside;
    private TalonSRX rightside;

    private PointGenerator pointGen;
    private Point robotPoint;
    private Point c2;
    private Point c3;
    private Point endPoint;
    private BezierCurve curve;

    private double currentT;

    public Drivetrain(TalonSRX leftside, TalonSRX rightside) {
        this.leftside = leftside;
        this.rightside = rightside;

        pointGen = new PointGenerator(0.4);
        robotPoint = new Point(0,0);
        c2 = new Point(0,0);
        c3 = new Point(0,0);
        endPoint = new Point(0,0);

        currentT = 0;
    }

    public void arcadeDrive(double throttle, double wheel) {
        double leftthrottle = throttle + wheel;
        double rightthrottle = throttle - wheel;
        leftside.set(ControlMode.PercentOutput, leftthrottle);
        rightside.set(ControlMode.PercentOutput, rightthrottle);
    }

    public void recalculateCurve(double robotHeading, double targetHeading, double targetDistance) {
        Localization.calculatePointOffset(robotHeading, targetDistance, targetHeading);
        endPoint = Localization.getSpecifiedPoint();

        c2 = pointGen.getControl2(robotPoint, Localization.getSpecifiedHeading(), endPoint);
        c3 = pointGen.getControl3(robotPoint, endPoint, 0);

        currentT = 0;

        curve = new BezierCurve(robotPoint, c2, c3, endPoint);
    }

    public void assistDriveWithGoodVariables(double throttle, double robotHeading, double targetHeading, double targetDistance) {
        recalculateCurve(robotHeading, targetHeading, targetDistance);
        assistDrive(throttle);
    }
    
    public void assistDrive(double throttle) {
        /* If no valid curve to follow, just drive forward */
        if(curve == null) arcadeDrive(throttle, 0);

        double nextT = curve.getDeltaT(currentT, throttle);
        Point currentPoint = curve.getPoint(currentT);
        Point nextPoint = curve.getPoint(nextT);

        double distance = curve.getDistance(currentPoint, nextPoint);
        double heading = curve.getHeading(currentPoint, nextPoint);

        nextT = currentT;

        leftside.follow(rightside, FollowerType.AuxOutput1);
        rightside.set(ControlMode.Position, distance, DemandType.AuxPID, heading * DEGREES_TO_PIGEON);
    }
}