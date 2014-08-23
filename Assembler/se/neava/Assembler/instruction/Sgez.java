package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Program;

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
            str = "jgez byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SGEZWORD };
            str = "jgez word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SGEZDWORD };
            str = "jgez dword";
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
