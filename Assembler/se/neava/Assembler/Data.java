package se.neava.Assembler;

import java.text.ParseException;
import java.util.ArrayList;

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

    public Data(String strSize, int[] array) throws ParseException 
    {
        int size = Parser.getSize(strSize);
        ArrayList<Byte> listCode = new ArrayList<Byte>();
        for(int i = 0; i < array.length; i++)
        {
            for(int j = 0; j < size; j++)
                listCode.add((byte)((array[i] >>> j*8) & 0xFF));
        }
        code = new byte[listCode.size()];
        for(int i = 0; i < listCode.size(); i++)
            code[i] = listCode.get(i);
        str = strSize + "[" + array.length + "] ";
        for(int i = 0; i < array.length; i++)
            strSize += array[i] + " ";
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
