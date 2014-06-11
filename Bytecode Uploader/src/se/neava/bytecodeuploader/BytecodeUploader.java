package se.neava.bytecodeuploader;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortEventListener;

public class BytecodeUploader {
    
    static SerialPort serialPort;

    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {

        try {
            serialPort = new SerialPort("COM4");

            serialPort.openPort();//Open serial port
            serialPort.setParams(SerialPort.BAUDRATE_9600, 
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE, false, false);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
            int mask = SerialPort.MASK_RXCHAR | SerialPort.MASK_CTS | SerialPort.MASK_DSR; //Prepare mask
            serialPort.setEventsMask(mask); //Set mask
            serialPort.addEventListener(new SerialPortReader());
            System.out.println("Writing...");

            //serialPort.writeBytes("~xtest~".getBytes("UTF-8"));//Write data to port
            serialPort.writeBytes("YOYOYOYO~".getBytes("UTF-8"));
            while(true);
            //serialPort.closePort();//Close serial port
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    static class SerialPortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {

            System.out.println("got something!");
            if(event.isRXCHAR())
            {//If data is available
                try {
                    byte buffer[] = serialPort.readBytes(event.getEventValue());
                    if(buffer.length > 0 && buffer[0] == '~')
                    {
                        serialPort.closePort();
                        System.exit(0);
                    }
                    String blah = new String(buffer);
                    System.out.print(blah);
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
}

