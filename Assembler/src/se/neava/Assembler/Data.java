package se.neava.Assembler;

import se.neava.Assembler.Statement;

public class Data implements Statement {
    byte[] code;
    
    Data(byte[] code)
    {
        this.code = code.clone();
    }
}
