package se.neava.bytecodeuploader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;

import jssc.SerialPortException;
import se.neava.Assembler.Assembler;
import se.neava.bytecodeuploader.BytecodeUploader;
import se.neava.bytecodeuploader.BytecodeUploader.BusyException;
import se.neava.bytecodeuploader.BytecodeUploader.NoPortsFoundException;

class UploadTestUi
{
    public static void main(String[] args) 
    {
        File file = new File("input.asm");
        System.out.println(file.getAbsolutePath());
        try 
        { 
            byte[] bytes = Files.readAllBytes(file.toPath());
            String text = new String(bytes,"UTF-8");

            byte[] code = new Assembler().assemble(text);
            BytecodeUploader bu = new BytecodeUploader(null, 9600);
            bu.transmitCode(code, 5); // "abcdefghijklmnopqrstuvwxyz".getBytes("UTF8"), 5);
            //bu.transmitCode("abcdefghijklmnopqrstuvwxyz".getBytes("UTF8"), 5);
        } 
        catch (NoPortsFoundException | SerialPortException | BusyException | IOException | ParseException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}