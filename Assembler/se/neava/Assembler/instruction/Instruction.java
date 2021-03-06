package se.neava.Assembler.instruction;

import java.text.ParseException;

import se.neava.Assembler.Statement;

/*
 * Base class for all instructions.
 */
public interface Instruction extends Statement 
{
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
    public final static byte OP_POPBYTE = 0x17;
    public final static byte OP_POPWORD = 0x18;
    public final static byte OP_POPDWORD = 0x19;
    public final static byte OP_CALL = 0x1A;
    public final static byte OP_RET = 0x1B;
    public final static byte OP_SYNC = 0x1C;
    public final static byte OP_ASYNC = 0x1D;
    public final static byte OP_CALLE = 0x1E;
    public final static byte OP_ADDBYTE = 0x1F;
    public final static byte OP_ADDWORD = 0x20;
    public final static byte OP_ADDDWORD = 0x21;
    public final static byte OP_SUBBYTE = 0x22;
    public final static byte OP_SUBWORD = 0x23;
    public final static byte OP_SUBDWORD = 0x24;
    public final static byte OP_MULBYTE = 0x25;
    public final static byte OP_MULWORD = 0x26;
    public final static byte OP_MULDWORD = 0x27;
    public final static byte OP_DIVBYTE = 0x28;
    public final static byte OP_DIVWORD = 0x29;
    public final static byte OP_DIVDWORD = 0x2A;
    public final static byte OP_MODBYTE = 0x2B;
    public final static byte OP_MODWORD = 0x2C;
    public final static byte OP_MODDWORD = 0x2D;
    
    public final static byte OP_ANDBYTE = 0x2E;
    public final static byte OP_ANDWORD = 0x2F;
    public final static byte OP_ANDDWORD = 0x30;
    
    public final static byte OP_ORBYTE = 0x31;
    public final static byte OP_ORWORD = 0x32;
    public final static byte OP_ORDWORD = 0x33;
    
    public final static byte OP_XORBYTE = 0x34;
    public final static byte OP_XORWORD = 0x35;
    public final static byte OP_XORDWORD = 0x36;
    
    public final static byte OP_SGZBYTE = 0x37;
    public final static byte OP_SGZWORD = 0x38;
    public final static byte OP_SGZDWORD = 0x39;

    public final static byte OP_SGEZBYTE = 0x3A;
    public final static byte OP_SGEZWORD = 0x3B;
    public final static byte OP_SGEZDWORD = 0x3C;
               
    public final static byte OP_SEZBYTE = 0x3D;
    public final static byte OP_SEZWORD = 0x3E;
    public final static byte OP_SEZDWORD = 0x3F;
               
    public final static byte OP_SNEZBYTE = 0x40;
    public final static byte OP_SNEZWORD = 0x41;
    public final static byte OP_SNEZDWORD = 0x42;
        
    public final static byte OP_JMP = 0x43;

    public final static byte OP_JEZ = 0x44;
    public final static byte OP_JNEZ = 0x45;
    
    public final static byte OP_SLLBYTE = 0x46;
    public final static byte OP_SLLWORD = 0x47;
    public final static byte OP_SLLDWORD = 0x48;

    public final static byte OP_SLLVBYTE = 0x49;
    public final static byte OP_SLLVWORD = 0x4A;
    public final static byte OP_SLLVDWORD = 0x4B;    

    public final static byte OP_SRLBYTE = 0x4C;
    public final static byte OP_SRLWORD = 0x4D;
    public final static byte OP_SRLDWORD = 0x4E;
    
    public final static byte OP_SRLVBYTE = 0x4F;
    public final static byte OP_SRLVWORD = 0x50;
    public final static byte OP_SRLVDWORD = 0x51;
    
    public final static byte OP_SRABYTE = 0x52;
    public final static byte OP_SRAWORD = 0x53;
    public final static byte OP_SRADWORD = 0x54;    

    public final static byte OP_SRAVBYTE = 0x55;
    public final static byte OP_SRAVWORD = 0x56;
    public final static byte OP_SRAVDWORD = 0x57;
    
    public byte[] getCode();
    public void fixAddress(int address);
}
