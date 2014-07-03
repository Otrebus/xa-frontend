package se.neava.Assembler;

import java.text.ParseException;

import se.neava.Assembler.Statement;

public class Instruction implements Statement {
    private byte[] code;
    private int addressIndex = -1;
    
    public Instruction(byte[] code, int addressIndex)
    {
        this.code = code.clone();
        this.addressIndex = addressIndex;
    }
    
    public byte[] getCode()
    {
        return code;
    }
    
    public void fixAddress(int address) throws ParseException
    {
        if(addressIndex <= 0)
            throw new ParseException("Address index <= 0", 0);
        code[addressIndex] = (byte)(address & 0xFF);
        code[addressIndex + 1] = (byte)(address >>> 8);
    }
}
