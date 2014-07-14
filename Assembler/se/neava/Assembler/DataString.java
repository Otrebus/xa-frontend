package se.neava.Assembler;

import java.io.UnsupportedEncodingException;

public class DataString implements Statement {
    byte[] code;
    String str;
    
    DataString(String str)
    {
        this.str = str;
        try 
        {
            // Zero-terminate the string
            this.code = new byte[str.length() + 1];
            
            byte[] strBytes = str.getBytes("UTF-8");
            for(int i = 0; i < strBytes.length; i++)
                this.code[i] = strBytes[i];
            this.code[str.length()] = 0;
        } 
        catch (UnsupportedEncodingException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public byte[] getCode() {
        return code;
    }

    public void addToProgram(Program p) {
        // TODO Auto-generated method stub
        p.addStatement(this);        
    }
    
    public String toString()
    {
        return "\"" + str + "\"";
    }
}
