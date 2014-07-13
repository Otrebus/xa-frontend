package se.neava.Assembler;

import java.text.ParseException;

public class Pop implements Instruction {
    int size = -1;
    String label = "";
    byte[] code;
    int addrIndex = 0;
    String str;
    
    public Pop(int imm)
    {
        str = "pop " + imm;
        code = new byte[] { OP_POPIMM, Parser.low8(imm), Parser.high8(imm) };
    }
    
    public Pop(int size, String label)
    {
        assert(size == 1 || size == 2 || size == 4);
        this.size = size;
        this.label = label;
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_POPBYTEADDR, 0, 0 };
            str = "pop byte [" + label + "]";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_POPWORDADDR, 0, 0 };
            str = "pop word [" + label + "]";
        }
        else if(size == 4)
        {
            code = new byte[] { Instruction.OP_POPDWORDADDR, 0, 0 };
            str = "pop dword [" + label + "]";
        }
        addrIndex = 1;
    }
    
    public Pop(int size, int c)
    {
        assert(size == 1 || size == 2 || size == 4);
        this.size = size;
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_POPBYTEFP, Parser.low8(c), Parser.high8(c) };
            str = "pop byte [$fp + " + c + "]";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_POPWORDFP, Parser.low8(c), Parser.high8(c) };
            str = "pop byte [$fp + " + c + "]";
        }
        else if(size == 4)
        {
            code = new byte[] { Instruction.OP_POPDWORDFP, Parser.low8(c), Parser.high8(c) };
            str = "pop byte [$fp + " + c + "]";
        }
    }
    
    public void addToProgram(Program p) throws ParseException 
    {
        if(!label.isEmpty())
        {
            if(p.getAddress(label) != -1)
                fixAddress(p.getAddress(label));
            else
                p.addErrata(this, label);
        }
        p.addStatement(this);
    }

    public byte[] getCode() 
    {
        // TODO Auto-generated method stub
        return code;
    }

    public void fixAddress(int address) {
        code[addrIndex] = Parser.low8(address);
        code[addrIndex + 1] = Parser.high8(address);
    }
    
    public String toString()
    {
        return str;
    }
}