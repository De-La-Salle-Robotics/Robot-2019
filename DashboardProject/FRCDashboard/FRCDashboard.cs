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

        private UdpClient raspberryPiClient;
        private IPAddress piAddress;
        private bool establishedConnectionWithPi;
        private IPEndPoint piEndPoint;
        private int piPort = 5800;
        private byte[] piBuf = new byte[256];
        private object piLockObject = new object();
        private bool attemptingToConnectToPi = false;
        private RaspberryPiData raspberryPiData = new RaspberryPiData();

        private UdpClient rioClient;
        private IPAddress rioAddress;
        private bool establishedConnectionWithRio;
        private IPEndPoint rioEndPoint;
        private int rioPort = 5801;
        private byte[] rioBuf = new byte[256];
        private object rioLockObject = new object();
        private bool attemptingToConnectToRio = false;

        private ConnectionObject connectionObject = new ConnectionObject();
        private bool setConnectionGridObject = true;

        private bool runThreads = true;


        public FRCDashboard()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            establishedConnectionWithPi = false;
            establishedConnectionWithRio = false;

            pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;

            grdConnectionProperties.SelectedObject = connectionObject;
            grdRaspPi.SelectedObject = raspberryPiData;
        }

        private void AttemptConnectPi()
        {
            string piStringAddress;
            try
            {
                piAddress = Dns.GetHostAddresses(connectionObject.RaspberryPiAddress)[0];
                piStringAddress = piAddress.ToString();
                raspberryPiClient = new UdpClient();
                raspberryPiClient.Client.SendTimeout = 1000;
                raspberryPiClient.Client.ReceiveTimeout = 1000;
                raspberryPiClient.Client.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveBuffer, 0);
                piEndPoint = new IPEndPoint(IPAddress.Any, piPort);

                new System.Threading.Thread(PiConnectThread).Start();
            }
            catch
            {
                piStringAddress = "Could not resolve " + connectionObject.RaspberryPiAddress;
            }
            connectionObject.SetPiIp(piStringAddress);
            attemptingToConnectToPi = false;
        }

        private void AttemptConnectRio()
        {
            string rioStringAddress;
            try
            {
                rioAddress = Dns.GetHostAddresses(connectionObject.RoboRioAddress)[0];
                rioStringAddress = rioAddress.ToString();
                rioClient = new UdpClient();
                rioClient.Client.SendTimeout = 1000;
                rioClient.Client.ReceiveTimeout = 1000;
                rioClient.Client.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveBuffer, 0);
                rioEndPoint = new IPEndPoint(IPAddress.Any, rioPort);

                new System.Threading.Thread(RioConnectThread).Start();
            }
            catch
            {
                rioStringAddress = "Could not resolve " + connectionObject.RoboRioAddress;
            }
            connectionObject.SetRioIp(rioStringAddress);
            attemptingToConnectToRio = false;
        }

        private void PiConnectThread()
        {
            while (!establishedConnectionWithPi && raspberryPiClient != null && runThreads)
            {
                try
                {
                    raspberryPiClient.Connect(piAddress, piPort);
                    raspberryPiClient.Send(new byte[] { 0x33 }, 1);
                    byte[] ret = raspberryPiClient.Receive(ref piEndPoint);
                    if (ret[0] == 0x77 && ret[1] == 0x62)
                    {
                        cameraStreamPort = ret[2] | ((int)ret[3] << 8);
                        stream = new MJPEGStream("http://frcvision.local:" + cameraStreamPort + "/stream.mjpg");
                        stream.NewFrame += new NewFrameEventHandler(FinalVideoDevice_NewFrame);
                        stream.Start();

                        establishedConnectionWithPi = true;
                        new System.Threading.Thread(UpdatePiBuf).Start();
                    }
                }
                catch { }
            }
        }
        private void UpdatePiBuf()
        {
            while (runThreads)
            {
                lock (piLockObject)
                {
                    try
                    {
                        piBuf = raspberryPiClient.Receive(ref piEndPoint);
                    }
                    catch
                    {
                        stream.Stop();
                        establishedConnectionWithPi = false;
                        break;
                    }
                }
                System.Threading.Thread.Sleep(100);
            }
        }

        private void RioConnectThread()
        {
            while (!establishedConnectionWithRio && rioClient != null && runThreads)
            {
                try
                {
                    rioClient.Connect(rioAddress, rioPort);
                    rioClient.Send(new byte[] { 0x33 }, 1);
                    byte[] ret = rioClient.Receive(ref rioEndPoint);
                    if (ret[0] == 0x77 && ret[1] == 0x62)
                    {
                        establishedConnectionWithRio = true;
                        new System.Threading.Thread(UpdateRioBuf).Start();
                    }
                }
                catch { }
            }
        }
        private void UpdateRioBuf()
        {
            while (runThreads)
            {
                lock (rioLockObject)
                {
                    try
                    {
                        rioBuf = rioClient.Receive(ref rioEndPoint);
                    }
                    catch { }
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

        void FinalVideoDevice_NewFrame(object sender, NewFrameEventArgs e)
        {
            try
            {
                if(pictureBox1.Image != null)
                    pictureBox1.Image.Dispose();
                pictureBox1.Image = (Bitmap)e.Frame.Clone();
            }
            catch { }
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            runThreads = false;
            if(raspberryPiClient != null)
                raspberryPiClient.Dispose();
            if(stream != null)
                stream.Stop();
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if(establishedConnectionWithPi)
            {
                lock (piLockObject)
                {
                    double dist = BitConverter.ToDouble(piBuf, 0);
                    double angle = BitConverter.ToDouble(piBuf, 8);
                    double rawSep = BitConverter.ToDouble(piBuf, 16);
                    double rawMid = BitConverter.ToDouble(piBuf, 24);

                    if (BitConverter.IsLittleEndian)
                    {
                        FlipDouble(ref dist);
                        FlipDouble(ref angle);
                        FlipDouble(ref rawSep);
                        FlipDouble(ref rawMid);
                    }

                    raspberryPiData.SetTargetAngle(angle);
                    raspberryPiData.SetTargetDistance(dist);
                    raspberryPiData.SetRawSeperation(rawSep);
                    raspberryPiData.SetRawMidpoint(rawMid);
                    connectionObject.SetPiPort(cameraStreamPort.ToString());
                }
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

            if (setConnectionGridObject)
                grdConnectionProperties.SelectedObject = connectionObject;
            else
                connectionObject = (ConnectionObject)grdConnectionProperties.SelectedObject;

            grdRaspPi.SelectedObject = raspberryPiData;
        }

        private void grdConnectionProperties_Enter(object sender, EventArgs e)
        {
            setConnectionGridObject = false;
        }

        private void grdConnectionProperties_Leave(object sender, EventArgs e)
        {
            setConnectionGridObject = true;
        }
    }
}
