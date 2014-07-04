package se.neava.Assembler;

import se.neava.Assembler.Statement;

public class Section implements Statement {
    String name;
    
    public Section(String name)
    {
        this.name = name;
    }

    @Override
    public byte[] getCode() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String toString()
    {
        return ".section " + name;
    }

    @Override
    public void addToProgram(Program p) {
        // TODO Auto-generated method stub
        p.setSection(name);
    }
}
