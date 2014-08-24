package se.neava.Assembler.instruction;

import se.neava.Assembler.Parser;
import se.neava.Assembler.Program;

public class Push implements Instruction 
{
    int size = -1;
    String label = "";
    byte[] code;
    int addrIndex = 0;
    String str;
    
    public Push(int size)
    {
        assert(size == 1 || size == 2 || size == 4);
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_PUSHBYTE };
            str = "push byte";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_PUSHWORD };
            str = "push word";
        }
        else
        {
            code = new byte[] { Instruction.OP_PUSHDWORD };
            str = "push dword";
        }
    }
    
    
    public Push(int size, boolean fp, int c)
    {
        assert(size == 1 || size == 2 || size == 4);
        this.size = size;
        if(size == 1)
        {
            if(fp)
            {
                code = new byte[] { Instruction.OP_PUSHBYTEFP, Parser.low8(c), Parser.high8(c) };
                str = "push byte [$fp" + (c > 0 ? ("+"+c) : c == 0 ? "" : c) + "]";
            }
            else
            {
                code = new byte[] { Instruction.OP_PUSHBYTEIMM, Parser.low8(c) };
                str = "push byte " + c;
            }
        }
        else if(size == 2)
        {
            if(fp)
            {
                code = new byte[] { Instruction.OP_PUSHWORDFP, Parser.low8(c), Parser.high8(c) };
                str = "push word [$fp" + (c > 0 ? ("+"+c) : c == 0 ? "" : c) + "]";
            }
            else
            {
                code = new byte [] { Instruction.OP_PUSHWORDIMM, Parser.low8(c), Parser.high8(c) };
                str = "push word " + c;
            }
        }
        else
        {
            if(fp)
            {
                code = new byte[] { Instruction.OP_PUSHDWORDFP, Parser.low8(c), Parser.high8(c) };
                str = "push dword [$fp" + (c > 0 ? ("+"+c) : c == 0 ? "" : c) + "]";
            }
            else
            {
                code = new byte [] { Instruction.OP_PUSHDWORDIMM, Parser.low8(c), Parser.high8(c), Parser.higher8(c), Parser.highest8(c) };
                str = "push dword " + c;
            }
        }
    }
    
    public Push(int size, String label)
    {
        assert(size == 1 || size == 2 || size == 4);
        this.size = size;
        this.label = label;
        if(size == 1)
        {
            code = new byte[] { Instruction.OP_PUSHBYTEADDR, 0, 0 };
            str = "push byte [" + label + "]";
        }
        else if(size == 2)
        {
            code = new byte[] { Instruction.OP_PUSHWORDADDR, 0, 0 };
            str = "push word [" + label + "]";
        }
        else if(size == 4)
        {
            code = new byte[] { Instruction.OP_PUSHDWORDADDR, 0, 0 };
            str = "push dword [" + label + "]";
        }
        addrIndex = 1;
    }

    public Push(boolean fp, int c)
    {
        if(fp)
        {
            code = new byte[] { Instruction.OP_PUSHFP, Parser.low8(c), Parser.high8(c) };
            str = "push $fp + " + c;
        }
        else
        {
            code = new byte[] { Instruction.OP_PUSHIMM, Parser.low8(c), Parser.high8(c) };
            str = "push " + c;
        }
    }
    
    public Push(String label)
    {
        this.label = label;
        code = new byte[] { Instruction.OP_PUSHADDR, 0, 0 };
        str = "push " + label;
        addrIndex = 1;
    }
   
    public String toString()
    {
        return str;
    }

    @Override
    public byte[] getCode() {
        return code;
    }

    @Override
    public void fixAddress(int address) {
        code[addrIndex] = Parser.low8(address);
        code[addrIndex + 1] = Parser.high8(address);
    }


    @Override
    public void addToProgram(Program p) {
        if(!label.isEmpty())
        {
            if(p.getAddress(label) != -1)
                fixAddress(p.getAddress(label));
            else
                p.addErrata(this, label);
        }
        p.addStatement(this);
    }
}
