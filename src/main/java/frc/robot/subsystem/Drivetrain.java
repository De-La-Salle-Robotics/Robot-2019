package frc.robot.subsystem;
import frc.robot.hardware.RobotMap;

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
    private final long TIME_FOR_NO_CURVE = 5000; /* Time in Milliseconds */
    
    private final double INCHES_TO_NATIVE_UNITS = 80.35;

    private boolean ableToDriverAssist;
    private long timeOfLastCurveUpdate;
    private long timeSinceLastGood;
    private double distanceWhenRecalculated;
    private double distanceToTravel;

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

    private boolean lastDriverAssist;

    private LED led;

    public Drivetrain(TalonSRX leftside, TalonSRX rightside, PigeonIMU pigeon, CameraLocalization cameraLocalization , LED led) {
        this.leftside = leftside;
        this.rightside = rightside;
        this.pigeon = pigeon;
        this.cameraLocalization = cameraLocalization;
        this.led = led;

        ableToDriverAssist = false;
        timeOfLastCurveUpdate = 0;
        timeSinceLastGood = 0;
        distanceWhenRecalculated = 0;
        distanceToTravel = 0;
        lastDriverAssist = false;

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
            System.out.println("Valid data");
            led.lighting(.425 , .115 , .0025);
            /* Data is valid, let's set the flag so we can assist driver */
            ableToDriverAssist = true;
            timeSinceLastGood = System.currentTimeMillis();

            /* Determine if it's time to recalculate bezier curve */
            if(//System.currentTimeMillis() - timeOfLastCurveUpdate > TIME_FOR_CURVE_UPDATE &&
                driverAssist && !lastDriverAssist) {
                System.out.println("Recalculating");
                /* Let's update the curve */
                double[] ypr = new double[3];
                pigeon.getYawPitchRoll(ypr);
                recalculateCurve(ypr[0], angleToTarget, distanceFromTarget);
                /* Let's set the time so we don't constantly re-update the curve */
                timeOfLastCurveUpdate = System.currentTimeMillis();
            }
            lastDriverAssist = driverAssist;
        } else if(System.currentTimeMillis() - timeSinceLastGood > TIME_FOR_NO_CURVE) {
            System.out.println("Clearing Valid data");
            /* It's been too long since we got valid data, clear the flag */
            ableToDriverAssist = false;
            led.lighting(.431, .258, .956);
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
            curve = null;
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

        distanceWhenRecalculated = rightside.getSelectedSensorPosition();
        distanceToTravel = 0;

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

        throttle *= 1;

        double deltaT = curve.getDeltaT(currentT, throttle);

        //if(deltaT < 0.001) return;

        Point currentPoint = curve.getPoint(currentT);
        Point nextPoint = curve.getPoint(currentT + deltaT);
        
        double distanceDelta = curve.getDistance(currentPoint, nextPoint);
        distanceToTravel += distanceDelta;
        double heading = curve.getHeading(currentPoint, nextPoint);

        currentT += deltaT;

        if(currentT >= 1)
        {
            rightside.set(ControlMode.PercentOutput, 0);
            leftside.set(ControlMode.PercentOutput, 0);
        }
        else
        {
            leftside.follow(rightside, FollowerType.AuxOutput1);
            rightside.set(ControlMode.Position, (distanceToTravel * INCHES_TO_NATIVE_UNITS) + distanceWhenRecalculated, DemandType.AuxPID, (heading / 360) * DEGREES_TO_PIGEON);    
        }
    }
}