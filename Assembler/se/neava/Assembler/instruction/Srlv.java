package se.neava.Assembler.instruction;

import se.neava.Assembler.Program;

public class Srlv implements Instruction 
{
    int size = -1;
    byte[] code;
    String str;
    
    public Srlv(int size)
    {
        assert(size == 1 || size == 2 || size == 4);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_SRLVBYTE };
            str = "srlv byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SRLVWORD };
            str = "srlv word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SRLVDWORD };
            str = "srlv dword";
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
