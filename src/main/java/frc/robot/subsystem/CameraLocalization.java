package frc.robot.subsystem;

import frc.robot.jni.Pixy2USBJNI;

public class CameraLocalization {
    private final int PIXYCAM_MIDDLE = 178;
    private final double PIXELS_TO_DEGREES = -0.5;

    private Pixy2USBJNI pixyRef;

    private double SEPERATION_COEFFICIENT = 2900.;
    
    private double distanceFromTarget;
    private double targetAngle;
    private boolean distanceIsValid;

    public CameraLocalization(Pixy2USBJNI pixyRef) {
        this.pixyRef = pixyRef;
        distanceFromTarget = 0;
        distanceIsValid = false;
    }

    public void onLoop() {
        int l1 = pixyRef.getBlockX(0);
        int l2 = pixyRef.getBlockX(1);
        if(l1 >= 0 && l2 >= 0) {
            /* We have at least 2 blocks, that means we can calculate distance */
            distanceIsValid = true;

            /* Currently just assume the next two blocks */
            int seperation = Math.abs(l2 - l1);
            int middleOfTarget = (l1 + l2) / 2;

            targetAngle = (middleOfTarget - PIXYCAM_MIDDLE) * PIXELS_TO_DEGREES;

            distanceFromTarget = calculateDistanceFromSeperation(seperation);
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