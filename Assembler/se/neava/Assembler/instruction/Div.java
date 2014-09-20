package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Program;

/*
 * Class representing the Div instruction.
 */
public class Div implements Instruction 
{
    String str;
    byte[] code;
    
    public Div(int size)
    {
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_DIVBYTE };
            str = "div byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_DIVWORD };
            str = "div word";
        }
        else
        {
            code = new byte[] { Instruction.OP_DIVDWORD };
            str = "div dword";
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
