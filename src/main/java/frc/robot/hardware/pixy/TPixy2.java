package frc.robot.hardware.pixy;

public class TPixy2
    {
        public final int PIXY_BUFFERSIZE = 0x104;
        public final int PIXY_CHECKSUM_SYNC = 0xc1af;
        public final int PIXY_NO_CHECKSUM_SYNC = 0xc1ae;
        public final int PIXY_SEND_HEADER_SIZE = 4;
        public final int PIXY_MAX_PROGNAME = 33;

        public final int PIXY_TYPE_REQUEST_CHANGE_PROG = 0x02;
        public final int PIXY_TYPE_REQUEST_RESOLUTION = 0x0c;
        public final int PIXY_TYPE_RESPONSE_RESOLUTION = 0x0d;
        public final int PIXY_TYPE_REQUEST_VERSION = 0x0e;
        public final int PIXY_TYPE_RESPONSE_VERSION = 0x0f;
        public final int PIXY_TYPE_RESPONSE_RESULT = 0x01;
        public final int PIXY_TYPE_RESPONSE_ERROR = 0x03;
        public final int PIXY_TYPE_REQUEST_BRIGHTNESS = 0x10;
        public final int PIXY_TYPE_REQUEST_SERVO = 0x12;
        public final int PIXY_TYPE_REQUEST_LED = 0x14;
        public final int PIXY_TYPE_REQUEST_LAMP = 0x16;

        public final int PIXY_RESULT_OK = 0;
        public final int PIXY_RESULT_ERROR = -1;
        public final int PIXY_RESULT_BUSY = -2;
        public final int PIXY_RESULT_CHECKSUM_ERROR = -3;
        public final int PIXY_RESULT_TIMEOUT = -4;
        public final int PIXY_RESULT_BUTTON_OVERRIDE = -5;


        public class Version
        {
            public String toString()
            {
                return "hardware ver: " + hardware + " firmware ver: " + firmwareMajor + "." + firmwareMinor + "." + firmwareBuild + " " + firmwareType;
            }

            public int hardware;
            public int firmwareMajor;
            public int firmwareMinor;
            public int firmwareBuild;
            public String firmwareType;
        };


        public Pixy2CCC ccc;
        public int frameWidth;
        public int frameHeight;
        public Version version;
        //public Pixy2Line line;
        public LinkType m_link;

        public byte[] m_buf;
        public int[] m_bufPayload;
        public byte m_type;
        public byte m_length;
        public boolean m_cs;

        public TPixy2()
        {
            ccc = new Pixy2CCC(this);
            //line = new Pixy2Line(this);

            // allocate buffer space for send/receive
            m_buf = new byte[PIXY_BUFFERSIZE];
            m_bufPayload = new int[PIXY_BUFFERSIZE - PIXY_SEND_HEADER_SIZE];
            // shifted buffer is used for sending, so we have space to write header information
            System.arraycopy(m_buf, PIXY_SEND_HEADER_SIZE, m_bufPayload, 0, m_bufPayload.length);

            frameWidth = frameHeight = 0;
            version = new Version();
        }

        private long millis()
        {
            return System.currentTimeMillis();
        }

        public int Init(edu.wpi.first.wpilibj.I2C portDef)
        {
            long t0;
            int res;

            res = m_link.open(portDef);
            if (res < 0)
                return res;

            // wait for pixy to be ready -- that is, Pixy takes a second or 2 boot up
            // getVersion is an effective "ping".  We timeout after 5s.
            for (t0 = millis(); millis() - t0 < 5000;)
            {
                if (GetVersion() >= 0) // successful version get -> pixy is ready
                {
                    GetResolution(); // get resolution so we have it
                    return PIXY_RESULT_OK;
                }
            }
            // timeout
            return PIXY_RESULT_TIMEOUT;
        }


        public int GetSync()
        {
            int i, j, cprev;
            byte[] c = new byte[1];
            int res;
            int start;

            // parse ints until we find sync
            for (i = j = 0, cprev = 0; true; i++)
            {
                res = m_link.recv(c, 1);
                if (res >= PIXY_RESULT_OK)
                {
                    // since we're using little endian, previous int is least significant int
                    start = cprev;
                    // current int is most significant int
                    start |= ((int)c[0] << 8);
                    cprev = c[0];
                    if (start == PIXY_CHECKSUM_SYNC)
                    {
                        m_cs = true;
                        return PIXY_RESULT_OK;
                    }
                    if (start == PIXY_NO_CHECKSUM_SYNC)
                    {
                        m_cs = false;
                        return PIXY_RESULT_OK;
                    }
                }
                // If we've read some ints and no sync, then wait and try again.
                // And do that several more times before we give up.  
                // Pixy guarantees to respond within 100us.
                if (i >= 4)
                {
                    if (j >= 4)
                    {
                        return PIXY_RESULT_ERROR;
                    }
                    j++;
                    i = 0;
                }
            }
        }


        public int recvPacket()
        {
            int csCalc, csSerial;
            int res;
            csCalc = csSerial = 0;
            res = GetSync();
            if (res < 0)
                return res;

            if (m_cs)
            {
                res = m_link.recv(m_buf, 4);
                if (res < 0)
                    return res;

                m_type = m_buf[0];
                m_length = m_buf[1];

                csSerial = m_buf[2];
                csSerial |= (int)(m_buf[3] << 8);

                res = m_link.recv(m_buf, m_length);
                csCalc = m_link.getChecksum();
                if (res < 0)
                    return res;

                if (csSerial != csCalc)
                {
                    return PIXY_RESULT_CHECKSUM_ERROR;
                }
            }
            else
            {
                res = m_link.recv(m_buf, 2);
                if (res < 0)
                    return res;

                m_type = m_buf[0];
                m_length = m_buf[1];

                res = m_link.recv(m_buf, m_length);
                if (res < 0)
                    return res;
            }
            return PIXY_RESULT_OK;
        }


        public int SendPacket()
        {
            // write header info at beginnig of buffer
            m_buf[0] = (byte)(PIXY_NO_CHECKSUM_SYNC & 0xff);
            m_buf[1] = (byte)(PIXY_NO_CHECKSUM_SYNC >> 8);
            m_buf[2] = m_type;
            m_buf[3] = m_length;
            System.arraycopy(m_bufPayload, 0, m_buf, 4, m_length);
            // send whole thing -- header and data in one call
            return m_link.send(m_buf, (int)(m_length + PIXY_SEND_HEADER_SIZE));
        }


        public int ChangeProg(char[] prog)
        {
            int res;

            // poll for program to change
            while (true)
            {
                System.arraycopy(m_bufPayload, 0, prog, 0, PIXY_MAX_PROGNAME);
                m_length = PIXY_MAX_PROGNAME;
                m_type = PIXY_TYPE_REQUEST_CHANGE_PROG;
                SendPacket();
                if (recvPacket() == 0)
                {
                    res = m_buf[0];
                    res |= m_buf[1] << 8;
                    res |= m_buf[2] << 16;
                    res |= m_buf[3] << 24;
                    if (res > 0)
                    {
                        GetResolution();  // get resolution so we have it
                        return PIXY_RESULT_OK; // success     
                    }
                }
                else
                    return PIXY_RESULT_ERROR;  // some kind of bitstream error
            }
        }


        public int GetVersion()
        {
            m_length = 0;
            m_type = PIXY_TYPE_REQUEST_VERSION;
            SendPacket();
            if (recvPacket() == 0)
            {
                if (m_type == PIXY_TYPE_RESPONSE_VERSION)
                {
                    version.hardware = m_buf[0];
                    version.hardware = (int)m_buf[1] << 8;
                    version.firmwareMajor = m_buf[2];
                    version.firmwareMinor = m_buf[3];
                    version.firmwareBuild = m_buf[4];
                    String firmwareTypeString = "";
                    for (int i = 0; i < 10; i++)
                        firmwareTypeString += (char)m_buf[i + 5];
                    version.firmwareType = firmwareTypeString;
                    return m_length;
                }
                else if (m_type == PIXY_TYPE_RESPONSE_ERROR)
                    return PIXY_RESULT_BUSY;
            }

            return PIXY_RESULT_ERROR;  // some kind of bitstream error
        }


        public int GetResolution()
        {
            m_length = 1;
            m_bufPayload[0] = 0; // for future types of queries
            m_type = PIXY_TYPE_REQUEST_RESOLUTION;
            SendPacket();
            if (recvPacket() == 0)
            {
                if (m_type == PIXY_TYPE_RESPONSE_RESOLUTION)
                {
                    frameWidth = m_buf[0];
                    frameWidth |= (int)m_buf[1] << 8;
                    frameHeight = m_buf[2];
                    frameHeight |= (int)m_buf[3] << 8;
                    return PIXY_RESULT_OK; // success
                }
                else
                    return PIXY_RESULT_ERROR;
            }
            else
                return PIXY_RESULT_ERROR;  // some kind of bitstream error
        }


        public int SetCameraBrightness(int brightness)
        {
            long res;

            m_bufPayload[0] = brightness;
            m_length = 1;
            m_type = PIXY_TYPE_REQUEST_BRIGHTNESS;
            SendPacket();
            if (recvPacket() == 0) // && m_type==PIXY_TYPE_RESPONSE_RESULT && m_length==4)
            {
                res = m_buf[0];
                res |= (long)m_buf[1] << 8;
                res |= (long)m_buf[2] << 16;
                res |= (long)m_buf[3] << 24;
                return (int)res;
            }
            else
                return PIXY_RESULT_ERROR;  // some kind of bitstream error
        }


        public int SetServos(int s0, int s1)
        {
            long res;

            m_bufPayload[0] = (int)(s0 & 0xFF);
            m_bufPayload[1] = (int)((s0 >> 8) & 0xFF);
            m_bufPayload[2] = (int)(s1 & 0xFF);
            m_bufPayload[3] = (int)((s1 >> 8) & 0xFF);
            m_length = 4;
            m_type = PIXY_TYPE_REQUEST_SERVO;
            SendPacket();
            if (recvPacket() == 0 && m_type == PIXY_TYPE_RESPONSE_RESULT && m_length == 4)
            {
                res = m_buf[0];
                res |= (long)m_buf[1] << 8;
                res |= (long)m_buf[2] << 16;
                res |= (long)m_buf[3] << 24;
                return (int)res;
            }
            else
                return PIXY_RESULT_ERROR;  // some kind of bitstream error	  
        }


        public int SetLED(int r, int g, int b)
        {
            long res;

            m_bufPayload[0] = r;
            m_bufPayload[1] = g;
            m_bufPayload[2] = b;
            m_length = 3;
            m_type = PIXY_TYPE_REQUEST_LED;
            SendPacket();
            if (recvPacket() == 0 && m_type == PIXY_TYPE_RESPONSE_RESULT && m_length == 4)
            {
                res = m_buf[0];
                res |= (long)m_buf[1] << 8;
                res |= (long)m_buf[2] << 16;
                res |= (long)m_buf[3] << 24;
                return (int)res;
            }
            else
                return PIXY_RESULT_ERROR;  // some kind of bitstream error
        }

        public int SetLamp(int upper, int lower)
        {
            long res;

            m_bufPayload[0] = upper;
            m_bufPayload[1] = lower;
            m_length = 2;
            m_type = PIXY_TYPE_REQUEST_LAMP;
            SendPacket();
            if (recvPacket() == 0 && m_type == PIXY_TYPE_RESPONSE_RESULT && m_length == 4)
            {
                res = m_buf[0];
                res |= (long)m_buf[1] << 8;
                res |= (long)m_buf[2] << 16;
                res |= (long)m_buf[3] << 24;
                return (int)res;
            }
            else
                return PIXY_RESULT_ERROR;  // some kind of bitstream error	
        }
    }