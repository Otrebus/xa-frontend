package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Program;

/*
 * Class representing the Mul instruction.
 */
public class Mul implements Instruction 
{
    String str;
    byte[] code;
    
    public Mul(int size)
    {
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_MULBYTE };
            str = "mul byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_MULWORD };
            str = "mul word";
        }
        else
        {
            code = new byte[] { Instruction.OP_MULDWORD };
            str = "mul dword";
        }
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
