package se.neava.Assembler;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible for extracting tokens from a line of code.
 */
public class Lexer 
{
    String str; // The input line
    int pos = 0; // The position in the input line
    static Pattern pattern; // The combined pattern for all tokens
    Matcher matcher;        // The util.regex matcher object for said pattern
    Token currentToken;     // The last matched token
    
    static
    {
        // Go through each regex described in the Token.Type enum and put them together
        // in a string (<NAME1>regex1)|(<NAME2>regex2) which we can use the util.matcher
        // mechanisms on to get retrieve a token name for a matched regex
        Token.Type[] tokens = Token.Type.values();
        String patternStr = "(?<" + tokens[0].name() + ">" + tokens[0].pattern + ")";
        for(int i = 1; i < tokens.length; i++)
            patternStr += "|(?<" + tokens[i].name() + ">" + tokens[i].pattern + ")";
        pattern = Pattern.compile(patternStr);
    }
    
    /**
     * Constructs a lexer object from a given string.
     * @param str A string of assembly code.
     */
    public Lexer(String str)
    {
        this.str = str;
        matcher = pattern.matcher(str);
        currentToken = nextToken();
    }
 
    /**
     * OM NOM NOM NOM
     * @param size NOM NOM NOM
     */
    private void omNomNom(int size)
    {
        str = str.substring(size);
        matcher = pattern.matcher(str);
    }
    
    /**
     * Identifies and returns the next token in the lexed string.
     * @return The next identified token.
     */
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
    
    /**
     * Returns the most recently identified token.
     * @return the most recently identified token.
     */
    public Token peek()
    {
        return currentToken;
    }
    
    /**
     * Attempts to match a given token to the next token in the string.
     * @param A given token.
     * @return If the token matched, that token is returned; otherwise null.
     */
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
    
    /**
     * Expects to match a given token to the next token in the string.
     * @param type The token to match.
     * @return The matched token.
     * @throws ParseException If there is no match, an exception is thrown.
     */
    public Token expect(Token.Type type) throws ParseException
    {
        if(!currentToken.type.equals(type))
            throw new ParseException("Expected token " + type.name(), 0);
        Token acceptedToken = currentToken;
        currentToken = nextToken();
        return acceptedToken;
    }
}
