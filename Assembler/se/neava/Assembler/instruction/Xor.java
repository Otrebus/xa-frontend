package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Program;

public class Xor implements Instruction {
    String str;
    byte[] code;
    
    public Xor(int size)
    {
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_XORBYTE };
            str = "xor byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_XORWORD };
            str = "xor word";
        }
        else
        {
            code = new byte[] { Instruction.OP_XORDWORD };
            str = "xor dword";
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
