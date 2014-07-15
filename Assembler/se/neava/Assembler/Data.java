package se.neava.Assembler;

import se.neava.Assembler.Statement;

public class Data implements Statement {
    byte[] code;
    
    Data(byte[] code)
    {
        this.code = code.clone();
    }

    public byte[] getCode() 
    {
        return code;
    }

    public void addToProgram(Program p) 
    {
        p.addStatement(this);        
    }
}
