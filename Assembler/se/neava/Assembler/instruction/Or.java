package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Program;


/*
 * Class representing the Or instruction.
 */
public class Or implements Instruction 
{
    String str;
    byte[] code;
    
    public Or(int size)
    {
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_ORBYTE };
            str = "or byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_ORWORD };
            str = "or word";
        }
        else
        {
            code = new byte[] { Instruction.OP_ORDWORD };
            str = "or dword";
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
