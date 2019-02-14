package frc.robot.subsystem;

import frc.robot.hardware.pixy.Pixy2CCC;

public class CameraLocalization {
    private final int PIXYCAM_MIDDLE = 300;
    private final double PIXELS_TO_DEGREES = 0.1;

    private byte SIGNAL_MAP = Pixy2CCC.CCC_SIG1;
    private Pixy2CCC pixyRef;

    private double SEPERATION_COEFFICIENT = 1.0;
    
    private double distanceFromTarget;
    private double targetAngle;
    private boolean distanceIsValid;

    public CameraLocalization(Pixy2CCC pixyRef) {
        this.pixyRef = pixyRef;
        distanceFromTarget = 0;
        distanceIsValid = false;
    }

    public void onLoop() {
        pixyRef.parseBlocks(SIGNAL_MAP);
        Pixy2CCC.Block[] blocks = pixyRef.blocks;
        if(pixyRef.numBlocks > 1) {
            /* We have at least 2 blocks, that means we can calculate distance */
            distanceIsValid = true;

            /* Currently just assume the next two blocks */
            int center0 = blocks[0].m_x + (blocks[0].m_width / 2);
            int center1 = blocks[1].m_x + (blocks[1].m_width / 2);
            int seperation = Math.abs(center1 - center0);
            int middleOfTarget = (center0 + center1) / 2;

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
        return SEPERATION_COEFFICIENT * (1 / seperation);
    }
}