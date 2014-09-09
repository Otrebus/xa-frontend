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

public class Assembler 
{
    
    /**
     * Constructor.
     */
    public Assembler()
    {
    }
    
    /**
     * Returns true if the given line is all whitespace.
     * @param line The string to check for whitespacedness.
     * @return True if the line was all whitespace, false if not.
     */
    private boolean isEmpty(String line)
    {
         return line.trim().length() == 0;
    }

    /**
     * Removes "--" and all subsequent characters from a string. If "--" is not a substring of the
     * provided string, this does nothing.
     * @param line Any string.
     * @return The argument string after having undergone the transformation described above.
     */
    private String removeComment(String line)
    {
        return line.split("--", 2)[0];
    }
    
    /**
     * Transforms the assembly code into byte code.
     * @param str The assembly code, verbatim.
     * @return A vector of bytes consisting of the corresponding byte code, if the program is
     *         correct.
     * @throws IOException Thrown if something went wrong in the I/O processing of the code.
     * @throws ParseException Thrown if the program is not a correct assembly program.
     */
    public byte[] assemble(String str) throws IOException, ParseException
    {
        BufferedReader rdr = new BufferedReader(new StringReader(str));
        List<String> strLines = new ArrayList<String>();
        
        // Remove all comments and empty lines
        for (String line = rdr.readLine(); line != null; line = rdr.readLine()) 
        {
            line = removeComment(line);
            if(!isEmpty(line))
                strLines.add(line);
        }
        rdr.close();
        
        Program p = new Program();
        int lineNumber = 1;
        
        // Go through each line from top to bottom and construct byte codes
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
        
        // Forward references need to be fixed after the above step
        p.fixErrata();
        System.out.println("-------------");
        System.out.println(p);
        System.out.println(Program.bytesToString(p.getCode()));
        return p.getCode();
    }
}
