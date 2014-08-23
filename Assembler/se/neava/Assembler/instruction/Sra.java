package se.neava.Assembler.instruction;

import se.neava.Assembler.Parser;
import se.neava.Assembler.Program;

public class Sra implements Instruction 
{
    int size = -1;
    byte[] code;
    String str;
    
    public Sra(int size, int imm)
    {
        assert(size == 1 || size == 2 || size == 4);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_SRABYTE, Parser.low8(imm) };
            str = "sra byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_SRAWORD, Parser.low8(imm) };
            str = "sra word";
        }
        else
        {
            code = new byte[] { Instruction.OP_SRADWORD, Parser.low8(imm) };
            str = "sra dword";
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
