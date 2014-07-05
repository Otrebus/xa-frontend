package se.neava.Assembler;

import java.text.ParseException;

public interface Statement {
    
    byte[] getCode();
    public void addToProgram(Program p) throws ParseException;
}
