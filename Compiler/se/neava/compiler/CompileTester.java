package se.neava.compiler;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.text.ParseException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import se.neava.Assembler.Assembler;
import se.neava.compiler.GravelParser.ProgramContext;
import se.neava.compiler.type.*;

public class CompileTester {

    public static void main(String[] args) 
    {
        String text = "";
        File file = new File("input.g");
        System.out.println(file.getAbsolutePath());
        byte[] bytes;
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
            asm.assemble(code);
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}