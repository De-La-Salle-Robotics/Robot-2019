package frc.robot.hardware.pixy;

import com.ctre.phoenix.time.StopWatch;

public class Pixy2CCC
{
    private final int PIXY_BUFFERSIZE = 0x104;
    private final int PIXY_CHECKSUM_SYNC = 0xc1af;
    private final int PIXY_NO_CHECKSUM_SYNC = 0xc1ae;
    private final int PIXY_SEND_HEADER_SIZE = 4;
    private final int PIXY_MAX_PROGNAME = 33;

    private final int PIXY_TYPE_REQUEST_CHANGE_PROG = 0x02;
    private final int PIXY_TYPE_REQUEST_RESOLUTION = 0x0c;
    private final int PIXY_TYPE_RESPONSE_RESOLUTION = 0x0d;
    private final int PIXY_TYPE_REQUEST_VERSION = 0x0e;
    private final int PIXY_TYPE_RESPONSE_VERSION = 0x0f;
    private final int PIXY_TYPE_RESPONSE_RESULT = 0x01;
    private final int PIXY_TYPE_RESPONSE_ERROR = 0x03;
    private final int PIXY_TYPE_REQUEST_BRIGHTNESS = 0x10;
    private final int PIXY_TYPE_REQUEST_SERVO = 0x12;
    private final int PIXY_TYPE_REQUEST_LED = 0x14;
    private final int PIXY_TYPE_REQUEST_LAMP = 0x16;

    private final int PIXY_RESULT_OK = 0;
    private final int PIXY_RESULT_ERROR = -1;
    private final int PIXY_RESULT_BUSY = -2;
    private final int PIXY_RESULT_CHECKSUM_ERROR = -3;
    private final int PIXY_RESULT_TIMEOUT = -4;
    private final int PIXY_RESULT_BUTTON_OVERRIDE = -5;


    private final int CCC_MAX_SIGNATURE = 7;

    private final int CCC_RESPONSE_BLOCKS = 0x21;
    private final int CCC_REQUEST_BLOCKS = 0x20;

    // Defines for sigmap:
    // You can bitwise "or" these together to make a custom sigmap.
    // For example if you're only interested in receiving blocks
    // with signatures 1 and fiVe, you could use a sigmap of 
    // PIXY_SIG1 | PIXY_SIG5
    public static final byte CCC_SIG1 = 1;
    public static final byte CCC_SIG2 = 2;
    public static final byte CCC_SIG3 = 4;
    public static final byte CCC_SIG4 = 8;
    public static final byte CCC_SIG5 = 16;
    public static final byte CCC_SIG6 = 32;
    public static final byte CCC_SIG7 = 64;
    public static final byte CCC_COLOR_CODES = (byte)128;

    public static final byte CCC_SIG_ALL = (byte)0xff; // all bits or'ed together

    public class Block
    {
        // print block structure!
        public String toString()
        {
            int i;
            int d;
            boolean flag;
            if (m_signature > CCC_MAX_SIGNATURE) // color code! (CC)
            {
                String sig = "";
                // convert signature number to an octal String
                for (i = 12, flag = false; i >= 0; i -= 3)
                {
                    d = (int)((m_signature >> i) & 0x07);
                    if (d > 0 && !flag)
                        flag = true;
                    if (flag)
                        sig += d;
                }
                sig += '\0';
                return "CC-block signat: " + sig + " sig: " + m_signature + " x: " + m_x + " y: " + m_y + " width: " + m_width + " height: " + m_height + " angle: " + m_angle + " index: " + m_index + " age: " + m_age;
            }
            else // regular block.  Note, angle is always zero, so no need to print
                return "sig: " + m_signature + " x: " + m_x + " y: " + m_y + " width: " + m_width + " height: " + m_height + " angle: " + m_angle + " index: " + m_index + " age: " + m_age;
        }

        public int m_signature;
        public int m_x;
        public int m_y;
        public int m_width;
        public int m_height;
        public int m_angle;
        public int m_index;
        public int m_age;
    };

    private TPixy2 m_pixy;
    private int m_goodIndex;
    private Block m_goodBlock;
    private StopWatch m_timeSinceLastGoodBlock;

    public int numBlocks;
    public Block[] blocks;

    public Pixy2CCC(TPixy2 pixy)
    {
        m_pixy = pixy;
        blocks = new Block[64];
        m_timeSinceLastGoodBlock = new StopWatch();
    }

    public int parseBlocks()
    {
        return parseBlocks(CCC_SIG_ALL, (byte)0xFF);
    }

    public int parseBlocks(byte sigmap)
    {
        return parseBlocks(sigmap, (byte)0xFF);
    }

    public int parseBlocks(byte sigmap, byte maxBlocks)
    {
        numBlocks = 0;

        // fill in request data
        m_pixy.m_bufPayload[0] = sigmap;
        m_pixy.m_bufPayload[1] = maxBlocks;
        m_pixy.m_length = 2;
        m_pixy.m_type = CCC_REQUEST_BLOCKS;

        // send request
        m_pixy.SendPacket();
        if (m_pixy.recvPacket() == 0)
        {
            if (m_pixy.m_type == CCC_RESPONSE_BLOCKS)
            {
                for (int i = 0; i < blocks.length; i++) blocks[i] = null;

                for (int i = 0; i < m_pixy.m_length - 13; i += 14)
                {
                    blocks[numBlocks] = new Block();
                    blocks[numBlocks].m_signature = m_pixy.m_buf[0 + i];
                    blocks[numBlocks].m_signature |= (int)(m_pixy.m_buf[1 + i] << 8);
                    blocks[numBlocks].m_x = m_pixy.m_buf[2 + i];
                    blocks[numBlocks].m_x |= (int)(m_pixy.m_buf[3 + i] << 8);
                    blocks[numBlocks].m_y = m_pixy.m_buf[4 + i];
                    blocks[numBlocks].m_y |= (int)(m_pixy.m_buf[5 + i] << 8);
                    blocks[numBlocks].m_width = m_pixy.m_buf[6 + i];
                    blocks[numBlocks].m_width |= (int)(m_pixy.m_buf[7 + i] << 8);
                    blocks[numBlocks].m_height = m_pixy.m_buf[8 + i];
                    blocks[numBlocks].m_height |= (int)(m_pixy.m_buf[9 + i] << 8);
                    blocks[numBlocks].m_angle = m_pixy.m_buf[10 + i];
                    blocks[numBlocks].m_angle |= (int)(m_pixy.m_buf[11 + i] << 8);
                    blocks[numBlocks].m_index = m_pixy.m_buf[12 + i];
                    blocks[numBlocks].m_age = m_pixy.m_buf[13 + i];

                    if (blocks[numBlocks].m_x > 315 || blocks[numBlocks].m_y > 207 ||
                        blocks[numBlocks].m_width > 315 || blocks[numBlocks].m_height > 207 ||
                        blocks[numBlocks].m_angle > 180 || blocks[numBlocks].m_angle < -180)
                        blocks[numBlocks] = null;
                    else
                            numBlocks++;
                }
                return (int)numBlocks;
            }
            else if (m_pixy.m_type == PIXY_TYPE_RESPONSE_ERROR && m_pixy.m_buf[0] == ((int)PIXY_RESULT_BUSY))
            {
                return PIXY_RESULT_BUSY; // new data not available yet
            }
            else // some other error, return as-is
                return m_pixy.m_buf[0];
        }
        else
            return PIXY_RESULT_ERROR;  // some kind of bitstream error
    }
}