package se.neava.Assembler;

import se.neava.Assembler.Statement;

public class Label implements Statement {
    String label;

    public Label(String str) {
        // TODO Auto-generated constructor stub
        label = str;
    }

    @Override
    public byte[] getCode() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String toString()
    {
        return label + ":";
    }

    @Override
    public void addToProgram(Program p) {
        p.addLabel(label);
    }

}
