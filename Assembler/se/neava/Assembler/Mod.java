package se.neava.Assembler;

import java.text.ParseException;

public class Mod implements Instruction {
    String str;
    byte[] code;
    
    public Mod(int size)
    {
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_MODBYTE };
            str = "mod byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_MODWORD };
            str = "mod word";
        }
        else
        {
            code = new byte[] { Instruction.OP_MODDWORD };
            str = "mod dword";
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
