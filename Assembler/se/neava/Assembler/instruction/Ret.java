package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Parser;
import se.neava.Assembler.Program;

/*
 * Class representing the Ret instruction.
 */
public class Ret implements Instruction 
{
    byte[] code;
    int imm;
    String str;
    
    public Ret(int imm)
    {
        code = new byte[] { OP_RET, Parser.low8(imm), Parser.high8(imm) };
        this.imm = imm;
        str = "ret " + imm;
    }
    
    public void addToProgram(Program p) throws ParseException 
    {
        p.addStatement(this);
    }

    public byte[] getCode() 
    {
        return code;
    }

    public void fixAddress(int address) 
    {
    }
    
    public String toString()
    {
        return str;
    }
}
