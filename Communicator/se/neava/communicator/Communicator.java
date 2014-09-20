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

//import se.neava.communicator.CommunicationEvent;

/**
 * Opens and maintains a uart connection, which can send bytecode and app messages to another system
 * and also react to received app messages and other communication events. The bytecode transmission
 * uses ARQ for frame validation and the frame contents are loosely based on HDLC with asynchronous
 * framing.
 */
public class Communicator 
{
    // Message headers
    static final private byte FRAME_DELIMITER = 0x7E;
    static final private byte ESCAPE_OCTET = 0x7D;
    static final private byte INITSEND_HEADER = 0x0A;
    static final private byte MORESEND_HEADER = 0x0B;
    static final private byte ACK_HEADER = 0x0C;
    static final private byte RESET_HEADER = 0x0D;
    
    // Variables for the timeout functionality inherent in ARQ
    private Timer codeTimeoutTimer = new Timer();
    private TimerTask codeTimeoutTask;
    
    // Various state variables concerning communication
    private boolean debugOutput = false; // Enables debug output
    private boolean escaping = false;    // Indicates the last byte received was the escape octet
                                         // (see HDLC/asynchronous framing)
    private boolean receiving = false;   // Indicates we are currently reading a frame
                                         // (FRAME_DELIMITER was received once)
    private Vector<Byte> recvBuf = new Vector<Byte>(); // Reception buffer for incoming data
    private byte[] code;  // Space for program bytecode to be transmitted
    private int sendPtr = 0; // Next byte to transmit in above buffer
    private int chunkSize = 0; // Size of payload to be sent per frame
    
    private SerialPort serialPort; // jssc SerialPort for uart communication
    private CommunicationEventHandler handler = null; // Possibly provided message handler

    /**
     * Constructor. Opens a UART communication port with the given parameters, using 1 stop bit and
     * 8 data bits.
     * 
     * @param portName The name of the port, eg "COM3", or null to use the first
     *                 existing serial port.
     * @param baudRate The baud rate to use on the given port.
     * @throws NoPortsFoundException
     * @throws SerialPortException
     */
    public Communicator(String portName, int baudRate) 
            throws NoPortsFoundException, SerialPortException
    {
        if(portName == null)
        {
            String[] portNames = SerialPortList.getPortNames();
            if(portNames.length == 0)
                throw new NoPortsFoundException();
            portName = portNames[0];
        }

        // Set up and open a port with the provided info
        serialPort = new SerialPort(portName);
        serialPort.openPort();
        serialPort.setParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, 
                             SerialPort.PARITY_NONE, false, false);
        // Set up an event listener to listen for incoming data from the port
        serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
        serialPort.addEventListener(new SerialPortReader());
    }
    
    /**
     * Toggles debug output, which is printed to stdout.
     * @param enabled
     */
    public void setDebugOutput(boolean enabled)
    {
        debugOutput = enabled;
    }
    
    /**
     * Prints a string (without newline) to stdout if debug output is enabled, and does nothing if
     * debug output is disabled. 
     * @param str The string to print.
     */
    private void debugOutput(String str)
    {
        if(debugOutput)
            System.out.print(str);
    }

    /**
     * Prints a string (with newline) to stdout if debug output is enabled, and does nothing if
     * debug output is disabled. 
     * @param str The string to print.
     */    
    private void debugOutputln(String str)
    {
        if(debugOutput)
            System.out.println(str);
    }
    
    /**
     * Assigns an event handler that is notified when certain communication events occur.
     * @param handler The {@link CommunicationEvent} handler to assign.
     */
    public void setEventHandler(CommunicationEventHandler handler)
    {
        this.handler = handler;
    }
    
    /**
     * Transmits bytecode to program the device with. 
     * @param code The bytecode to send.
     * @param chunkSize The number of bytes to send with each frame. Must be in range [1, 255].
     * @throws SerialPortException
     * @throws BusyException
     */
    public synchronized void transmitCode(byte[] code, int chunkSize) 
            throws SerialPortException, BusyException
    {
        if(chunkSize < 1 || chunkSize > 255)
            throw new IllegalArgumentException("chunkSize must be between "
                                                + "values 1 and 255 inclusive.");
        if(code.length > (1 << 15))
            throw new IllegalArgumentException("Code is too long.");
    
        sendPtr = 0;
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
        buffer.putInt(checksum);
        buffer.put(FRAME_DELIMITER);
        
        // The delimiters are sent verbatim but the rest is sent unchecked
        serialPort.writeByte(buffer.get(0));
        for(int i = 1; i < buffer.capacity() - 1; i++)
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
    
    /**
     * Attempts to retransmit a chunk of code. This method is called by the timeout timer task
     * if the device failed to respond in time to a transmitted frame.
     * @throws SerialPortException
     * @throws BusyException
     */
    private void retransmitCode() throws SerialPortException, BusyException
    {
        debugOutput("r");
        if(sendPtr == 0)
            transmitCode(code, chunkSize);
        else transmitMore();
        handleEvent(new CommunicationEvent(CommunicationEvent.RETRANSMITTED));
    }
    
    /**
     * Computes a new checksum for bytes {b_0, .., b_n, b_{n+1}} given the checksum for series
     * {b_0, ..., b_n} and another byte b_{n+1}.
     * @param oldChecksum The old checksum.
     * @param b The given byte.
     * @throws SerialPortException
     * @throws BusyException
     */    
    private int addToChecksum(int oldChecksum, byte b)
    {
        return oldChecksum + (((int) b) & 0xFF);
    }
    
    /**
     * Transmits the provided series of bytes, also adding escape octets before any frame delimiter
     * or escape octet. 
     * @param bytes The byte series to be transmitted.
     * @throws SerialPortException
     * @see writeCheckedBytes
     */
    private void writeCheckedBytes(byte[] bytes) throws SerialPortException
    {
        for(byte b : bytes)
            writeCheckedByte(b);
    }

    /**
     * Transmits the provided byte, possibly also preceded by adding an escape octet if the byte to 
     * be sent is either the escape octet or a frame delimiter. 
     * @param bytes The byte series to be transmitted.
     * @throws SerialPortException
     * @see writeCheckedBytes
     */
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
    
    /**
     * Transmits another frame of code. This is called when an ack is received to an earlier frame.
     * @throws SerialPortException
     */
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
        buffer.putInt(checksum);
        buffer.put(FRAME_DELIMITER);
        
        serialPort.writeByte(buffer.get(0));
        serialPort.writeByte(buffer.get(1));
        for(int i = 2; i < buffer.capacity() - 1; i++)
            writeCheckedByte(buffer.get(i));
        serialPort.writeByte(buffer.get(buffer.capacity() - 1));        
        
        resetTimeout();
    }
    
    /**
     * Closes the connection.
     * @throws SerialPortException
     */
    public void close() throws SerialPortException
    {
        cancelTimeout();
        serialPort.closePort();
    }

    /**
     * Handles an incoming ack frame to a previously sent frame of bytecode.
     * @throws SerialPortException
     */
    private void handleAckFrame() throws SerialPortException
    {
        // Next four lines could be done in one in a more functional language
        byte[] bytes = new byte[recvBuf.size()];
        for(int i = 0; i < recvBuf.size(); i++)
            bytes[i] = recvBuf.elementAt(i).byteValue();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        // Message structure is [delimiter | header | seq | crc | delimiter]
        // but delimiters were stripped away before reaching recvBuf
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
        
        handleEvent(new CommunicationEvent(CommunicationEvent.GOT_ACK));
        
        sendPtr = ackSeq;
        if(ackSeq >= code.length)
        {
            debugOutputln("Finished transmitting!");
            handleEvent(new CommunicationEvent(CommunicationEvent.FINISHED_UPLOADING));
            sendPtr = 0;
            cancelTimeout();
            return;
        }
        
        debugOutputln("Received ack, seq: " + ackSeq);
        transmitMore();
    }
    
    /**
     * Handles an incoming message frame sent via uart.
     */
    private void handleAppFrame()
    {
        byte[] bytes = new byte[recvBuf.size()-1];
        for(int i = 1; i < recvBuf.size(); i++)
            bytes[i-1] = recvBuf.elementAt(i).byteValue();
        handleEvent(new CommunicationEvent(bytes));
    }

    /**
     * Processes a single received byte from the port. 
     * @param data The received byte.
     * @throws SerialPortException
     */
    private void handleReceivedByte(byte data) throws SerialPortException
    {
        String debugStr = "Received " + String.format("%02X ", data);
        if(data == ESCAPE_OCTET)
        {
            escaping = true;
            return;
        }
        if(escaping)
            data = (byte) (data ^ (1 << 5));

        debugStr += (" (" + data + ")" + 
                       ((receiving || data == FRAME_DELIMITER) ? "" 
                               : " (outside frame delimiters!)"));
        debugOutputln(debugStr);
        
        // An actual frame delimiter was received (ie not preceded by an escape octet)
        if(data == FRAME_DELIMITER && !escaping)
        {
            // A new frame was started
            if(!receiving)
            {
                receiving = true;
                return;
            }
            
            // A frame ended, handle it according to its type which indicated by its first byte
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
        
        // Escaped byte was handled, handle next one normally
        if(escaping)
            escaping = false;
    }
    
    /**
     * Transmits a message to the application running on the remote end. The message is
     * automatically appended with frame delimiters and a frame header of 0x00.
     * @param code The code to be sent.
     * @throws SerialPortException
     */
    public void transmitAppData(byte[] code) throws SerialPortException
    {
        serialPort.writeByte(FRAME_DELIMITER);
        serialPort.writeByte((byte) 0x00);
        writeCheckedBytes(code);
        serialPort.writeByte(FRAME_DELIMITER);
    }
    
    /**
     * The event listener for the jssc API. The only use here is to listen for received bytes. 
     */
    private class SerialPortReader implements SerialPortEventListener 
    {        
        public void serialEvent(SerialPortEvent event) 
        {
            if(event.isRXCHAR()) // Received bytes
            {
                try 
                {    
                    byte data[] = serialPort.readBytes(event.getEventValue());
                    for(int i = 0; i < data.length; i++)
                        handleReceivedByte(data[i]);
                }
                catch (SerialPortException ex) 
                {
                    System.out.println(ex);
                }
            }
        }
    }
    
    /**
     * This exception is thrown if a port is busy, for example if it is already used by another 
     * application.
     */
    public class BusyException extends Exception
    {
        private static final long serialVersionUID = 9109563675205651338L;
    }
    
    /**
     * This exception is thrown if the constructor can not find any free ports, which it can be 
     * asked to do by specifying null as the port name.
     */
    public class NoPortsFoundException extends Exception 
    {
        private static final long serialVersionUID = -6206865497981467014L;
    }

    /**
     * Sends a reset frame to the device, which stops the given program and starts waiting for a new
     * one.
     * @throws SerialPortException
     */
    public void sendReset() throws SerialPortException 
    {
        serialPort.writeByte(FRAME_DELIMITER);
        serialPort.writeByte((byte) RESET_HEADER);
        serialPort.writeByte(FRAME_DELIMITER);
    }
    
    /**
     * Starts the timeout task. This is called after having sent a frame.
     */
    private void startTimeout()
    {
        codeTimeoutTimer = new Timer();
        codeTimeoutTask = 
        new TimerTask() 
        { 
            public void run() 
            { 
                try 
                {
                    retransmitCode();
                } 
                catch (SerialPortException | BusyException e) 
                {
                    e.printStackTrace();
                } 
            } 
        };
        codeTimeoutTimer.schedule(codeTimeoutTask, 50);
    }
    
    /**
     * Resets the timeout task. This is called after having received an ack.
     */
    private void resetTimeout()
    {
        codeTimeoutTask.cancel();
        codeTimeoutTask = 
        new TimerTask() 
        { 
            public void run() // This runs when the timer times out ..
            { 
                try 
                {
                    retransmitCode(); // .. and we then try to retransmit the last code frame
                } 
                catch (SerialPortException | BusyException e) 
                {
                    e.printStackTrace();
                } 
            } 
        };
        codeTimeoutTimer.schedule(codeTimeoutTask, 50);
    }
    
    /**
     * Cancels the timeout task. This is called after having successfully sent a full program.
     */
    private void cancelTimeout()
    {
        codeTimeoutTimer.cancel();
    }
    
    /**
     * Convenience method to let the provided event handler handle an event only if it exists. 
     * @param e The event to be handled.
     */
    private void handleEvent(CommunicationEvent e)
    {
        if(handler != null)
            handler.handleEvent(e);
    }
}

