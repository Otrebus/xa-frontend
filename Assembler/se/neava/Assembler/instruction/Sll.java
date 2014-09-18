package se.neava.Assembler.instruction;

import se.neava.Assembler.Parser;
import se.neava.Assembler.Program;

/*
 * Class representing the Sll instruction.
 */
public class Sll implements Instruction 
{
    int size = -1;
    byte[] code;
    String str;
    
    public Sll(int size, int imm)
    {
        assert(size == 1 || size == 2 || size == 4);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_SLLBYTE, Parser.low8(imm) };
            str = "sll byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SLLWORD, Parser.low8(imm) };
            str = "sll word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SLLDWORD, Parser.low8(imm) };
            str = "sll dword";
        }
    }
       
    public String toString()
    {
        return str;
    }

    public byte[] getCode() 
    {
        return code;
    }

    public void fixAddress(int address) 
    {
    }

    public void addToProgram(Program p) 
    {
        p.addStatement(this);
    }
}
