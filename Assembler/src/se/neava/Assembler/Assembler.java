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

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            File file = new File("input.asm");
            byte[] bytes = Files.readAllBytes(file.toPath());
            String text = new String(bytes,"UTF-8");

            try {
                new Assembler().assemble(text);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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
    
    public byte[] assemble(String str) throws IOException
    {
        BufferedReader rdr = new BufferedReader(new StringReader(str));
        List<String> strLines = new ArrayList<String>();
        for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
            line = removeComment(line);
            if(!isEmpty(line))
                strLines.add(line);
        }
        rdr.close();
        
        for(String line : strLines)
        {
            //System.out.println(line);
            Lexer lex = new Lexer(line);
            Parser parser = new Parser(lex); 
            try {
                if(parser.parse() != null)
                    System.out.println(line);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

  /*          for(Token tok = lex.nextToken(); tok.type != Token.Type.END; tok = lex.nextToken())
            {
                if(tok.type == Token.Type.INVALID)
                {
                    System.out.println("INVALID");
                    break;
                }
                System.out.println(tok);
            }*/
        }
        
        return new byte[0];
    }
}
