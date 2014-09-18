package se.neava.Assembler;

import java.text.ParseException;
import java.util.ArrayList;

import se.neava.Assembler.Statement;

/**
 * Class representing a data statement in the assembly code, such as "dword 14" or "byte[3] 0 1 2".
 */
public class Data implements Statement 
{
    byte[] code; // The actual bytes representing this data
    String str;  // The corresponding line of assembly language of this data
    
    // TODO: change to int size to conform with the others
    /**
     * Constructor for an allocated and initialized chunk of data.
     * @param The size of this allocated chunk of data.
     * @param The value of the data.
     * @throws ParseException
     */
    Data(String strSize, int value) throws ParseException
    {
        int size = Parser.getSize(strSize);

        code = new byte[size];
        for(int i = 0; i < size; i++)
            code[i] = (byte)((value >>> i*8) & 0xFF);
        str = strSize + " " + value;
    }
    
    /**
     * Constructor for an uninitialized chunk of data.
     * @param The size of this data.
     * @throws ParseException
     */
    Data(String strSize) throws ParseException
    {
        code = new byte[Parser.getSize(strSize)];
        str = strSize;
    }

    /**
     * Constructor for an array of data elements.
     * @param strSize The size of each element.
     * @param array The values of the elements.
     * @throws ParseException
     */
    public Data(String strSize, int[] array) throws ParseException 
    {
        int size = Parser.getSize(strSize);
        ArrayList<Byte> listCode = new ArrayList<Byte>(); // Temp array for easier handling
        
        // Turn each value into the equivalent byte, word or dword, little-endian
        for(int i = 0; i < array.length; i++)
        {
            for(int j = 0; j < size; j++)
                listCode.add((byte)((array[i] >>> j*8) & 0xFF));
        }
        code = new byte[listCode.size()]; // Back to our static code array
        for(int i = 0; i < listCode.size(); i++)
            code[i] = listCode.get(i);
        str = strSize + "[" + array.length + "] ";
        for(int i = 0; i < array.length; i++)
            strSize += array[i] + " ";
    }

    /**
     * Returns the bytecode representation of this statement.
     * @return the bytecode representation of this statement.
     */
    public byte[] getCode() 
    {
        return code;
    }

    /**
     * Adds this instruction to a given program.
     */
    public void addToProgram(Program p) 
    {
        p.addStatement(this);        
    }
    
    /**
     * Returns the assembly language string composing this statement.
     */
    public String toString()
    {
        return str;
    }
}
