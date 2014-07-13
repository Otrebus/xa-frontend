package se.neava.Assembler;

import java.text.ParseException;

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
    public void addToProgram(Program p) throws ParseException {
        if(p.getAddress(label) != -1)
            throw new ParseException("Duplicate label: " + label, 0);
        p.addLabel(label);
    }

}