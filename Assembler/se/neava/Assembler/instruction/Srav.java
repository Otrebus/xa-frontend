package se.neava.Assembler.instruction;

import se.neava.Assembler.Program;

public class Srav implements Instruction 
{
    int size = -1;
    byte[] code;
    String str;
    
    public Srav(int size)
    {
        assert(size == 1 || size == 2 || size == 4);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_SRAVBYTE };
            str = "srav byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SRAVWORD };
            str = "srav word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SRAVDWORD };
            str = "srav dword";
        }
    }
       
    public String toString()
    {
        return str;
    }

    public byte[] getCode() 
    {
        return code;
    }

    public void fixAddress(int address) 
    {
    }

    public void addToProgram(Program p) 
    {
        p.addStatement(this);
    }
}
