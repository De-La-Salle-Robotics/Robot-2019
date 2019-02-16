package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.sensors.PigeonIMU;

import frc.robot.pathfinder.BezierCurve;
import frc.robot.pathfinder.Localization;
import frc.robot.pathfinder.Point;
import frc.robot.pathfinder.PointGenerator;

public class Drivetrain {
    private final double DEGREES_TO_PIGEON = 8192.0;
    private final long TIME_FOR_CURVE_UPDATE = 100; /* Time in Milliseconds */
    private final long TIME_FOR_NO_CURVE = 500; /* Time in Milliseconds */

    private boolean ableToDriverAssist;
    private long timeOfLastCurveUpdate;

    private TalonSRX leftside;
    private TalonSRX rightside;
    private PigeonIMU pigeon;
    private CameraLocalization cameraLocalization;

    private PointGenerator pointGen;
    private Point robotPoint;
    private Point c2;
    private Point c3;
    private Point endPoint;
    private BezierCurve curve;

    private double currentT;

    public Drivetrain(TalonSRX leftside, TalonSRX rightside, PigeonIMU pigeon, CameraLocalization cameraLocalization) {
        this.leftside = leftside;
        this.rightside = rightside;
        this.pigeon = pigeon;
        this.cameraLocalization = cameraLocalization;

        ableToDriverAssist = false;
        timeOfLastCurveUpdate = 0;

        pointGen = new PointGenerator(0.4);
        robotPoint = new Point(0, 0);
        c2 = new Point(0, 0);
        c3 = new Point(0, 0);
        endPoint = new Point(0, 0);

        currentT = 0;
    }

    public void driveControl(double throttle, double wheel, boolean driverAssist) {
        /* First we calculate if we have the target and can help with driver assistance */
        double distanceFromTarget = cameraLocalization.getDistance();
        double angleToTarget = cameraLocalization.getAngle();
        /* First determine if we can assist driver from distanceFromTarget */
        if(distanceFromTarget > 0) {
            /* Data is valid, let's set the flag so we can assist driver */
            ableToDriverAssist = true;

            /* Determine if it's time to recalculate bezier curve */
            if(System.currentTimeMillis() - timeOfLastCurveUpdate > TIME_FOR_CURVE_UPDATE) {
                /* Let's update the curve */
                recalculateCurve(pigeon.getFusedHeading(), angleToTarget, distanceFromTarget);
                /* Let's set the time so we don't constantly re-update the curve */
                timeOfLastCurveUpdate = System.currentTimeMillis();
            }
        } else if(System.currentTimeMillis() - timeOfLastCurveUpdate > TIME_FOR_NO_CURVE) {
            /* It's been too long since we got valid data, clear the flag */
            ableToDriverAssist = false;
        }

        /* Then we determine if the driver wants the driver assistance */
        if (driverAssist) {
            if(ableToDriverAssist) {
                /* We want to assist driver and we can, so let's do it */
                assistDrive(throttle);
            } else {
                /* We want to assist driver, but we can't so arcade drive */
                arcadeDrive(throttle, wheel);
            }
        } else {
            arcadeDrive(throttle, wheel);
        }
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

    public void assistDriveWithGoodVariables(double throttle, double robotHeading, double targetHeading,
            double targetDistance) {
        recalculateCurve(robotHeading, targetHeading, targetDistance);
        assistDrive(throttle);
    }

    public void assistDrive(double throttle) {
        /* If no valid curve to follow, just drive forward */
        if (curve == null)
        {
            arcadeDrive(throttle, 0);
            return;
        }

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