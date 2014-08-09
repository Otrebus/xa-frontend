package se.neava.Assembler;

import java.text.ParseException;

import se.neava.Assembler.Statement;

public class Data implements Statement {
    byte[] code;
    int value;
    String strSize;
    String str;
    
    // TODO: change to int size to conform with the others
    Data(String strSize, int value) throws ParseException
    {
        int size = Parser.getSize(strSize);
        this.value = value;
        this.strSize = strSize;

        code = new byte[size];
        for(int i = 0; i < size; i++)
            code[i] = (byte)((value >>> i*8) & 0xFF);
        str = strSize + " " + value;
    }
    
    Data(String strSize) throws ParseException
    {
        this.strSize = strSize;
        code = new byte[Parser.getSize(strSize)];
        str = strSize;
    }
    

    public Data(int i) 
    {
        code = new byte[i];
        str = "byte[" + i + "]";
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
        return str;
    }
}
