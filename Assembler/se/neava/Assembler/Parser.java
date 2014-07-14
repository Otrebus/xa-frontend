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
        parseMap.put("pop", new PopParser());
        parseMap.put("call", new CallParser());
        parseMap.put("ret", new RetParser());
        parseMap.put("sync", new SyncParser());
        parseMap.put("async", new AsyncParser());
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
    
    static int num(Lexer lexer) throws ParseException
    {
        String op = "";
        Token tok = lexer.accept(Token.Type.OPERATOR);
        if(tok != null)
            op = tok.str;
        tok = lexer.expect(Token.Type.NUMBER);
        return parseNum(op + tok.str);
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
    
    private interface InstructionParser
    {
        Instruction parseInstruction(Lexer lexer) throws ParseException;
    }
    
    private static class PushParser implements InstructionParser
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
    
    private static class PopParser implements InstructionParser
    {
        Lexer lexer;

        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            this.lexer = lexer;
            // TODO Auto-generated method stub
            return popBody();
        }

        private Instruction popBody() throws ParseException 
        {
            Token tok;
            tok = lexer.accept(Token.Type.SIZE);
            if(tok != null)
                return sizedPop(tok.str);
            return unsizedPop();
        }
        
        private Instruction sizedPop(String size) throws ParseException {
            // TODO Auto-generated method stub
            lexer.expect(Token.Type.OPENBRACKET);
            Mem m = mem(lexer);
            lexer.expect(Token.Type.CLOSEBRACKET);
            lexer.expect(Token.Type.END);
            if(m.fp)
                return new Pop(getSize(size), parseNum(m.value));
            else
                return new Pop(getSize(size), m.value);
        }

        private Instruction unsizedPop() throws ParseException
        {
            int imm = num(lexer);
            lexer.expect(Token.Type.END);
            return new Pop(imm);
        }
    }
    
    private static class CallParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.IDENTIFIER);
            lexer.expect(Token.Type.END);
            return new Call(tok.str);
        }
    }
    
    private static class RetParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.NUMBER);
            lexer.expect(Token.Type.END);
            return new Ret(Parser.parseNum(tok.str));
        }
    }
    
    private static class AsyncParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            lexer.expect(Token.Type.END);
            return new Async();
        }
    }
    
    private static class SyncParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            lexer.expect(Token.Type.END);
            return new Sync();
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
    
    private Statement section(String str) throws ParseException
    {
        String name = str.substring(1);
        lexer.expect(Token.Type.END);
        return new Directive(name);
    }
    
    private Statement string(String str) throws ParseException
    {
        lexer.expect(Token.Type.END);        
        return new DataString(str.substring(1, str.length() - 1));
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
            return section(tok.str);
        tok = lexer.accept(Token.Type.SIZE);
        if(tok != null)
            return data(tok.str);
        tok = lexer.accept(Token.Type.STRING);
        if(tok != null)
            return string(tok.str);
        tok = lexer.accept(Token.Type.LABEL);
        if(tok != null)
            return label(tok.str);
        tok = lexer.accept(Token.Type.IDENTIFIER);
        if(tok == null)
            throw new ParseException("Invalid token", 0);
        if(parseMap.containsKey(tok.str))
            return parseMap.get(tok.str).parseInstruction(lexer);
        else
            throw new ParseException("Unrecognized instruction " + tok.str, 0);
    }
}
