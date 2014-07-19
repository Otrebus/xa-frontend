package se.neava.Assembler;

import java.text.ParseException;

public class Jez implements Instruction 
{
    String str;
    byte[] code;
    String label;
    
    public Jez(int size, String label)
    {
        this.label = label;
        assert(size == 1 || size == 2 || size == 3);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_JEZBYTE, 0, 0 };
            str = "jez byte " + label;
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_JEZWORD, 0, 0 };
            str = "jez word " + label;
        }
        else
        {
            code = new byte[] { Instruction.OP_JEZDWORD, 0, 0 };
            str = "jez dword " + label;
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
