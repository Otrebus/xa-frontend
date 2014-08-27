package se.neava.bytecodeuploader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.ParseException;

import jssc.SerialPortException;
import se.neava.Assembler.Assembler;
import se.neava.bytecodeuploader.BytecodeUploader;
import se.neava.bytecodeuploader.BytecodeUploader.BusyException;
import se.neava.bytecodeuploader.BytecodeUploader.NoPortsFoundException;
import se.neava.compiler.CompileException;
import se.neava.compiler.Compiler;

class UploadTestUi
{
    public static void main(String[] args) 
    {
        String text = "";
        File file = new File("input.g");
        System.out.println(file.getAbsolutePath());
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(file.toPath());
            text = new String(bytes,"UTF-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Compiler compiler = new Compiler();
        String code;
        try 
        {
            code = compiler.compile(text);
        } 
        catch (CompileException e1) 
        {
            // TODO Auto-generated catch block
            System.out.println("Compile error!");
            System.out.println(e1.what);
            return;
        }

        System.out.println(code);
        Assembler asm = new Assembler();
        try {
            bytes = asm.assemble(code);
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try 
        { 
            BytecodeUploader bu = new BytecodeUploader(null, 9600);
            bu.transmitCode(bytes, 5);
            while(true)
            {
                BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
                String line=buffer.readLine();
                if(line.equals("RESET"))
                    bu.sendReset();
                bu.transmitAppData(line.getBytes("UTF-8"));
            }
        } 
        catch (NoPortsFoundException | SerialPortException | BusyException | IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}