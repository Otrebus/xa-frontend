package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Program;

/*
 * Class representing the Sez instruction.
 */
public class Sez implements Instruction 
{
    String str;
    byte[] code;
    
    public Sez(int size)
    {
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_SEZBYTE };
            str = "sez byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SEZWORD };
            str = "sez word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SEZDWORD };
            str = "sez dword";
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
