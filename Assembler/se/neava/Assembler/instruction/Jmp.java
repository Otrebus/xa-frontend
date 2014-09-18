package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Parser;
import se.neava.Assembler.Program;

/*
 * Class representing the Jmp instruction.
 */
public class Jmp implements Instruction 
{
    String str;
    byte[] code;
    String label;
    
    public Jmp(String label)
    {
        this.label = label;
        code = new byte[] { Instruction.OP_JMP, 0, 0 };
        str = "jmp " + label;
    }
    
    public void addToProgram(Program p) throws ParseException 
    {
        if(p.getAddress(label) != -1)
            fixAddress(p.getAddress(label));
        else
            p.addErrata(this, label);
        p.addStatement(this);
    }

    public byte[] getCode() 
    {
        return code;
    }

    public void fixAddress(int address) 
    {
        code[1] = Parser.low8(address);
        code[2] = Parser.high8(address);
    }
    
    public String toString()
    {
        return str;
    }
}
