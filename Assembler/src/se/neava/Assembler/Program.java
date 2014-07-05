package se.neava.Assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Program 
{
    enum Section { DATA, CODE, EXTERN };
    Section currentSection;
    int dataSection = -1;
    int codeSection = -1;
    int externSection = -1;
    
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
    
    public void setSection(String str)
    {
        assert(str.equals("data") || str.equals("code") || str.equals("extern"));
        if(str.equals("data"))
        {
            dataSection = pos;
            currentSection = Section.DATA;
        }
        if(str.equals("code"))
        {
            codeSection = pos;
            currentSection = Section.CODE;
        }
            
        if(str.equals("extern"))
        {
            externSection = pos;
            currentSection = Section.EXTERN;
        }
    }
    
    public void addErrata(Instruction i, String label)
    {
        errata.put(i, label);
    }
    
    public void fixErrata()
    {
        for(Map.Entry<Instruction,String> entry : errata.entrySet())
            entry.getKey().fixAddress(getAddress(entry.getValue()));
    }
    
    public byte[] getCode()
    {
        byte[] bytes = new byte[pos];
        int i = 0;
        for(Statement s : statements)
        {
            byte[] code = s.getCode();
            for(int j = 0; j < code.length; j++)
                bytes[i + j] = code[j];
            i += code.length;
        }
        return bytes;
    }
    
    public String bytesToString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
    
    public String toString()
    {
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
