package se.neava.Assembler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import se.neava.Assembler.instruction.*;

/**
 * Class responsible for parsing a set of lexemes to a string of bytes.
 */
public class Parser 
{
    Lexer lexer;        // The lexer for this string
    
    // Maps each instruction mnemonic to its parser
    private static final Map<String, InstructionParser> parseMap 
        = new TreeMap<String, InstructionParser>();
    static 
    {
        parseMap.put("push", new PushParser());
        parseMap.put("pop", new PopParser());
        parseMap.put("call", new CallParser());
        parseMap.put("ret", new RetParser());
        parseMap.put("sync", new SyncParser());
        parseMap.put("async", new AsyncParser());
        parseMap.put("add", new AddParser());
        parseMap.put("sub", new SubParser());
        parseMap.put("mul", new MulParser());
        parseMap.put("div", new DivParser());
        parseMap.put("mod", new ModParser());
        parseMap.put("and", new AndParser());
        parseMap.put("or", new OrParser());
        parseMap.put("xor", new XorParser());
        parseMap.put("sgz", new SgzParser());
        parseMap.put("sgez", new SgezParser());
        parseMap.put("snez", new SnezParser());
        parseMap.put("sez", new SezParser());
        parseMap.put("jmp", new JmpParser());
        parseMap.put("jnez", new JnezParser());
        parseMap.put("jez", new JezParser());
        parseMap.put("sll", new SllParser());
        parseMap.put("srl", new SrlParser());
        parseMap.put("sra", new SraParser());
        parseMap.put("sllv", new SllvParser());
        parseMap.put("srlv", new SrlvParser());
        parseMap.put("srav", new SravParser());
    }
    
    /**
     * Returns the least significant 8 bits of a number (bits 0..7).
     * @param x An integer.
     * @return the least significant 8 bits of a number.
     */
    static public byte low8(int x)
    {
        return (byte) (x & 0xFF);
    }
    
    /**
     * Returns bits 8..15 of a number.
     * @param x An integer.
     * @return bits 8..15 of the given integer.
     */
    static public byte high8(int x)
    {
        return (byte) ((x >>> 8) & 0xFF);
    }
    
    /**
     * Returns bits 16..23 of a number.
     * @param x An integer.
     * @return bits 16..23 of the given integer.
     */
    static public byte higher8(int x)
    {
        return (byte) ((x >>> 16) & 0xFF);
    }
    
    /**
     * Returns bits 24..31 of a number.
     * @param x An integer.
     * @return bits 24..31 of the given integer.
     */
    static public byte highest8(int x)
    {
        return (byte) ((x >>> 24) & 0xFF);
    }
    
    /**
     * Attempts to match the next token to a number.
     * @param lexer The lexer supplying the tokens.
     * @return The matched integer.
     * @throws ParseException
     */
    private static int num(Lexer lexer) throws ParseException
    {
        String op = "";
        Token tok = lexer.accept(Token.Type.OPERATOR);
        if(tok != null)
            op = tok.str;
        tok = lexer.expect(Token.Type.NUMBER);
        return Integer.decode(op + tok.str);
    }
    
    /**
     * Represents a memory location.
     */
    private static class Mem
    {
        boolean fp;
        String value;
        Mem(boolean fp, String value)
        {
            this.fp = fp;
            this.value = value;
        }
    }
    
    /**
     * Parses a string representing a memory location like [$fp+1] or [label]
     * @param lexer The lexer supplying the tokens.
     * @return A mem object representing the memory location.
     * @throws ParseException
     */
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
    
    /**
     * The instruction parser interface which is instantiated for every instruction.
     */
    private interface InstructionParser
    {
        Instruction parseInstruction(Lexer lexer) throws ParseException;
    }
    
    /**
     * Instruction parser for the Sll instruction.
     */
    private static class SllParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            int size = getSize(tok.str);
            tok = lexer.expect(Token.Type.NUMBER);
            int imm = Integer.decode(tok.str);
            lexer.expect(Token.Type.END);
            return new Sll(size, imm); 
        }
    }
    
    /**
     * Instruction parser for the Srl instruction.
     */
    private static class SrlParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            int size = getSize(tok.str);
            tok = lexer.expect(Token.Type.NUMBER);
            int imm = Integer.decode(tok.str);
            lexer.expect(Token.Type.END);
            return new Srl(size, imm); 
        }
    }
    
    /**
     * Instruction parser for the Sra instruction.
     */
    private static class SraParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            int size = getSize(tok.str);
            tok = lexer.expect(Token.Type.NUMBER);
            int imm = Integer.decode(tok.str);
            lexer.expect(Token.Type.END);
            return new Sra(size, imm); 
        }
    }
    
    /**
     * Instruction parser for the Sllv instruction.
     */    
    private static class SllvParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            int size = getSize(tok.str);
            lexer.expect(Token.Type.END);
            return new Sllv(size); 
        }
    }
    
    /**
     * Instruction parser for the Srlv instruction.
     */
    private static class SrlvParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            int size = getSize(tok.str);
            lexer.expect(Token.Type.END);
            return new Srlv(size); 
        }
    }
    
    /**
     * Instruction parser for the Srav instruction.
     */
    private static class SravParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            int size = getSize(tok.str);
            lexer.expect(Token.Type.END);
            return new Srav(size); 
        }
    }
    
    /**
     * Instruction parser for the Jmp instruction.
     */
    private static class JmpParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.IDENTIFIER);
            String label = tok.str;
            lexer.expect(Token.Type.END);
            return new Jmp(label);
        }
    }
    
    /**
     * Instruction parser for the Sgz instruction.
     */
    private static class SgzParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            int size = getSize(tok.str);
            lexer.expect(Token.Type.END);
            return new Sgz(size);
        }
    }
    
    /**
     * Instruction parser for the Sgez instruction.
     */
    private static class SgezParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            int size = getSize(tok.str);
            lexer.expect(Token.Type.END);
            return new Sgez(size);
        }
    }
    
    /**
     * Instruction parser for the Sez instruction.
     */
    private static class SezParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            int size = getSize(tok.str);
            lexer.expect(Token.Type.END);
            return new Sez(size);
        }
    }
    
    /**
     * Instruction parser for the Snez instruction.
     */
    private static class SnezParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            int size = getSize(tok.str);
            lexer.expect(Token.Type.END);
            return new Snez(size);
        }
    }
    
    /**
     * Instruction parser for the Add instruction.
     */
    private static class AddParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            lexer.expect(Token.Type.END);
            int size = getSize(tok.str);
            return new Add(size);
        }
    }
    
    /**
     * Instruction parser for the Sub instruction.
     */
    private static class SubParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            lexer.expect(Token.Type.END);
            int size = getSize(tok.str);
            return new Sub(size);
        }
    }
    
    /**
     * Instruction parser for the Mul instruction.
     */
    private static class MulParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            lexer.expect(Token.Type.END);
            int size = getSize(tok.str);
            return new Mul(size);
        }
    }
    
    /**
     * Instruction parser for the Div instruction.
     */
    private static class DivParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            lexer.expect(Token.Type.END);
            int size = getSize(tok.str);
            return new Div(size);
        }
    }
    
    /**
     * Instruction parser for the Mod instruction.
     */
    private static class ModParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            lexer.expect(Token.Type.END);
            int size = getSize(tok.str);
            return new Mod(size);
        }
    }
    
    /**
     * Instruction parser for the And instruction.
     */
    private static class AndParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            lexer.expect(Token.Type.END);
            int size = getSize(tok.str);
            return new And(size);
        }
    }
    
    /**
     * Instruction parser for the Or instruction.
     */
    private static class OrParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            lexer.expect(Token.Type.END);
            int size = getSize(tok.str);
            return new Or(size);
        }
    }
    
    /**
     * Instruction parser for the Xor instruction.
     */
    private static class XorParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.SIZE);
            lexer.expect(Token.Type.END);
            int size = getSize(tok.str);
            return new Xor(size);
        }
    }
    
    /**
     * Instruction parser for the Push instruction.
     */
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
                int imm = Integer.decode(tok.str);
                return new Push(false, imm);
            }
            Mem m = mem(lexer);
            lexer.expect(Token.Type.END);
            if(m.fp)
                return new Push(true, Integer.decode(m.value));
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
                return new Push(getSize(size), false, Integer.decode(tok.str));
            }
            tok = lexer.accept(Token.Type.OPERATOR);
            if(tok != null)
            {
                String op = tok.str;
                tok = lexer.expect(Token.Type.NUMBER);
                lexer.expect(Token.Type.END);
                return new Push(getSize(size), false, Integer.decode(op + tok.str));
            }
            lexer.expect(Token.Type.OPENBRACKET);
            Mem m = mem(lexer);
            lexer.expect(Token.Type.CLOSEBRACKET);
            lexer.expect(Token.Type.END);
            if(m.fp)
                return new Push(getSize(size), m.fp, Integer.decode(m.value));
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
    
    /**
     * Instruction parser for the Pop instruction.
     */
    private static class PopParser implements InstructionParser
    {
        Lexer lexer;

        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            this.lexer = lexer;
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
        
        private Instruction sizedPop(String size) throws ParseException 
        {
            Token tok = lexer.accept(Token.Type.OPENBRACKET);
            if(tok == null)
            {
                lexer.expect(Token.Type.END);
                return new Pop(getSize(size), false);
            }
            Mem m = mem(lexer);
            lexer.expect(Token.Type.CLOSEBRACKET);
            lexer.expect(Token.Type.END);
            if(m.fp)
                return new Pop(getSize(size), Integer.decode(m.value));
            else
                return new Pop(getSize(size), m.value);
        }

        private Instruction unsizedPop() throws ParseException
        {
            int imm = num(lexer);
            lexer.expect(Token.Type.END);
            return new Pop(imm, true);
        }
    }
    
    /**
     * Instruction parser for the Call instruction.
     */
    private static class CallParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.IDENTIFIER);
            lexer.expect(Token.Type.END);
            return new Call(tok.str);
        }
    }
    
    /**
     * Instruction parser for the Ret instruction.
     */
    private static class RetParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.NUMBER);
            lexer.expect(Token.Type.END);
            return new Ret(Integer.decode(tok.str));
        }
    }
    
    /**
     * Instruction parser for the Async instruction.
     */
    private static class AsyncParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            lexer.expect(Token.Type.END);
            return new Async();
        }
    }
    
    /**
     * Instruction parser for the Sync instruction.
     */
    private static class SyncParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            lexer.expect(Token.Type.END);
            return new Sync();
        }
    }
    
    /**
     * Instruction parser for the Jez instruction.
     */
    private static class JezParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.IDENTIFIER);
            String label = tok.str;
            lexer.expect(Token.Type.END);
            return new Jez(label);
        }
    }
    
    /**
     * Instruction parser for the Jnez instruction.
     */
    private static class JnezParser implements InstructionParser
    {
        public Instruction parseInstruction(Lexer lexer) throws ParseException 
        {
            Token tok = lexer.expect(Token.Type.IDENTIFIER);
            String label = tok.str;
            lexer.expect(Token.Type.END);
            return new Jnez(label);
        }
    }
    
    /**
     * Transforms a data size string to its corresponding byte length.
     * @param str A string in the set {"byte", "word", "dword"}.
     * @return The length of the given size, in number of bytes.
     * @throws ParseException
     */
    static int getSize(String str) throws ParseException
    {
        if(str.equals("byte"))
            return 1;
        else if(str.equals("word"))
            return 2;
        else if(str.equals("dword"))
            return 4;
        throw new ParseException("bad size", 0);
    }
    
    /**
     * Constructs a parser for the given lexer.
     * @param lexer A given lexer for an instruction.
     */
    public Parser(Lexer lexer)
    {
        this.lexer = lexer;
    }
    
    /**
     * Parses a directive statement, like ".code"
     * @param str The directive statement.
     * @return The directive object corresponding to this directive.
     * @throws ParseException
     */
    private Statement directive(String str) throws ParseException
    {
        String name = str.substring(1);
        lexer.expect(Token.Type.END);
        return new Directive(name);
    }
    
    /**
     * Parses a string to its corresponding statement object.
     * @param str The string to parse.
     * @return the corresponding statement object of the given string.
     * @throws ParseException
     */
    private Statement string(String str) throws ParseException
    {
        lexer.expect(Token.Type.END);        
        return new DataString(str.substring(1, str.length() - 1));
    }
    
    /**
     * Parses a data declaration to its representing statement object.
     * @param size The data size mnemonic (the first word of the string)
     * @return The statement object representing this line of data allocation.
     * @throws ParseException
     */
    private Statement data(String size) throws ParseException
    {
        Token tok;
        tok = lexer.accept(Token.Type.END);
        if(tok != null)
            return new Data(size);
        tok = lexer.accept(Token.Type.OPENBRACKET);
        if(tok != null)
        {
            tok = lexer.expect(Token.Type.NUMBER);
            int arrayLength = Integer.decode(tok.str);
            lexer.expect(Token.Type.CLOSEBRACKET);
            ArrayList<Integer> data = new ArrayList<Integer>();        
            do
            {
                tok = lexer.accept(Token.Type.NUMBER);
                if(tok != null)
                    data.add(Integer.parseInt(tok.str));
            } 
            while(tok != null);
            
            if(data.size() > 0)
            {
                if(data.size() != arrayLength)
                    throw new ParseException("Initializer length mismatch: needed " 
                                             + arrayLength + ", got " + data.size(), 0);
                
                tok = lexer.expect(Token.Type.END);
                
                int[] array = new int[arrayLength];
                for(int i = 0; i < array.length; i++)
                    array[i] = data.get(i);
                return new Data(size, array);
            }
            return new Data(size, new int[arrayLength]);
        }
        tok = lexer.expect(Token.Type.NUMBER);        
        int i = Integer.parseInt(tok.str);
        lexer.expect(Token.Type.END);
        return new Data(size, i);
    }
    
    /**
     * Parses a label statement, like "blah:"
     * @param str The label statement.
     * @return The label object corresponding to this label.
     * @throws ParseException
     */
    private Statement label(String str) throws ParseException
    {
        lexer.expect(Token.Type.END);
        return new Label(str.substring(0, str.length() - 1));
    }
    
    /**
     * Parses this line of lexemes.
     * @return The statement corresponding to this line.
     * @throws ParseException
     */
    Statement parse() throws ParseException
    {
        Token tok;
        tok = lexer.accept(Token.Type.SECTION);
        if(tok != null)
            return directive(tok.str);
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
