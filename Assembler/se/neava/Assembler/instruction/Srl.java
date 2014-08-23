package se.neava.Assembler.instruction;

import se.neava.Assembler.Parser;
import se.neava.Assembler.Program;

public class Srl implements Instruction 
{
    int size = -1;
    byte[] code;
    String str;
    
    public Srl(int size, int imm)
    {
        assert(size == 1 || size == 2 || size == 4);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_SRLBYTE, Parser.low8(imm) };
            str = "srl byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SRLWORD, Parser.low8(imm) };
            str = "srl word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SRLDWORD, Parser.low8(imm) };
            str = "srl dword";
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
