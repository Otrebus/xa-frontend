package se.neava.Assembler;

import java.text.ParseException;

public class Jgz implements Instruction 
{
    String str;
    byte[] code;
    String label;
    
    public Jgz(int size, String label)
    {
        this.label = label;
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_JGZBYTE, 0, 0 };
            str = "jgz byte " + label;
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_JGZWORD, 0, 0 };
            str = "jgz word " + label;
        }
        else
        {
            code = new byte[] { Instruction.OP_JGZDWORD, 0, 0 };
            str = "jgz dword " + label;
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
