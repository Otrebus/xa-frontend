package se.neava.Assembler;

import java.text.ParseException;

import se.neava.Assembler.Statement;

public interface Instruction extends Statement {
    
    // Opcodes. Not an enum to make things explicit and help translate it to the vm
    
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
    public final static byte OP_CALLE = 0x1B;
    public final static byte OP_ADDBYTE = 0x1C;
    public final static byte OP_ADDWORD = 0x1D;
    public final static byte OP_ADDDWORD = 0x1E;
    public final static byte OP_SUBBYTE = 0x1F;
    public final static byte OP_SUBWORD = 0x20;
    public final static byte OP_SUBDWORD = 0x21;
    public final static byte OP_MULBYTE = 0x22;
    public final static byte OP_MULWORD = 0x23;
    public final static byte OP_MULDWORD = 0x24;
    public final static byte OP_DIVBYTE = 0x25;
    public final static byte OP_DIVWORD = 0x26;
    public final static byte OP_DIVDWORD = 0x27;
    public final static byte OP_MODBYTE = 0x28;
    public final static byte OP_MODWORD = 0x29;
    public final static byte OP_MODDWORD = 0x2a;
    
    public final static byte OP_ANDBYTE = 0x2b;
    public final static byte OP_ANDWORD = 0x2c;
    public final static byte OP_ANDDWORD = 0x2d;
    
    public final static byte OP_ORBYTE = 0x2e;
    public final static byte OP_ORWORD = 0x2f;
    public final static byte OP_ORDWORD = 0x30;
    
    public final static byte OP_XORBYTE = 0x31;
    public final static byte OP_XORWORD = 0x32;
    public final static byte OP_XORDWORD = 0x33;
    
    public final static byte OP_JGZBYTE = 0x34;
    public final static byte OP_JGZWORD = 0x35;
    public final static byte OP_JGZDWORD = 0x36;

    public final static byte OP_JGEZBYTE = 0x37;
    public final static byte OP_JGEZWORD = 0x38;
    public final static byte OP_JGEZDWORD = 0x39;
    
    public final static byte OP_JEZBYTE = 0x3a;
    public final static byte OP_JEZWORD = 0x3b;
    public final static byte OP_JEZDWORD = 0x3c;
    
    public final static byte OP_JNEZBYTE = 0x3d;
    public final static byte OP_JNEZWORD = 0x3e;
    public final static byte OP_JNEZDWORD = 0x3f;
    
    public byte[] getCode();
    public void fixAddress(int address);
}
