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
    public partial class Form1 : Form
    {
        private const string camStream = @"http://frcvision.local:1181/stream.mjpg";
        private MJPEGStream stream;

        private UdpClient client;
        private IPAddress piAddress;
        private bool establishedConnection;
        private IPEndPoint piEndPoint;
        private bool runThreads = true;

        private byte[] buf = new byte[256];

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            stream = new MJPEGStream(camStream);
            stream.NewFrame += new NewFrameEventHandler(FinalVideoDevice_NewFrame);
            stream.Start();

            piAddress = IPAddress.Parse("10.0.0.156");
            client = new UdpClient();
            establishedConnection = false;
            piEndPoint = new IPEndPoint(IPAddress.Any, 5800);

            new System.Threading.Thread(ConnectThread).Start();
        }

        private void ConnectThread()
        {
            while (!establishedConnection && client != null && runThreads)
            {
                try
                {
                    client.Connect(piAddress, 5800);
                    client.Send(new byte[] { 0x33 }, 1);
                    byte[] ret = client.Receive(ref piEndPoint);
                    if (ret[0] == 0x77 && ret[1] == 0x62)
                        establishedConnection = true;
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
            stream.Stop();
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if(establishedConnection)
            {
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

                lblDist.Text = dist.ToString();
                lblAngle.Text = angle.ToString();
            }
        }
        private void UpdateUdpBuf()
        {
            buf = client.Receive(ref piEndPoint);
        }
    }
}
