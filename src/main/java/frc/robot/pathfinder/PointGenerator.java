package frc.robot.pathfinder;

public class PointGenerator
{
    private double _pushCoefficient;

    public PointGenerator(double pushCoefficient)
    {
        _pushCoefficient = pushCoefficient;
    }

    
    public Point getControl2(Point start,
                             double startHeading,
                             Point end)
    {
        /* Find approximate distance between points */
        double dist = Math.sqrt(Math.pow(end.x - start.x, 2) + 
                                Math.pow(end.y - start.y, 2));
        /* Calculate x and y from push coefficient and heading */
        double x = (_pushCoefficient * dist * 
                   Math.sin(Math.toRadians(startHeading))) + start.x;
        double y = (_pushCoefficient * dist * 
                   Math.cos(Math.toRadians(startHeading))) + start.y;

        return new Point(x, y);
    }

    public Point getControl3(Point start,
                             Point end,
                             double endHeading)
    {
        /* Find approximate distance between points */
        double dist = Math.sqrt(Math.pow(end.x - start.x, 2) + 
                                Math.pow(end.y - start.y, 2));
        /* Calculate x and y from push coefficient and heading */
        double x = end.x - (_pushCoefficient * dist * 
                   Math.sin(Math.toRadians(endHeading)));
        double y = end.y - (_pushCoefficient * dist * 
                   Math.cos(Math.toRadians(endHeading)));
        
        return new Point(x, y);
    }
}