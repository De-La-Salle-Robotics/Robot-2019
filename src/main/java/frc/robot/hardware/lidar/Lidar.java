package frc.robot.hardware.lidar;

import edu.wpi.first.wpilibj.SerialPort;

public class Lidar {
    /* Constants used for the TFMini Lidar */
    private final int BAUD_RATE = 115200;
    private final int DATA_BITS = 8;
    private final SerialPort.StopBits STOP_BITS = SerialPort.StopBits.kOne;
    private final SerialPort.Parity PARITY_BITS = SerialPort.Parity.kNone;

    private final byte HEADER = 0x59;
    
    /* Reference to Serial Port */
    private SerialPort _portReference;

    /* Size of buffer */
    private final int BUFFER_SIZE = 1024; //There's ~300 bytes every time we get called, we should make sure the buffer can hold that many bytes

    /* Variables to operate the Lidar faster */
    private byte[] _buf;
    private int _endPointer;

    /* Variables we're concerned with */
    private short _strength = 0;
    public short getStrength() { return _strength; }
    private short _distance = 0;
    public short getDistance(){ return _distance; }

    /* Constructor for LIDAR, has to specify a Serial Port */
    public Lidar(SerialPort.Port port)
    {
        _portReference = new SerialPort(BAUD_RATE, port, DATA_BITS, PARITY_BITS, STOP_BITS);
        _buf = new byte[BUFFER_SIZE];
        _endPointer = 0;
    }

    /* Local method used to get the size of the buffer */
    private int getBufSize()
    {
        return _endPointer;
    }

    /* Local method to fill buffer with serial port buffer */
    private void updateBuf()
    {
        /* NOTE: We don't keep track of overflows with this method */
        
        int _endPointer = _portReference.getBytesReceived();
        /* Create temporary buffer with serial values */
        byte[] buf = _portReference.read(_endPointer);
        /* Fill local buffer with serial buffer */
        System.arraycopy(buf, 0, _buf, 0, _endPointer);
    }

    /* Public method to update the distance and strength values */
    public void updateValues()
    {
        /* First we update the buffer to make sure we have latest values */
        updateBuf();
        /* If it's not large enough to hold data, return right away */
        if(getBufSize() < 9) return;

        /* Create local variable to handle where we are in the buffer */
        int tmpStart = 0;
        if(getBufSize() > 17)
        {
            tmpStart = _endPointer - 18;
        }
        if(tmpStart < 0) tmpStart += BUFFER_SIZE;

        /* Move buffer pointer to the last point so we know we have latest data */
        while(_buf[tmpStart] != HEADER && _buf[tmpStart + 1] != HEADER)
        {
            tmpStart++;
        }

        /* Temporary variables in case checksum fails */
        byte checksum = 0;
        short tmpDis = 0;
        short tmpStr = 0;
        /* Parse data */
        for(int i = 0; i < 9; i++)
        {
            checksum += _buf[tmpStart];
            if(i == 2) tmpDis = _buf[tmpStart];
            if(i == 3) tmpDis |= ((short)_buf[tmpStart] << 8);
            if(i == 4) tmpStr = _buf[tmpStart];
            if(i == 5) tmpStr |= ((short)_buf[tmpStart] << 8);
            if(i == 8) checksum -= (_buf[tmpStart] * 2);

            tmpStart++;
        }
        /* If checksum works out, we can update public variables */
        if(checksum == 0)
        {
            _distance = tmpDis;
            _strength = tmpStr;
        }
    }

}