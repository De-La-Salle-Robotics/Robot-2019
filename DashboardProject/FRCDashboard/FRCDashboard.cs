using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Net;
using System.IO;
using System.Net.Sockets;

using AForge.Video;
using AForge.Video.DirectShow;

namespace FRCDashboard
{
    public partial class FRCDashboard : Form
    {
        private MJPEGStream stream;
        private int cameraStreamPort = 0;
        private int cameraProcessedPort = 0;

        private UdpClient raspberryPiClient;
        private IPAddress piAddress;
        private bool establishedConnectionWithPi;
        private IPEndPoint piEndPoint;
        private int piPort = 5800;
        private bool attemptingToConnectToPi = false;
        private RaspberryPiData raspberryPiData = new RaspberryPiData();
        private object piLock = new object();

        private UdpClient rioClient;
        private IPAddress rioAddress;
        private bool establishedConnectionWithRio;
        private IPEndPoint rioEndPoint;
        private int rioPort = 5801;
        private bool attemptingToConnectToRio = false;
        private RioData rioData = new RioData();
        private object rioLock = new object();

        private object camLock = new object();
        private object dataLock = new object();

        private ConnectionObject connectionObject = new ConnectionObject();
        private bool setConnectionGridObject = true;

        private bool runThreads = true;

        System.Diagnostics.Stopwatch st = new System.Diagnostics.Stopwatch();

        public FRCDashboard()
        {
            System.Threading.Thread.BeginCriticalRegion();
            InitializeComponent();
            System.Threading.Thread.EndCriticalRegion();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            establishedConnectionWithPi = false;
            establishedConnectionWithRio = false;

            pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;

            grdConnectionProperties.SelectedObject = connectionObject;
            grdRaspPi.SelectedObject = raspberryPiData;
            grdRio.SelectedObject = rioData;
            st.Start();
        }

        private void AttemptConnectPi()
        {
            string piStringAddress;
            try
            {
                piAddress = IPAddress.Parse(connectionObject.RaspberryPiAddress);
                piStringAddress = piAddress.ToString();
                raspberryPiClient = new UdpClient();
                raspberryPiClient.Client.SendTimeout = 500;
                raspberryPiClient.Client.ReceiveTimeout = 1000;
                raspberryPiClient.Client.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveBuffer, 0);
                piEndPoint = new IPEndPoint(IPAddress.Any, piPort);
                
                while (!establishedConnectionWithPi && raspberryPiClient != null && runThreads)
                {
                    raspberryPiClient.Connect(piAddress, piPort);
                    raspberryPiClient.Send(new byte[] { 0x33 }, 1);
                    byte[] ret = raspberryPiClient.Receive(ref piEndPoint);
                    if (ret[0] == 0x77 && ret[1] == 0x62)
                    {
                        cameraStreamPort = ret[2] | ((int)ret[3] << 8);

                        lock (dataLock)
                        {
                            connectionObject.SetPiPort(cameraStreamPort);
                        }
                        if (stream == null)
                        {
                            stream = new MJPEGStream("http://" + connectionObject.RaspberryPiAddress + ":" + cameraStreamPort + "/stream.mjpg");
                            stream.NewFrame += new NewFrameEventHandler(FinalVideoDevice_NewFrame);
                            //stream.Start();
                        }

                        establishedConnectionWithPi = true;
                        new System.Threading.Thread(PiUpdateThread).Start();
                    }
                    System.Threading.Thread.Sleep(1000);
                }
            }
            catch
            {
                piStringAddress = "Could not resolve " + connectionObject.RaspberryPiAddress;
            }
            lock (dataLock)
            {
                connectionObject.SetPiIp(piStringAddress);
            }
            attemptingToConnectToPi = false;
        }

        private void AttemptConnectRio()
        {
            string rioStringAddress;
            try
            {
                rioAddress = IPAddress.Parse(connectionObject.RoboRioAddress);
                rioStringAddress = rioAddress.ToString();
                rioClient = new UdpClient();
                rioClient.Client.SendTimeout = 500;
                rioClient.Client.ReceiveTimeout = 500;
                rioClient.Client.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveBuffer, 0);
                rioEndPoint = new IPEndPoint(IPAddress.Any, rioPort);

                while (!establishedConnectionWithRio && rioClient != null && runThreads)
                {
                    rioClient.Connect(rioAddress, rioPort);
                    rioClient.Send(new byte[] { 0x33 }, 1);
                    byte[] ret = rioClient.Receive(ref rioEndPoint);
                    if (ret[0] == 0x77 && ret[1] == 0x62)
                    {
                        establishedConnectionWithRio = true;
                        new System.Threading.Thread(RioUpdateThread).Start();
                    }
                    System.Threading.Thread.Sleep(1000);
                }
            }
            catch
            {
                rioStringAddress = "Could not resolve " + connectionObject.RoboRioAddress;
            }
            connectionObject.SetRioIp(rioStringAddress);

            attemptingToConnectToRio = false;
        }

        private void PiUpdateThread()
        {
            while (establishedConnectionWithPi && runThreads)
            {
                byte[] piBuf;
                try
                {
                    piBuf = raspberryPiClient.Receive(ref piEndPoint);
                }
                catch
                {
                    if (stream != null)
                    {
                        stream.Stop();
                        stream = null;
                    }
                    establishedConnectionWithPi = false;
                    break;
                }

                double dist = BitConverter.ToDouble(piBuf, 0);
                double angle = BitConverter.ToDouble(piBuf, 8);
                double rawSep = BitConverter.ToDouble(piBuf, 16);
                double rawMid = BitConverter.ToDouble(piBuf, 24);
                long loopTime = BitConverter.ToInt64(piBuf, 32);

                if (BitConverter.IsLittleEndian)
                {
                    FlipDouble(ref dist);
                    FlipDouble(ref angle);
                    FlipDouble(ref rawSep);
                    FlipDouble(ref rawMid);
                    FlipLong(ref loopTime);
                }
                lock (piLock)
                {
                    raspberryPiData.SetTargetAngle(angle);
                    raspberryPiData.SetTargetDistance(dist);
                    raspberryPiData.SetRawSeperation(rawSep);
                    raspberryPiData.SetRawMidpoint(rawMid);
                    raspberryPiData.SetLoopTime(loopTime);
                }

                System.Threading.Thread.Sleep(100);
            }
        }
        private void RioUpdateThread()
        {
            while (runThreads)
            {
                byte[] rioBuf;
                try
                {
                    rioBuf = rioClient.Receive(ref rioEndPoint);
                }
                catch
                {
                    establishedConnectionWithRio = false;
                    break;
                }
                if (rioBuf.Length < 10) continue;
                double p1x = BitConverter.ToDouble(rioBuf, 0);
                double p1y = BitConverter.ToDouble(rioBuf, 8);
                double p2x = BitConverter.ToDouble(rioBuf, 16);
                double p2y = BitConverter.ToDouble(rioBuf, 24);
                double p3x = BitConverter.ToDouble(rioBuf, 32);
                double p3y = BitConverter.ToDouble(rioBuf, 40);
                double p4x = BitConverter.ToDouble(rioBuf, 48);
                double p4y = BitConverter.ToDouble(rioBuf, 56);

                double leftDist = BitConverter.ToDouble(rioBuf, 64);
                double rightDist = BitConverter.ToDouble(rioBuf, 72);
                double yaw = BitConverter.ToDouble(rioBuf, 80);

                bool ready = rioBuf[88] == 1;

                Int32 stringSize = BitConverter.ToInt32(rioBuf, 89);
                if(BitConverter.IsLittleEndian)
                {
                    FlipInt(ref stringSize);
                }
                string arbitraryString = BitConverter.ToString(rioBuf, 91, stringSize);

                if(BitConverter.IsLittleEndian)
                {
                    FlipDouble(ref p1x);
                    FlipDouble(ref p1y);
                    FlipDouble(ref p2x);
                    FlipDouble(ref p2y);
                    FlipDouble(ref p3x);
                    FlipDouble(ref p3y);
                    FlipDouble(ref p4x);
                    FlipDouble(ref p4y);

                    FlipDouble(ref leftDist);
                    FlipDouble(ref rightDist);
                    FlipDouble(ref yaw);

                    FlipString(ref arbitraryString);
                }

                lock (rioLock)
                {
                    rioData.SetP1(new RioData.Point(p1x, p1y));
                    rioData.SetP2(new RioData.Point(p2x, p2y));
                    rioData.SetP3(new RioData.Point(p3x, p3y));
                    rioData.SetP4(new RioData.Point(p4x, p4y));

                    rioData.SetLeftDist(leftDist);
                    rioData.SetRightDist(rightDist);
                    rioData.SetYaw(yaw);

                    rioData.SetPigeonState(ready);

                    rioData.SetStringLength(stringSize);
                    rioData.SetArbitraryString(arbitraryString);
                }

                System.Threading.Thread.Sleep(100);
            }
        }

        private void FlipDouble(ref double doub)
        {
            var ar = BitConverter.GetBytes(doub);
            Array.Reverse(ar);
            doub = BitConverter.ToDouble(ar, 0);
        }
        private void FlipLong(ref long lng)
        {
            var ar = BitConverter.GetBytes(lng);
            Array.Reverse(ar);
            lng = BitConverter.ToInt64(ar, 0);
        }
        private void FlipInt(ref Int32 it)
        {
            var ar = BitConverter.GetBytes(it);
            Array.Reverse(ar);
            it = BitConverter.ToInt32(ar, 0);
        }
        private void FlipString(ref string str)
        {
            StringBuilder sb = new StringBuilder();
            int sz = str.Length;
            for(int i = sz-1; i >= 0; i--)
            {
                sb.Append(str[i]);
            }
            str = sb.ToString();
        }

        void FinalVideoDevice_NewFrame(object sender, NewFrameEventArgs e)
        {
            lock (camLock)
            {
                try
                {
                    if (pictureBox1.Image != null)
                        pictureBox1.Image.Dispose();
                    pictureBox1.Image = (Bitmap)e.Frame.Clone();
                }

                catch { }
            }
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            runThreads = false;
            if(raspberryPiClient != null)
                raspberryPiClient.Dispose();
            if (stream != null)
            {
                stream.Stop();
                stream = null;
            }
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if(establishedConnectionWithPi)
            {

            }
            else
            {
                if (!attemptingToConnectToPi)
                {
                    attemptingToConnectToPi = true;
                    var tmp = new System.Threading.Thread(AttemptConnectPi);
                    tmp.IsBackground = true;
                    tmp.Start();
                }  
            }

            if(establishedConnectionWithRio)
            {

            }
            else
            {
                if(!attemptingToConnectToRio)
                {
                    attemptingToConnectToRio = true;
                    var tmp = new System.Threading.Thread(AttemptConnectRio);
                    tmp.IsBackground = true;
                    tmp.Start();
                }
            }

            lock (dataLock)
            {
                if (setConnectionGridObject)
                    grdConnectionProperties.SelectedObject = connectionObject;
                else
                    connectionObject = (ConnectionObject)grdConnectionProperties.SelectedObject;
            }

            lock(piLock)
                grdRaspPi.SelectedObject = raspberryPiData;
            lock(rioLock)
                grdRio.SelectedObject = rioData;
        }

        private void grdConnectionProperties_Enter(object sender, EventArgs e)
        {
            setConnectionGridObject = false;
        }

        private void grdConnectionProperties_Leave(object sender, EventArgs e)
        {
            setConnectionGridObject = true;
        }

        private void grdConnectionProperties_PreviewKeyDown(object sender, PreviewKeyDownEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                setConnectionGridObject = true;

            }
        }
    }
}
