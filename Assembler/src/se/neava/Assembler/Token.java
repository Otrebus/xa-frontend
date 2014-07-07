package se.neava.Assembler;

public class Token {
    
    public static enum Type 
    {
        STRING("\\\".*\\\""),
        WHITESPACE("(\\s)+"), 
        SIZE("(byte|word|dword)\\b"), 
        LABEL("[a-z_A-Z]+:"), 
        IDENTIFIER("[a-z_A-Z]+[0-9a-z_A-Z]*"), 
        NUMBER("(0x[0-9a-fA-F]+|[0-9]+)"), 
        OPENBRACKET("\\["), 
        CLOSEBRACKET("\\]"),
        SECTION("\\.section"),
        FRAMEPOINTER("\\$fp"),
        OPERATOR("[+-]"),
        INVALID("(?!)"), 
        END("(?!)");
        
        public final String pattern;

        private Type(String pattern) {
            this.pattern = pattern;
        }
    }
    
    String str;
    Type type;
    
    public Token(Type type, String str)
    {
        this.type = type;
        this.str = str;
    }
    
    public String toString()
    {
        return "(" + type.name() + ", " + str + ")";
    }
    
    boolean equals(Token tok)
    {
        return type == tok.type && str.equals(tok.str);
    }
}
