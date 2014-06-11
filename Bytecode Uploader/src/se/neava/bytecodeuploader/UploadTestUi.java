package se.neava.bytecodeuploader;

import java.io.UnsupportedEncodingException;

import jssc.SerialPortException;
import se.neava.bytecodeuploader.BytecodeUploader.BusyException;
import se.neava.bytecodeuploader.BytecodeUploader.NoPortsFoundException;

public class UploadTestUi {

    public static void main(String[] args) 
    {
        try 
        {
            BytecodeUploader bu = new BytecodeUploader(null, 9600);
            bu.transmitCode("abcdefghijklmnopqrstuvwxyz".getBytes("UTF8"), 5);
        } 
        catch (NoPortsFoundException | SerialPortException | UnsupportedEncodingException | BusyException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
