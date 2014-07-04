package se.neava.Assembler;

import java.text.ParseException;

import se.neava.Assembler.Statement;

public interface Instruction extends Statement {
    public final static byte OP_PUSHFP = 0x01;
    public final static byte OP_PUSHIMM = 0x02;
    public final static byte OP_PUSHADDR = 0x03;
    public final static byte OP_PUSHBYTEFP = 0x04;
    public final static byte OP_PUSHWORDFP = 0x05;
    public final static byte OP_PUSHDWORDFP = 0x06;
    public final static byte OP_PUSHBYTEADDR = 0x07;
    public final static byte OP_PUSHWORDADDR = 0x08;
    public final static byte OP_PUSHDWORDADDR = 0x09;
    public final static byte OP_PUSHBYTEIMM = 0x0A;
    public final static byte OP_PUSHWORDIMM = 0x0B;
    public final static byte OP_PUSHDWORDIMM = 0x0C;
    public final static byte OP_PUSHBYTE = 0x0D;
    public final static byte OP_PUSHWORD = 0x0E;
    public final static byte OP_PUSHDWORD = 0x0F;
    
    public byte[] getCode();
    public void fixAddress(int address);
}
