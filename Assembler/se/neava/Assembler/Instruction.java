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
    public final static byte OP_POPIMM = 0x10;
    public final static byte OP_POPBYTEFP = 0x11;
    public final static byte OP_POPWORDFP = 0x12;
    public final static byte OP_POPDWORDFP = 0x13;
    public final static byte OP_POPBYTEADDR = 0x14;
    public final static byte OP_POPWORDADDR = 0x15;
    public final static byte OP_POPDWORDADDR = 0x16;
    public final static byte OP_CALL = 0x17;
    public final static byte OP_RET = 0x18;
    public final static byte OP_SYNC = 0x19;
    public final static byte OP_ASYNC = 0x1A;
    
    public byte[] getCode();
    public void fixAddress(int address);
}
