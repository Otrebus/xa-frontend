package se.neava.Assembler;

import se.neava.Assembler.Statement;

public class Section implements Statement {
    String name;
    
    public Section(String name)
    {
        this.name = name;
    }
}
