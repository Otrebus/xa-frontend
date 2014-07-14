package se.neava.Assembler;

import java.text.ParseException;

public class Sync implements Instruction {
    byte[] code;
    
    public void addToProgram(Program p) throws ParseException 
    {
        code = new byte[] { OP_SYNC };
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
        return "sync";
    }
}
