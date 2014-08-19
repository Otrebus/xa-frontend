package se.neava.Assembler;

import java.text.ParseException;

public class Jez implements Instruction 
{
    String str;
    byte[] code;
    String label;
    
    public Jez(String label)
    {
        this.label = label;
        code = new byte[] { Instruction.OP_JEZ, 0, 0 };
        str = "jez " + label;
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