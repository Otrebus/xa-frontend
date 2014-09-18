package se.neava.Assembler;

/**
 * Class representing a lexer token.
 */
public class Token 
{
    // Regexes for the various tokens
    public static enum Type 
    {
        STRING("\\\".*\\\""),
        WHITESPACE("(\\s)+"), 
        SIZE("(byte|word|dword)\\b"), 
        LABEL("[0-9a-z_A-Z]+:"), 
        IDENTIFIER("[a-z_A-Z]+[0-9a-z_A-Z]*"), 
        NUMBER("(0x[0-9a-fA-F]+|[0-9]+)"), 
        OPENBRACKET("\\["), 
        CLOSEBRACKET("\\]"),
        SECTION("\\.[a-z_A-Z]+"),
        FRAMEPOINTER("\\$fp"),
        OPERATOR("[+-]"),
        INVALID("(?!)"), 
        END("(?!)");
        
        public final String pattern;

        private Type(String pattern) 
        {
            this.pattern = pattern;
        }
    }
    
    String str;
    Type type;
    
    /**
     * Constructor
     * @param type The type of this token.
     * @param str The string that generated this token.
     */
    public Token(Type type, String str)
    {
        this.type = type;
        this.str = str;
    }
    
    /**
     * Returns a string representation of this token (for debugging, etc).
     */
    public String toString()
    {
        return "(" + type.name() + ", " + str + ")";
    }
    
    /**
     * Returns true if this token is exactly equal to the argument.
     * @param tok The token to check for equality.
     * @return True if equal, false if not.
     */
    boolean equals(Token tok)
    {
        return type == tok.type && str.equals(tok.str);
    }
}
