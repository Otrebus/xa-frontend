package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Program;

/*
 * Class representing the Add instruction.
 */
public class Add implements Instruction 
{
    String str;
    byte[] code;
    
    public Add(int size)
    {
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_ADDBYTE };
            str = "add byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_ADDWORD };
            str = "add word";
        }
        else
        {
            code = new byte[] { Instruction.OP_ADDDWORD };
            str = "add dword";
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
