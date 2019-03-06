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
        private bool establishedConnection;
        private IPEndPoint piEndPoint;
        private bool runThreads = true;

        private byte[] buf = new byte[256];
        private bool gettingData = false;

        public FRCDashboard()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            piAddress = Dns.GetHostAddresses("frcvision.local")[0];
            lblRaspPiAddress.Text = "Raspberry Pi Address: " + piAddress.ToString();
            raspberryPiClient = new UdpClient();
            raspberryPiClient.Client.SendTimeout = 10;
            raspberryPiClient.Client.ReceiveTimeout = 10; /* Very short timeouts to make sure we get the latest data quickly */
            establishedConnection = false;
            piEndPoint = new IPEndPoint(IPAddress.Any, 5800);

            pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;

            new System.Threading.Thread(ConnectThread).Start();
        }

        private void ConnectThread()
        {
            while (!establishedConnection && raspberryPiClient != null && runThreads)
            {
                try
                {
                    raspberryPiClient.Connect(piAddress, 5800);
                    raspberryPiClient.Send(new byte[] { 0x33 }, 1);
                    byte[] ret = raspberryPiClient.Receive(ref piEndPoint);
                    if (ret[0] == 0x77 && ret[1] == 0x62)
                    {
                        cameraStreamPort = ret[2] | ((int)ret[3] << 8);
                        stream = new MJPEGStream("http://frcvision.local:" + cameraStreamPort + "/stream.mjpg");
                        stream.NewFrame += new NewFrameEventHandler(FinalVideoDevice_NewFrame);
                        stream.Start();

                        establishedConnection = true;
                    }
                }
                catch { }
            }
        }
        void FinalVideoDevice_NewFrame(object sender, NewFrameEventArgs e)
        {
            try
            {
                pictureBox1.Image = (Bitmap)e.Frame.Clone();
            }
            catch { }
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            runThreads = false;
            raspberryPiClient.Dispose();
            stream.Stop();
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if(establishedConnection)
            {
                if(!gettingData)
                    new System.Threading.Thread(UpdateUdpBuf).Start();
                
                double dist = BitConverter.ToDouble(buf, 0);
                double angle = BitConverter.ToDouble(buf, 8);

                if(BitConverter.IsLittleEndian)
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
            }
        }
        private void UpdateUdpBuf()
        {
            gettingData = true;
            try
            {
                while(true)
                    buf = raspberryPiClient.Receive(ref piEndPoint);
            }
            catch { }
            gettingData = false;
        }
    }
}
