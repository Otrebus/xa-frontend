package se.neava.Assembler;

import java.text.ParseException;

import se.neava.Assembler.Statement;

/**
 * Class representing a directive statement, such as ".extern" or ".entry".
 */
public class Directive implements Statement 
{
    String name;
    
    /**
     * Constructs this statement.
     * @param The name of this directive, eg "extern" or "entry".
     * @throws ParseException
     */
    public Directive(String name) throws ParseException 
    {
        if(!name.equals("extern") && !name.equals("entry") && !name.equals("program"))
            throw new ParseException("Illegal directive", 0);
        this.name = name;
    }

    /**
     * This returns nothing since a directive is only used internally by the assembler.
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
        return "." + name;
    }

    /**
     * Adds this instruction to a given program.
     */
    public void addToProgram(Program p) 
    {
        if(name.equals("extern"))
            p.setExtern();
        else if(name.equals("entry"))
            p.setEntry();
        else if(name.equals("program"))
            p.setCodeSegment();
    }
}
