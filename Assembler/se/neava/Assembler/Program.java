package se.neava.Assembler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Program 
{
    private static final int headerSize = 6;
    int entry = -1;
    int extern = -1;
    int program = -1;
    
    int pos = 0;
    private Map<String, Integer> labels = new TreeMap<String, Integer>();
    private ArrayList<Statement> statements = new ArrayList<Statement>();
    private HashMap<Instruction, String> errata = new HashMap<Instruction, String>();
    
    public void addLabel(String label)
    {
        labels.put(label, pos);
    }
    
    int getAddress(String label)
    {
        if(labels.containsKey(label))
            return labels.get(label);
        else
            return -1;
    }

    public void addStatement(Statement s) {
        statements.add(s);
        pos += s.getCode().length;
    }
    
    public void setEntry()
    {
        entry = pos;
    }
    
    public void setExtern()
    {
        extern = pos;
    }
    
    public int getEntry()
    {
        return entry;
    }
    
    public int getExtern()
    {
        return extern;
    }
    
    public void setProgram() 
    {
        program = pos;   
    }
    
    public int getProgram()
    {
        return program;
    }
    
    public void addErrata(Instruction i, String label)
    {
        errata.put(i, label);
    }
    
    public void fixErrata() throws ParseException
    {
        for(Map.Entry<Instruction,String> entry : errata.entrySet())
        {
            int addr = getAddress(entry.getValue());
            if(addr == -1)
                throw new ParseException("could not find label \"" + entry.getValue() + "\"", 0);
            entry.getKey().fixAddress(addr);
        }
    }
    
    public byte[] getCode()
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(pos + headerSize);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort((short) program);
        byteBuffer.putShort((short) entry);
        byteBuffer.putShort((short) extern);
        for(Statement s : statements)
            byteBuffer.put(s.getCode());
        return byteBuffer.array();
    }
    
    // TODO: remove this 
    static public String bytesToString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
    
    public String toString()
    {
        System.out.println("Code starts at: " + String.format("0x%4s", Integer.toHexString(program)).replace(' ', '0'));
        System.out.println("Entry point is: " + String.format("0x%4s", Integer.toHexString(entry)).replace(' ', '0'));
        System.out.println("Extern vars after: " + String.format("0x%4s", Integer.toHexString(extern)).replace(' ', '0'));
        String retstr = "";
        int i = 0;
        for(Statement s : statements)
        {
            byte[] code = s.getCode();
            String addr = String.format("0x%4s", Integer.toHexString(i)).replace(' ', '0');
            retstr += String.format("%s: %-40s %s \n", addr, s.toString(), bytesToString(code));
            i += s.getCode().length;
        }
        return retstr;
    }
}
