package se.neava.Assembler;

import java.text.ParseException;

import se.neava.Assembler.Statement;

/**
 * Class representing a label statement.
 */
public class Label implements Statement 
{
    String label;

    /**
     * Constructs a label statement.
     * @param str The name of the label.
     */
    public Label(String str) 
    {
        label = str;
    }

    /**
     * This returns nothing since a label is only used internally by the assembler.
     * @return An empty array of bytes.
     */
    public byte[] getCode() 
    {
        return new byte[] {};
    }
    
    /**
     * Returns the assembly language string composing this directive.
     */
    public String toString()
    {
        return label + ":";
    }

    /**
     * Adds this label to a given program.
     */
    public void addToProgram(Program p) throws ParseException 
    {
        if(p.getAddress(label) != -1)
            throw new ParseException("Duplicate label: " + label, 0);
        p.addLabel(label);
    }
}
