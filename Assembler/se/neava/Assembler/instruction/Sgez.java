package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Program;

/*
 * Class representing the Sgez instruction.
 */
public class Sgez implements Instruction 
{
    String str;
    byte[] code;
    
    public Sgez(int size)
    {
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_SGEZBYTE };
            str = "sgez byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SGEZWORD };
            str = "sgez word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SGEZDWORD };
            str = "sgez dword";
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
