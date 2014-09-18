package se.neava.Assembler;

import java.io.UnsupportedEncodingException;

/**
 * Represents a data series consisting of a zero-terminated string of ascii characters.
 */
public class DataString implements Statement 
{
    byte[] code; // The actual bytes representing this data
    String str;  // The corresponding line of assembly language of this data
    
    /**
     * Constructs an allocated string of data.
     * @param The ascii string composing the data.
     */
    DataString(String str)
    {
        this.str = str;
        try 
        {
            this.code = new byte[str.length() + 1];
            
            byte[] strBytes = str.getBytes("UTF-8");
            for(int i = 0; i < strBytes.length; i++)
                this.code[i] = strBytes[i];
            this.code[str.length()] = 0; // Zero-terminate
        } 
        catch (UnsupportedEncodingException e) 
        {   // This might happen if the system does not support UTF-8 (!)
            e.printStackTrace();
        }
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
        return "\"" + str + "\"";
    }
}
