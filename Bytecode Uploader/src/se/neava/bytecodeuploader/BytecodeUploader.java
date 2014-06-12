package se.neava.bytecodeuploader;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortEventListener;
import jssc.SerialPortList;

public class BytecodeUploader 
{
    static final byte FRAME_DELIMITER = 0x7E;
    static final byte ESCAPE_OCTET = 0x7D;
    static final byte INITSEND_HEADER = 0x00;
    static final byte MORESEND_HEADER = 0x01;
    static final byte ACK_HEADER = 0x02;
    static final byte ECHO_HEADER = 0x03;
    
    boolean escaping = false;
    enum ReceiveState { Idle, ExpectingHeader, ExpectingAckSeqLsb, ExpectingAckSeqMsb, ExpectingAckDelim, ExpectingData };
    
    byte[] code;
    ReceiveState receiveState = ReceiveState.Idle;
    int sendPtr = 0;
    int ackSeq = 0;
    int chunkSize = 0;
    
    SerialPort serialPort;

    public BytecodeUploader(String portName, int baudRate) throws NoPortsFoundException, SerialPortException
    {
        // If the port name was null, we're just going to take the first best port (convenience functionality)
        if(portName == null)
        {
            String[] portNames = SerialPortList.getPortNames();
            if(portNames.length == 0)
                throw new NoPortsFoundException();
            portName = portNames[0];
        }
        serialPort = new SerialPort(portName);
        serialPort.openPort();
        serialPort.setParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, 
                             SerialPort.PARITY_NONE, false, false);
        serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
        serialPort.addEventListener(new SerialPortReader());
    }
    
    public void finalize()
    {
        try 
        {
            serialPort.closePort();
        } 
        catch (SerialPortException e) // Not much we can do
        {
            e.printStackTrace();
        }
    }
    
    public void transmitCode(byte[] code, int chunkSize) throws SerialPortException, BusyException
    {
        if(chunkSize < 1)
            throw new IllegalArgumentException("chunkSize must be a positive value.");
    
        this.code = code.clone();
        this.chunkSize = Math.min(chunkSize, code.length);
        byte init[] = { FRAME_DELIMITER, INITSEND_HEADER, (byte) (code.length & 0xFF), (byte) (code.length >>> 8)};
        byte crc[] = { 0, 0, 0, 0 }; // TODO: actually calculate CRC
        
        serialPort.writeBytes(init);        
        for(int i = 0; i < chunkSize; i++)
            serialPort.writeByte(code[i]);
        sendPtr = chunkSize;
        serialPort.writeBytes(crc);
        serialPort.writeByte(FRAME_DELIMITER);
    }
    
    public void transmitMore() throws SerialPortException
    {
        byte bytes[] = { FRAME_DELIMITER, MORESEND_HEADER };
        byte crc[] = { 0, 0, 0, 0 };
         
        serialPort.writeBytes(bytes);
        chunkSize = Math.min(chunkSize, code.length - sendPtr);
        for(int i = sendPtr; i < sendPtr + chunkSize; i++)
            serialPort.writeByte(code[i]);
        sendPtr += chunkSize;
        
        serialPort.writeBytes(crc);
        serialPort.writeByte(FRAME_DELIMITER);
    }
    
    private void handleAck() throws SerialPortException
    {
        System.out.println("got ack!");
        if(ackSeq >= code.length)
        {
            System.out.println("FINISHED TRANSMITTING :D \\o/");
            sendPtr = 0;
            return;
        }
        if(ackSeq == sendPtr)
        {
            transmitMore();
        }
    }
    
    private void handleReceivedByte(byte data) throws SerialPortException
    {
        System.out.println("Received " + (int) data);
        if(escaping)
        {
            data = (byte) (data ^ (1 << 5));
            escaping = false;
        }
        if(data == ESCAPE_OCTET)
        {
            if(receiveState != ReceiveState.Idle)
                escaping = true;
            return;
        }
        
        switch(receiveState)
        {
        case Idle:
            if(data == FRAME_DELIMITER)
                receiveState = ReceiveState.ExpectingHeader;
            else
                System.out.println("Received " + (char) data + " while idle.");
            break;
        case ExpectingHeader:
            if(data == ACK_HEADER)
                receiveState = ReceiveState.ExpectingAckSeqLsb;
            else if(data == ECHO_HEADER)
                receiveState = ReceiveState.ExpectingData;
            break;
        case ExpectingAckSeqLsb:
            ackSeq = data;
            receiveState = ReceiveState.ExpectingAckSeqMsb;
            break;
        case ExpectingAckSeqMsb:
            ackSeq |= (int)data << 8;
            receiveState = ReceiveState.ExpectingAckDelim;
            break;
        case ExpectingAckDelim:
            if(data == FRAME_DELIMITER)
            {
                receiveState = ReceiveState.Idle;
                handleAck();
            }
            else
                System.out.println("Expected ack end delimiter, got something else.");
            break;
        case ExpectingData:
            if(data == FRAME_DELIMITER)
            {
                System.out.println("");
                receiveState = ReceiveState.Idle;
                return;
            }
            else
                System.out.print(data);
            break;
        default:
            System.out.println("Bug!! Bad state!");
            break;
        }
    }
    
    private class SerialPortReader implements SerialPortEventListener 
    {        
        public void serialEvent(SerialPortEvent event) 
        {
            if(event.isRXCHAR()) // Received some bytes!
            {
                try 
                {    
                    byte data[] = serialPort.readBytes(event.getEventValue());
                    for(int i = 0; i < data.length; i++)
                        handleReceivedByte(data[i]);
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
    
    public class BusyException extends Exception
    {
        private static final long serialVersionUID = 9109563675205651338L;
    }
    
    public class NoPortsFoundException extends Exception {

        private static final long serialVersionUID = -6206865497981467014L;
    }
}

