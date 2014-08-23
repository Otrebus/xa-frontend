package se.neava.Assembler.instruction;

import se.neava.Assembler.Program;

public class Sllv implements Instruction 
{
    int size = -1;
    byte[] code;
    String str;
    
    public Sllv(int size)
    {
        assert(size == 1 || size == 2 || size == 4);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_SLLVBYTE };
            str = "sllv byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SLLVWORD };
            str = "sllv word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SLLVDWORD };
            str = "sllv dword";
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
