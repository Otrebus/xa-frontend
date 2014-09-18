package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Program;

/*
 * Class representing the Async instruction.
 */
public class Async implements Instruction 
{
    byte[] code;
    
    public void addToProgram(Program p) throws ParseException 
    {
        code = new byte[] { OP_ASYNC };
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
        return "async";
    }
}
