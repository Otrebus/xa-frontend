package se.neava.Assembler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import se.neava.Assembler.instruction.Instruction;

/**
 * Class representing an assembly language program.
 */
public class Program 
{
    private static final int headerSize = 8;
    int entryObjectPos = -1; // Position of the entry object
    int entryPos = -1;       // Position of the entry code
    int externPos = -1;      // Position of the extern strings
    int codeSegmentPos = -1;     // Position of the code segment
    
    int pos = 0;             // Current position in the program that's being parsed
    
    // All labels and their program positions
    private Map<String, Integer> labels = new TreeMap<String, Integer>();
    // The statements comprising a program
    private ArrayList<Statement> statements = new ArrayList<Statement>();
    // The errata for instructions that referenced labels not yet defined at that point
    private HashMap<Instruction, String> errata = new HashMap<Instruction, String>();
    
    /**
     * Associates a label with the current position in the program.
     * @param label The label name.
     */
    public void addLabel(String label)
    {
        labels.put(label, pos);
    }
    
    /**
     * Gets the address (position) of a given label.
     * @param label The label name.
     * @return -1 if the label was not found, otherwise the position of the label.
     */
    public int getAddress(String label)
    {
        if(labels.containsKey(label))
            return labels.get(label);
        else
            return -1;
    }

    /**
     * Adds a statement to the current position in the program, and advances the position.
     * @param s The statement to add.
     */
    public void addStatement(Statement s) 
    {
        statements.add(s);
        pos += s.getCode().length;
    }
    
    /**
     * If this method is called before the code segment of a program, it sets the entry object to
     * the current line of the program. Otherwise it sets the entry point in the code. 
     */
    public void setEntry()
    {
        if(codeSegmentPos == -1)
            entryObjectPos = pos;
        else
            entryPos = pos;
    }
    
    /**
     * Sets the start of the extern section to the current position of the program.
     */
    public void setExtern()
    {
        externPos = pos;
    }
    
    /**
     * Returns the position of the entry object.
     * @return the position of the entry object.
     */
    public int getEntryObject()
    {
        return entryObjectPos;
    }

    /**
     * Returns the position of the entry point of the code.
     * @return the position of the entry point of the code.
     */
    public int getEntryPoint()
    {
        return entryPos;
    }
    
    /**
     * Returns the start of the extern section.
     * @return the start of the extern section.
     */
    public int getExtern()
    {
        return externPos;
    }
    
    /**
     * Sets the code segment of the program to this position.
     */
    public void setCodeSegment() 
    {
        codeSegmentPos = pos;   
    }
    
    /**
     * Returns the code segment of the program.
     * @return
     */
    public int getCodeSegment()
    {
        return codeSegmentPos;
    }
    
    /**
     * Adds an errata entry to the program, which is an instance of an instruction that referred
     * to an undeclared label.
     * @param i The instruction that referred to the undeclared label. 
     * @param label The name of the label in question.
     */
    public void addErrata(Instruction i, String label)
    {
        errata.put(i, label);
    }
    
    /**
     * Fixes the errata entries by substituting the referred labels by the actual addresses.
     * @throws ParseException
     */
    public void fixErrata() throws ParseException
    {
        for(Map.Entry<Instruction,String> entry : errata.entrySet())
        {
            int addr = getAddress(entry.getValue());
            if(addr == -1)
                throw new ParseException("Could not find label \"" + entry.getValue() + "\"", 0);
            entry.getKey().fixAddress(addr);
        }
    }
    
    /**
     * Returns the bytecode representation of this program.
     * @return
     */
    public byte[] getCode()
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(pos + headerSize);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort((short) entryObjectPos);
        byteBuffer.putShort((short) codeSegmentPos);
        byteBuffer.putShort((short) entryPos);
        byteBuffer.putShort((short) externPos);
        for(Statement s : statements)
            byteBuffer.put(s.getCode());
        return byteBuffer.array();
    }
    
    /**
     * Converts an array of bytes to a string of space separated hexadecimal 2-digit values. 
     * @param bytes The array of bytes to convert.
     * @return A string of space separated hexadecimal 2-digit values with the elements of the
     *         parameter as said values.
     */
    static String bytesToString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
    
    /**
     * Returns the program in text form.
     */
    public String toString()
    {
        System.out.println("Entry object at: " + String.format("0x%4s", Integer.toHexString(entryObjectPos)).replace(' ', '0'));
        System.out.println("Code starts at: " + String.format("0x%4s", Integer.toHexString(codeSegmentPos)).replace(' ', '0'));
        System.out.println("Entry point is: " + String.format("0x%4s", Integer.toHexString(entryPos)).replace(' ', '0'));
        System.out.println("Extern vars after: " + String.format("0x%4s", Integer.toHexString(externPos)).replace(' ', '0'));
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
