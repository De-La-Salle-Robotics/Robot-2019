package frc.robot.pathfinder;

public class BezierCurve
{
    
    private Point _c1;
    private Point _c2;
    private Point _c3;
    private Point _c4;

    public BezierCurve(Point c1, Point c2, Point c3, Point c4)
    {
        _c1 = c1;
        _c2 = c2;
        _c3 = c3;
        _c4 = c4;
    }

    public Point getPoint(double t)
    {
        double n1 = Math.pow(1-t, 3);
        double n2 = Math.pow(1-t, 2) * Math.pow(t, 1) * 3;
        double n3 = Math.pow(1-t, 1) * Math.pow(t, 2) * 3;
        double n4 = Math.pow(t, 3);

        double x = (n1 * _c1.x) + (n2 * _c2.x) + (n3 * _c3.x) + (n4 * _c4.x);
        double y = (n1 * _c1.y) + (n2 * _c2.y) + (n3 * _c3.y) + (n4 * _c4.y);
        return new Point(x, y);
    }
    public Point getDerivative(double t)
    {
        double n1 = 3 * Math.pow(1-t, 2);
        double n2 = 6 * Math.pow(1-t, 1) * Math.pow(t, 1);
        double n3 = 3 * Math.pow(t, 2);

        double x = (n1 * (_c2.x - _c1.x)) + (n2 * (_c3.x -_c2.x)) 
                    + (n3 * (_c4.x - _c3.x));
        double y = (n1 * (_c2.y - _c1.y)) + (n2 * (_c3.y - _c2.y)) 
                    + (n3 * (_c4.y - _c3.y));

        return new Point(x, y);
    }

    public double getDeltaT(double currentT, double velocity)
    {
        Point derivative = getDerivative(currentT);
        double totalDerivative = Math.sqrt(Math.pow(derivative.x,2) + 
                                           Math.pow(derivative.y, 2));
        return velocity / totalDerivative;
    }

    public double getHeading(double currentT, double velocity)
    {
        Point currentPoint = getPoint(currentT);
        Point nextPoint = getPoint(currentT + getDeltaT(currentT, velocity));
        return Math.toDegrees(Math.atan2(nextPoint.y - currentPoint.y,
                                         nextPoint.x - currentPoint.x));
    }
}