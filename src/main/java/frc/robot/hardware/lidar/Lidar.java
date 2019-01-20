package frc.robot.hardware.lidar;

import edu.wpi.first.wpilibj.SerialPort;

public class Lidar {
    private final int BAUD_RATE = 115200;
    private final int DATA_BITS = 8;
    private final SerialPort.StopBits STOP_BITS = SerialPort.StopBits.kOne;
    private final SerialPort.Parity PARITY_BITS = SerialPort.Parity.kNone;

    private final byte HEADER_1 = 0x59;
    private final byte HEADER_2 = 0x59;
    
    private SerialPort _portReference;

    public int strength = 0;
    public int distance = 0;

    public Lidar(SerialPort.Port port)
    {
        _portReference = new SerialPort(BAUD_RATE, port, DATA_BITS, PARITY_BITS, STOP_BITS);
    }

    public void updateBuf()
    {
        byte[] buf = _portReference.read(_portReference.getBytesReceived());

        int state = 0;

        int tmpDistance = 0;
        int goodDistance = 0;
        int tmpStrength = 0;
        int goodStrength = 0;

        byte runningChecksum = 0;

        for(int i = 0; i < buf.length; i++)
        {
            switch(state)
            {
                case 0:
                    runningChecksum = 0;
                    if(buf[i] == HEADER_1)
                        state = 1;
                    runningChecksum += buf[i];
                    break;
                case 1:
                    if(buf[i] == HEADER_2)
                        state = 2;
                    else
                        state = 0;
                    runningChecksum += buf[i];
                    break;
                case 2:
                    tmpDistance = buf[i];
                    runningChecksum += buf[i];
                    state = 3;
                    break;
                case 3:
                    tmpDistance |= buf[i] << 8;
                    runningChecksum += buf[i];
                    state = 4;
                    break;
                case 4:
                    tmpStrength = buf[i];
                    runningChecksum += buf[i];
                    state = 5;
                    break;
                case 5:
                    tmpStrength |= buf[i] << 8;
                    runningChecksum += buf[i];
                    state = 6;
                    break;
                case 6:
                    runningChecksum += buf[i];
                    state = 7;
                    break;
                case 7:
                    runningChecksum += buf[i];
                    state = 8;
                    break;
                case 8:
                    // This is the checksum byte, it is all other bytes added together
                    // If subtracting this from running checksum = 0, we're good
                    runningChecksum -= buf[i];
                    state = 0;
                    break;
            }
            if(runningChecksum == 0)
            {
                goodDistance = tmpDistance;
                goodStrength = tmpStrength;
            }
        }

        distance = goodDistance;
        strength = goodStrength;
    }

}