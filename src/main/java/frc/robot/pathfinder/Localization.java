package frc.robot.pathfinder;

public class Localization
{
    private static Point specifiedPoint;
    private static double specifiedHeading;
    public static Point getSpecifiedPoint() { return specifiedPoint; }
    public static double getSpecifiedHeading() { return specifiedHeading; }

    /* Assuming we get distance in a straight line */
    public static void calculatePoint(double robotHeading, 
                                      double dist, 
                                      double targetHeading)
    {
        double b = dist * Math.sin(Math.toRadians(robotHeading));
        double a = b * Math.tan(Math.toRadians(robotHeading + targetHeading));
        specifiedPoint = new Point(a, b);

        /* This will probably change as we figure out how to better localize */
        specifiedHeading = robotHeading;
    }
    /* Assuming we get distance from target */
    public static void calculatePointOffset(double robotHeading, 
                                      double dist, 
                                      double targetHeading)
    {
        double b = dist * Math.cos(Math.toRadians(targetHeading));
        double a = dist * Math.sin(Math.toRadians(targetHeading));
        specifiedPoint = new Point(a, b);

        /* This will probably change as we figure out how to better localize */
        specifiedHeading = robotHeading;
    }
}