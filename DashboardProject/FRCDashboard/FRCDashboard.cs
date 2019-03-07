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
        private const string camStream = @"http://frcvision.local:1181/stream.mjpg";
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

        private UdpClient rioClient;
        private IPAddress rioAddress;
        private bool establishedConnectionWithRio;
        private IPEndPoint rioEndPoint;
        private int rioPort = 5801;
        private byte[] rioBuf = new byte[256];
        private object rioLockObject = new object();
        private bool attemptingToConnectToRio = false;

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

        }

        private void AttemptConnectPi()
        {
            string piStringAddress;
            try
            {
                piAddress = Dns.GetHostAddresses("frcvision.local")[0];
                piStringAddress = "Raspberry Pi Address: " + piAddress.ToString();
                raspberryPiClient = new UdpClient();
                raspberryPiClient.Client.SendTimeout = 1000;
                raspberryPiClient.Client.ReceiveTimeout = 1000;
                raspberryPiClient.Client.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveBuffer, 0);
                piEndPoint = new IPEndPoint(IPAddress.Any, piPort);

                new System.Threading.Thread(PiConnectThread).Start();
            }
            catch
            {
                piStringAddress = "Raspberry Pi Address: Could not resolve Pi DNS";
            }
            lblRaspPiAddress.Invoke((MethodInvoker)delegate
            {
                lblRaspPiAddress.Text = piStringAddress;
            });
            attemptingToConnectToPi = false;
        }

        private void AttemptConnectRio()
        {
            string rioStringAddress;
            try
            {
                rioAddress = Dns.GetHostAddresses("roborio-7762-frc.local")[0];
                rioStringAddress = "RoboRIO Address: " + rioAddress.ToString();
                rioClient = new UdpClient();
                rioClient.Client.SendTimeout = 1000;
                rioClient.Client.ReceiveTimeout = 1000;
                rioClient.Client.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveBuffer, 0);
                rioEndPoint = new IPEndPoint(IPAddress.Any, rioPort);

                new System.Threading.Thread(RioConnectThread).Start();
            }
            catch
            {
                rioStringAddress = "RoboRIO Address: Could not resolve Rio DNS";
            }
            lblRioAddress.Invoke((MethodInvoker)delegate
            {
                lblRioAddress.Text = rioStringAddress;
            });
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
                    catch { }
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

                    if (BitConverter.IsLittleEndian)
                    {
                        var ar = BitConverter.GetBytes(dist);
                        Array.Reverse(ar);
                        dist = BitConverter.ToDouble(ar, 0);

                        ar = BitConverter.GetBytes(angle);
                        Array.Reverse(ar);
                        angle = BitConverter.ToDouble(ar, 0);
                    }

                    lblTargetDistance.Text = "Target Distance: " + dist.ToString();
                    lblTargetAngle.Text = "Target Angle: " + angle.ToString();
                    lblCamPort.Text = "Camera Port: " + cameraStreamPort;
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
        }
    }
}
