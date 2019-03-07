using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FRCDashboard
{
    class RaspberryPiData
    {
        public double TargetDistance { get; private set; }
        public double TargetAngle { get; private set; }
        public double RawSeperation { get; private set; }
        public double RawMidpoint { get; private set; }
        public long LoopTime { get; private set; }

        public void SetTargetDistance(double dist) { TargetDistance = dist; }
        public void SetTargetAngle(double angle) { TargetAngle = angle; }
        public void SetRawSeperation(double sep) { RawSeperation = sep; }
        public void SetRawMidpoint(double mid) { RawMidpoint = mid; }
        public void SetLoopTime(long time) { LoopTime = time; }
    }
}
