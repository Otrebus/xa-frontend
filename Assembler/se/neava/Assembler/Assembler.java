package se.neava.Assembler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;

public class Assembler {
    
    public Assembler()
    {
    }

    public static void main(String[] args) 
    {
        try
        {
            File file = new File("input.asm");
            System.out.println(file.getAbsolutePath());
            byte[] bytes = Files.readAllBytes(file.toPath());
            String text = new String(bytes,"UTF-8");

            new Assembler().assemble(text);
        } 
        catch (ParseException e) 
        {
            System.out.println("Parsing error: " + e.getMessage() + " (line " + e.getErrorOffset() + ")");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private boolean isEmpty(String line)
    {
         return line.trim().length() == 0;
    }

    private String removeComment(String line)
    {
        return line.split("--", 2)[0];
    }
    
    public byte[] assemble(String str) throws IOException, ParseException
    {
        BufferedReader rdr = new BufferedReader(new StringReader(str));
        List<String> strLines = new ArrayList<String>();
        for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
            line = removeComment(line);
            if(!isEmpty(line))
                strLines.add(line);
        }
        rdr.close();
        
        Program p = new Program();
        int lineNumber = 1;
        
        for(String line : strLines)
        {
            try
            {
                Lexer lex = new Lexer(line);
                Parser parser = new Parser(lex); 
                Statement s = parser.parse();
                if(s != null)
                    s.addToProgram(p);
            }
            catch(ParseException e)
            {
                System.out.println(lineNumber);
                throw new ParseException(e.getMessage(), lineNumber);
            }
            lineNumber++;
        }
        System.out.println("-------------");
        System.out.println(p);
        System.out.println(Program.bytesToString(p.getCode()));
        return p.getCode();
    }
}
