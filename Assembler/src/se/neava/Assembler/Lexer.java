package se.neava.Assembler;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    String str;
    int pos = 0;
    static Pattern pattern;
    Matcher matcher;
    Token currentToken;
    
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
        currentToken = nextToken();
    }
    
    private void omNomNom(int size)
    {
        str = str.substring(size);
        matcher = pattern.matcher(str);
    }
    
    private Token nextToken()
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
    }
    
    public Token peek()
    {
        return currentToken;
    }
    
    public Token accept(Token.Type type)
    {
        if(currentToken.type.equals(type))
        {
            Token acceptedToken = currentToken;
            currentToken = nextToken();
            return acceptedToken;
        }
        return null;
    }
    
    public Token expect(Token.Type type) throws ParseException
    {
        if(!currentToken.type.equals(type))
            throw new ParseException("Expected token " + type.name(), 0);
        Token acceptedToken = currentToken;
        currentToken = nextToken();
        return acceptedToken;
    }
    
    public Token accept(Token tok)
    {
        if(currentToken.equals(tok))
        {
            Token acceptedToken = currentToken;
            currentToken = nextToken();
            return acceptedToken;
        }
        return null;
    }
    
    public Token expect(Token tok) throws ParseException
    {
        if(!currentToken.equals(tok))
            throw new ParseException("Expected token " + tok.type.name() + ", specifically \"" + tok.str + "\"", 0);
        Token acceptedToken = currentToken;
        currentToken = nextToken();
        return acceptedToken;
    }
}
