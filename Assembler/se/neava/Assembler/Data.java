package se.neava.Assembler;

import java.text.ParseException;

import se.neava.Assembler.Statement;

public class Data implements Statement {
    byte[] code;
    int value;
    String strSize;
    
    Data(String strSize, int value) throws ParseException
    {
        int size = Parser.getSize(strSize);
        this.value = value;
        this.strSize = strSize;

        code = new byte[size];
        for(int i = 0; i < size; i++)
            code[i] = (byte)((value >>> i*8) & 0xFF);
    }
    
    Data(String strSize) throws ParseException
    {
        this.strSize = strSize;
        code = new byte[Parser.getSize(strSize)];
    }
    

    public byte[] getCode() 
    {
        return code;
    }

    public void addToProgram(Program p) 
    {
        p.addStatement(this);        
    }
    
    public String toString()
    {
        return strSize + " " + value;
    }
}
