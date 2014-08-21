package se.neava.Assembler;

import java.text.ParseException;

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
            str = "jez byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SEZWORD };
            str = "jez word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SEZDWORD };
            str = "jez dword";
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
