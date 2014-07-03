package se.neava.Assembler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    String str;
    int pos = 0;
    static Pattern pattern;
    Matcher matcher;
    
    static
    {
        Token.Type[] tokens = Token.Type.values();
        String patternStr = "(?<" + tokens[0].name() + ">" + tokens[0].pattern + ")";
        for(int i = 1; i < tokens.length; i++)
            patternStr += "|(?<" + tokens[i].name() + ">" + tokens[i].pattern + ")";
        pattern = Pattern.compile(patternStr);
    }
    
    public Lexer(String str)
    {
        this.str = str;
        matcher = pattern.matcher(str);
    }
    
    private void omNomNom(int size)
    {
        str = str.substring(size);
        matcher = pattern.matcher(str);
    }
    
    public Token nextToken()
    {
        if(str.equals(""))
            return new Token(Token.Type.END, "");
        if(!matcher.lookingAt())
            return new Token(Token.Type.INVALID, "");

        for(Token.Type typ : Token.Type.values())
        {
            String matched = matcher.group(typ.name());
            if (matched != null)
            {
                omNomNom(matched.length());
                if(typ == Token.Type.WHITESPACE)
                    return nextToken();
                return new Token(typ, matched);
            }
        }
        omNomNom(1);
        return new Token(Token.Type.INVALID, "");
    } //         WHITESPACE("(\\d)+"), SIZE("[byte|word|dword]"), LABEL("[a-zA-Z]+:"), IDENTIFIER("[a-zA-Z]+"), NUMBER("[0-9]+"), OPENBRACKET("\\["), CLOSEBRACKET("\\]");
}
