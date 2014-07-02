package se.neava.Assembler;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Statement {
    static Pattern sectionPattern = Pattern.compile("^(\\s)*\\.section+(\\s)+[a-z_A-z0-9]+(\\s)*$");
    static Pattern labelPattern = Pattern.compile("^(\\s)*[a-z_A-z0-9]+:(\\s)*$");
    static Pattern dataPattern = Pattern.compile("^(\\s)*[\".*\" [byte] [word] [dword]](\\s).*$");
    static Pattern instructionPattern = Pattern.compile("^(\\s)*[a-z_A-z0-9].$");
    
    public Statement()
    {
       
    }
    
    static Statement parseStatement(String str) throws ParseException
    {
        return null;

    }     
}
