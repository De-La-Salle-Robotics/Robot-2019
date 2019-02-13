package frc.robot.pathfinder;

public class Localization
{
    private static Point specifiedPoint;
    private static double specifiedHeading;
    public Point getSpecifiedPoint() { return specifiedPoint; }
    public double getSpecifiedHeading() { return specifiedHeading; }

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
}