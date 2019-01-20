package frc.robot.hardware.pixy;

public class LinkI2C implements LinkType
{
    public final int PIXY_I2C_DEFAULT_ADDR = 0x54;
    public final int PIXY_I2C_MAX_SEND = 16;

    private edu.wpi.first.wpilibj.I2C m_I2CPort;

    private final int bufSize = 128;
    private byte[] m_localBuf;
    private int start;
    private int end;
    public boolean bufferOverflow = false;
    private int checkSum = 0;

    public int getChecksum() { return checkSum; }

    public void clearBuffer()
    {
        start = end = 0;
        bufferOverflow = false;
    }
    public int bufferSize() { return (int)(end - start); }

    public int open(edu.wpi.first.wpilibj.I2C portDef)
    {

        m_I2CPort = portDef;

        m_localBuf = new byte[bufSize];
        start = end = 0;
        return 0;
    }

    public void close()
    {
        //Nothing
    }

    private void updateBuffer()
    {
        byte[] tmpBuf = new byte[1];
        while(m_I2CPort.read(PIXY_I2C_DEFAULT_ADDR, 1, tmpBuf))
        {
            m_localBuf[start++] = tmpBuf[0];
        }
    }

    public int recv(byte[] buf, int len)
    {
        checkSum = 0;
        updateBuffer(); //Update buffer manually b/c wpi i2c implementation has no callback method for new data
        if (bufferSize() >= len)
        {
            if (start + len < bufSize)
                System.arraycopy(m_localBuf, start, buf, 0, len);
            else
            {
                System.arraycopy(m_localBuf, start, buf, 0, bufSize - 1 - start);
                System.arraycopy(m_localBuf, 0, buf, bufSize - 1 - start, len - (bufSize - 1 - start));
            }
            for (int i = 0; i < len; i++)
            {
                checkSum += buf[i];
            }
            start += len;
            if (start >= bufSize) start -= bufSize;
            return len;
        }
        else
            return -1;
    }

    public int send(byte[] buf, int len)
    {
        return send(buf, len, PIXY_I2C_DEFAULT_ADDR);
    }
    public int send(byte[] buf, int len, int address)
    {
        for(int i = 0; i < len && i < PIXY_I2C_MAX_SEND; i++)
            m_I2CPort.write(address, buf[i]);
        return len;
    }
}

    