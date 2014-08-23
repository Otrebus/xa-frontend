package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Program;

public class Snez implements Instruction 
{
    String str;
    byte[] code;
    
    public Snez(int size)
    {
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_SNEZBYTE };
            str = "jnez byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SNEZWORD };
            str = "jnez word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SNEZDWORD };
            str = "jnez dword";
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
