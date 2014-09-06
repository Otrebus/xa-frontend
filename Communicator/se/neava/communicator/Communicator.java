package se.neava.communicator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortEventListener;
import jssc.SerialPortList;

public class Communicator 
{
    static final private byte FRAME_DELIMITER = 0x7E;
    static final private byte ESCAPE_OCTET = 0x7D;
    static final private byte INITSEND_HEADER = 0x0A;
    static final private byte MORESEND_HEADER = 0x0B;
    static final private byte ACK_HEADER = 0x0C;
    static final private byte RESET_HEADER = 0x0D;
    
    private Timer codeTimeoutTimer = new Timer();
    private TimerTask codeTimeoutTask;
    
    private boolean debugOutput = false;
    private boolean escaping = false;
    private boolean receiving = false;

    private Vector<Byte> recvBuf = new Vector<Byte>();
    private byte[] code;
    private int sendPtr = 0;
    private int chunkSize = 0;
    
    private SerialPort serialPort;
    
    CommunicationEventHandler handler = null;

    public Communicator(String portName, int baudRate) throws NoPortsFoundException, SerialPortException
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
    
    public void setDebugOutput(boolean b)
    {
        debugOutput = b;
    }
    
    private void debugOutput(String str)
    {
        if(debugOutput)
            System.out.print(str);
    }
    
    private void debugOutputln(String str)
    {
        if(debugOutput)
            System.out.println(str);
    }
    
    void setEventHandler(CommunicationEventHandler handler)
    {
        this.handler = handler;
    }
    
    public synchronized void transmitCode(byte[] code, int chunkSize) throws SerialPortException, BusyException
    {
        if(chunkSize < 1)
            throw new IllegalArgumentException("chunkSize must be a positive value.");
        
        if(code.length > (1 << 15))
            throw new IllegalArgumentException("Code is too long.");
    
        this.code = code.clone();
        this.chunkSize = Math.min(chunkSize, code.length);
        ByteBuffer buffer = ByteBuffer.allocate(chunkSize + 9);

        // Message structure is [delimiter | header | codelength | code | crc | delimiter]
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(FRAME_DELIMITER);
        buffer.put(INITSEND_HEADER);
        buffer.putShort((short) code.length);

        for(int i = 0; i < chunkSize; i++)
            buffer.put(code[i]);
        int checksum = 0;
        for(int i = 1; i < chunkSize + 5; i++)
            checksum = addToChecksum(checksum, buffer.get(i));
        buffer.putInt((checksum & 0xFFFF));
        buffer.put(FRAME_DELIMITER);
        
        serialPort.writeByte(buffer.get(0));
        serialPort.writeByte(buffer.get(1));
        for(int i = 2; i < buffer.capacity() - 1; i++)
            writeCheckedByte(buffer.get(i));
        serialPort.writeByte(buffer.get(buffer.capacity() - 1));
        startTimeout();
    }
    
    public void finalize()
    {
        try 
        {
            serialPort.closePort();
        } 
        catch (SerialPortException e)
        {
            e.printStackTrace();
        }
    }
    
    private void retransmitCode() throws SerialPortException, BusyException
    {
        debugOutput("r");
        if(sendPtr == 0)
            transmitCode(code, chunkSize);
        else transmitMore();
        if(handler != null)
            handler.handleEvent(new CommunicationEvent(CommunicationEvent.RETRANSMITTED));
    }
    
    private int addToChecksum(int oldChecksum, byte b)
    {
        return oldChecksum + (((int) b) & 0xFF);
    }
    
    private void writeCheckedBytes(byte[] bytes) throws SerialPortException
    {
        for(byte b : bytes)
            writeCheckedByte(b);
    }
    
    private void writeCheckedByte(byte b) throws SerialPortException
    {
        if(b == FRAME_DELIMITER || b == ESCAPE_OCTET)
        {
            serialPort.writeByte(ESCAPE_OCTET);
            int convert = (b ^ (1 << 5)) & 0xFF;
            serialPort.writeByte((byte) convert);
        }
        else
            serialPort.writeByte(b);
    }
    
    private void transmitMore() throws SerialPortException
    {
        this.code = code.clone();
        this.chunkSize = Math.min(chunkSize, code.length - sendPtr);
        ByteBuffer buffer = ByteBuffer.allocate(chunkSize + 9);

        // Message structure is [delimiter | header | seq | code | crc | delimiter]
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(FRAME_DELIMITER);
        buffer.put(MORESEND_HEADER);
        buffer.putShort((short) sendPtr);
        
        for(int i = sendPtr; i < sendPtr + chunkSize; i++)
            buffer.put(code[i]);
        int checksum = 0;
        for(int i = 1; i < chunkSize + 5; i++)
            checksum = addToChecksum(checksum, buffer.get(i));
        buffer.putInt((checksum & 0xFFFF));
        buffer.put(FRAME_DELIMITER);
        
        serialPort.writeByte(buffer.get(0));
        serialPort.writeByte(buffer.get(1));
        for(int i = 2; i < buffer.capacity() - 1; i++)
            writeCheckedByte(buffer.get(i));
        serialPort.writeByte(buffer.get(buffer.capacity() - 1));        
        
        resetTimeout();
    }

    private void handleAckFrame() throws SerialPortException
    {
        // Next four lines could be done in one in a more functional language
        byte[] bytes = new byte[recvBuf.size()];
        for(int i = 0; i < recvBuf.size(); i++)
            bytes[i] = recvBuf.elementAt(i).byteValue();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        int checksum = 0;
        for(int i = 0; i < 3; i++)
            checksum = addToChecksum(checksum, buffer.get(i));
        int receivedChecksum = buffer.getInt(3);
        
        int ackSeq = buffer.getShort(1);
        
        if(checksum != receivedChecksum)
        {
            debugOutputln("Bad ack checksum!");
            return;
        }
        if(ackSeq > sendPtr + chunkSize)
        {
            debugOutputln("Too high ack seq!");
            return;
        }
        
        sendPtr = ackSeq;
        if(ackSeq >= code.length)
        {
            debugOutputln("Finished transmitting!");
            if(handler != null)
                handler.handleEvent(new CommunicationEvent(CommunicationEvent.FINISHED_UPLOADING));
            sendPtr = 0;
            cancelTimeout();
            return;
        }
        debugOutputln("Received ack, seq: " + ackSeq);
        transmitMore();
    }
    
    private void handleAppFrame()
    {
        byte[] bytes = new byte[recvBuf.size()];
        for(int i = 0; i < recvBuf.size(); i++)
            bytes[i] = recvBuf.elementAt(i).byteValue();
        if(handler != null)
            handler.handleEvent(new CommunicationEvent(CommunicationEvent.MESSAGE, bytes));
    }
    
    private void handleReceivedByte(byte data) throws SerialPortException
    {
        debugOutputln("Received " + String.format("%02X ", data));
        if(data == ESCAPE_OCTET)
        {
            escaping = true;
            return;
        }
        if(escaping)
            data = (byte) (data ^ (1 << 5));

        debugOutputln(" (" + data + ")" + 
                       ((receiving || data == FRAME_DELIMITER) ? "" : " (outside frame delimiters!)"));
        
        if(data == FRAME_DELIMITER && !escaping)
        {
            if(!receiving)
            {
                receiving = true;
                return;
            }
            
            switch(recvBuf.get(0).byteValue())
            {
            case ACK_HEADER:
                handleAckFrame();
                break;
            default:
                handleAppFrame();
                break;
            }
            recvBuf.clear();
            receiving = false;
        }
        else
            recvBuf.add(data);
        
        if(escaping)
            escaping = false;
    }
    
    public void transmitAppData(byte[] code) throws SerialPortException
    {
        serialPort.writeByte(FRAME_DELIMITER);
        serialPort.writeByte((byte) 0x00);
        writeCheckedBytes(code);
        serialPort.writeByte(FRAME_DELIMITER);
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

    public void sendReset() throws SerialPortException 
    {
        serialPort.writeByte(FRAME_DELIMITER);
        serialPort.writeByte((byte) RESET_HEADER);
        serialPort.writeByte(FRAME_DELIMITER);
    }
    
    private void startTimeout()
    {
        codeTimeoutTask = 
        new TimerTask() { 
            public void run() { 
                try {
                    retransmitCode();
                } 
                catch (SerialPortException | BusyException e) {
                    e.printStackTrace();
                } 
            } 
        };
        codeTimeoutTimer.schedule(codeTimeoutTask, 50);
    }
    
    private void resetTimeout()
    {
        codeTimeoutTask.cancel();
        codeTimeoutTask = 
        new TimerTask() { 
            public void run() { 
                try {
                    retransmitCode();
                } 
                catch (SerialPortException | BusyException e) {
                    e.printStackTrace();
                } 
            } 
        };
        codeTimeoutTimer.schedule(codeTimeoutTask, 50);
    }
    
    private void cancelTimeout()
    {
        codeTimeoutTimer.cancel();
    }
}

