package se.neava.Assembler;

import java.text.ParseException;

public class Jnez implements Instruction 
{
    String str;
    byte[] code;
    String label;
    
    public Jnez(int size, String label)
    {
        this.label = label;
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_JNEZBYTE, 0, 0 };
            str = "jnez byte " + label;
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_JNEZWORD, 0, 0 };
            str = "jnez word " + label;
        }
        else
        {
            code = new byte[] { Instruction.OP_JNEZDWORD, 0, 0 };
            str = "jnez dword " + label;
        }
    }
    
    public void addToProgram(Program p) throws ParseException 
    {
        if(p.getAddress(label) != -1)
            fixAddress(p.getAddress(label));
        else
            p.addErrata(this, label);
        p.addStatement(this);
    }

    public byte[] getCode() 
    {
        return code;
    }

    public void fixAddress(int address) 
    {
        code[1] = Parser.low8(address);
        code[2] = Parser.high8(address);
    }
    
    public String toString()
    {
        return str;
    }
}
