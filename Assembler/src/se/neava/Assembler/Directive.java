package se.neava.Assembler;

import java.text.ParseException;

import se.neava.Assembler.Statement;

public class Directive implements Statement {
    String name;
    
    public Directive(String name) throws ParseException 
    {
        if(!name.equals("extern") && !name.equals("entry"))
            throw new ParseException("Illegal directive", 0);
        this.name = name;
    }

    public byte[] getCode() 
    {
        return new byte[] {};
    }
    
    public String toString()
    {
        return "." + name;
    }

    public void addToProgram(Program p) 
    {
        if(name.equals("extern"))
            p.setExtern();
        else
            p.setEntry();
    }
}
