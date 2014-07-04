package se.neava.Assembler;

import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

public class Parser {
    Lexer lexer;
    Token currentToken;
    
    private static final Map<String, InstructionParser> parseMap = new TreeMap<String, InstructionParser>();
    static {
        parseMap.put("push", new PushParser());
    }
    
    static public byte low8(int x)
    {
        return (byte) (x & 0xFF);
    }
    
    static public byte high8(int x)
    {
        return (byte) ((x >>> 8) & 0xFF);
    }
    
    static public byte higher8(int x)
    {
        return (byte) ((x >>> 16) & 0xFF);
    }
    
    static public byte highest8(int x)
    {
        return (byte) ((x >>> 24) & 0xFF);
    }
    
    private static int parseNum(String num)
    {
        int imm = 0;
        try
        {
            imm = Integer.parseInt(num);
        }
        catch(NumberFormatException e)
        {
            imm = Integer.decode(num);
        }
        return imm;
    }
    
    static class Mem
    {
        boolean fp;
        String value;
        Mem(boolean fp, String value)
        {
            this.fp = fp;
            this.value = value;
        }
    }
    
    private static Mem mem(Lexer lexer) throws ParseException
    {
        Token tok = lexer.accept(Token.Type.FRAMEPOINTER);
        if(tok != null)
        {
            tok = lexer.accept(Token.Type.OPERATOR);
            if(tok != null)
            {
                String sgn = tok.str;
                tok = lexer.expect(Token.Type.NUMBER);
                return new Mem(true, (sgn.equals("-") ? "-" : "") + tok.str);
            }
            return new Mem(true, "0");
        }
        tok = lexer.expect(Token.Type.IDENTIFIER);
        return new Mem(false, tok.str);
    }
    
    interface InstructionParser
    {
        Instruction parseInstruction(Lexer lexer) throws ParseException;
    }
    
    static class PushParser implements InstructionParser
    {
        Lexer lexer;
        
        private Instruction unsizedPush() throws ParseException
        {
            Token tok;
            tok = lexer.accept(Token.Type.NUMBER);
           
            if(tok != null)
            {
                lexer.expect(Token.Type.END);
                int imm = parseNum(tok.str);
                return new Push(false, imm);
            }
            Mem m = mem(lexer);
            lexer.expect(Token.Type.END);
            if(m.fp)
                return new Push(true, parseNum(m.value));
            else
                return new Push(m.value);
        }
        
        private Instruction sizedPush(String size) throws ParseException
        {
            Token tok;
            tok = lexer.accept(Token.Type.END);
            if(tok != null)
                return new Push(getSize(size));
            tok = lexer.accept(Token.Type.NUMBER);
            if(tok != null)
            {
                lexer.expect(Token.Type.END);
                return new Push(getSize(size), false, parseNum(tok.str));
            }
            tok = lexer.accept(Token.Type.OPERATOR);
            if(tok != null)
            {
                String op = tok.str;
                tok = lexer.expect(Token.Type.NUMBER);
                lexer.expect(Token.Type.END);
                return new Push(getSize(size), false, parseNum(op + tok.str));
            }
            lexer.expect(Token.Type.OPENBRACKET);
            Mem m = mem(lexer);
            lexer.expect(Token.Type.CLOSEBRACKET);
            lexer.expect(Token.Type.END);
            if(m.fp)
                return new Push(getSize(size), m.fp, parseNum(m.value));
            else
                return new Push(getSize(size), m.value);
        }
        
        private Instruction pushBody() throws ParseException
        {
            Token tok;
            tok = lexer.accept(Token.Type.SIZE);
            if(tok != null)
                return sizedPush(tok.str);
            return unsizedPush();
            
        }
        
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            this.lexer = lexer;
            return pushBody();
        }
        
    }
    
    private static int getSize(String str) throws ParseException
    {
        if(str.equals("byte"))
            return 1;
        else if(str.equals("word"))
            return 2;
        else if(str.equals("dword"))
            return 4;
        throw new ParseException("bad size", 0);
    }
    
    public Parser(Lexer lexer)
    {
        this.lexer = lexer;
    }
    
    private Statement section()
    {
        Token tok;
        tok = lexer.accept(new Token(Token.Type.IDENTIFIER, "data"));
        if(tok != null)
            return new Section("data");
        tok = lexer.accept(new Token(Token.Type.IDENTIFIER, "code"));
        if(tok != null)
            return new Section("code");
        tok = lexer.accept(new Token(Token.Type.IDENTIFIER, "extern"));
        if(tok != null)
            return new Section("extern");
        return null;
    }
    
    private Statement data(String size) throws ParseException
    {
        Token tok;
        tok = lexer.accept(Token.Type.END);
        if(tok != null)
            return new Data(new byte[getSize(size)]);
        lexer.expect(Token.Type.NUMBER);
        // TODO: Actually fill in data
        lexer.expect(Token.Type.END);
        return new Data(new byte[getSize(size)]);
    }
    
    private Statement label(String str) throws ParseException
    {
        lexer.expect(Token.Type.END);
        return new Label(str.substring(0, str.length() - 1));
    }
    
    Statement parse() throws ParseException
    {
        Token tok;
        tok = lexer.accept(Token.Type.SECTION);
        if(tok != null)
            return section();
        tok = lexer.accept(Token.Type.SIZE);
        if(tok != null)
            return data(tok.str);
        tok = lexer.accept(Token.Type.LABEL);
        if(tok != null)
            return label(tok.str);
        tok = lexer.accept(Token.Type.IDENTIFIER);
        if(tok == null)
            throw new ParseException("Invalid token", 0);
        if(parseMap.containsKey(tok.str))
            return parseMap.get(tok.str).parseInstruction(lexer);
        else
            return null;
    }
}
