using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FRCDashboard
{
    class ConnectionObject
    {
        public string RaspberryPiAddress { get; set; } = "10.77.62.6";
        public string RoboRioAddress { get; set; } = "10.77.62.2";

        public string RaspberryPiIp { get; private set; }
        public int    RaspberryPiPort { get; private set; }
        public string RoboRioIp { get; private set; }

        public void SetPiIp(string ip) { RaspberryPiIp = ip; }
        public void SetPiPort(int port) { RaspberryPiPort = port; }
        public void SetRioIp(string ip) { RoboRioIp = ip; }
    }
}
