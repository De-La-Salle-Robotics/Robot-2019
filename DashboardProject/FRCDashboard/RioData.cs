using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FRCDashboard
{
    /*
        public Point p1 = new Point(1, 1);
        public Point p2 = new Point(1, 1);
        public Point p3 = new Point(1, 1);
        public Point p4 = new Point(1, 1);

        public double leftDist = 0;
        public double rightDist = 0;
        public double yaw = 0;

        public boolean pigeonReady = false;

        public int stringLength = 0;
        public String arbitraryString = "";
    */
    class RioData
    {
        public class Point
        {
            public Point(double x, double y) { this.x = x; this.y = y; }
            public double x;
            public double y;
            
            public override string ToString() { return "(" + x + ", " + y + ")"; }
        }
        public string P1 { get; private set; }
        public string P2 { get; private set; }
        public string P3 { get; private set; }
        public string P4 { get; private set; }

        public double LeftDist { get; private set; }
        public double RightDist { get; private set; }
        public double Yaw { get; private set; }

        public string PigeonState { get; private set; }

        public string ArbitraryString { get; private set; }

        public void SetP1(Point p1) { P1 = p1.ToString(); }
        public void SetP2(Point p2) { P2 = p2.ToString(); }
        public void SetP3(Point p3) { P3 = p3.ToString(); }
        public void SetP4(Point p4) { P4 = p4.ToString(); }
        public void SetLeftDist(double leftDist) { LeftDist = leftDist; }
        public void SetRightDist(double rightDist) { RightDist = rightDist; }
        public void SetYaw(double yaw) { Yaw = yaw; }
        public void SetPigeonState(bool ready) { PigeonState = ready ? "Ready" : "Not Ready"; }
        public void SetArbitraryString(string val) { ArbitraryString = val; }
    }
}
