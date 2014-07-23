package se.neava.Assembler;

import java.text.ParseException;

public class And implements Instruction {
    String str;
    byte[] code;
    
    public And(int size)
    {
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_ANDBYTE };
            str = "and byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_ANDWORD };
            str = "and word";
        }
        else
        {
            code = new byte[] { Instruction.OP_ANDDWORD };
            str = "and dword";
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
