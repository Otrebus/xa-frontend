package se.neava.Assembler;

import se.neava.Assembler.Statement;

public class Data implements Statement {
    byte[] code;
    
    Data(byte[] code)
    {
        this.code = code.clone();
    }

    public byte[] getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    public void addToProgram(Program p) {
        // TODO Auto-generated method stub
        p.addStatement(this);        
    }
}
