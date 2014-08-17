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
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            text = new String(bytes,"UTF-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Compiler compiler = new Compiler();
        String code = compiler.compile(text);
        
        if(compiler.error())
        {
            System.out.println("Compile error(s)!");
            System.out.println(compiler.dumpErrors());
        }
        else
            System.out.println(code);
    }
}
