package frc.robot.subsystem;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.dashboard.Dashboard;

public class CameraLocalization {
    private final int PIXYCAM_MIDDLE = 178;
    private final double PIXELS_TO_DEGREES = -0.5;

    private NetworkTableInstance ntInst;

    private NetworkTableEntry validData;
    private NetworkTableEntry targetDistanceEntry;
    private NetworkTableEntry targetAngleEntry;

    private double SEPERATION_COEFFICIENT = 2900.;
    
    private double distanceFromTarget;
    private double targetAngle;
    private boolean distanceIsValid;

    public CameraLocalization(NetworkTableInstance networkTableInst) {
        ntInst = networkTableInst;

        validData = ntInst.getEntry("ValidDataEntry");
        targetDistanceEntry = ntInst.getEntry("DistanceEntry");
        targetAngleEntry = ntInst.getEntry("AngleEntry");

        distanceFromTarget = 0;
        distanceIsValid = false;
    }

    public void onLoop() {
        
        if(validData.getBoolean(false)) {
            targetAngle = -targetAngleEntry.getDouble(-1.0);
            distanceFromTarget = targetDistanceEntry.getDouble(-1.0);
            
            distanceIsValid = (distanceFromTarget > 0);
        }
    }

    public double getDistance() {
        if(distanceIsValid) {
            /* Set valid flag to false, and return distance */
            distanceIsValid = false;
            return distanceFromTarget;
        } 
        /* Otherwise return impossible value */
        return -1;
    }

    /* Don't bother about valid or not, distance returns valid or not */
    public double getAngle() {
        return targetAngle;
    }

    private double calculateDistanceFromSeperation(int seperation) {
        return SEPERATION_COEFFICIENT * (1.0 / (double)seperation);
    }
}