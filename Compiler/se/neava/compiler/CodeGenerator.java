package se.neava.compiler;

import java.util.Set;
import java.util.TreeSet;

public class CodeGenerator 
{
    String dataString = "";
    String programString = ".program\n";
    String externString = ".extern\n";
    Set<String> labels = new TreeSet<String>();
    
    public String getCode()
    {
        return dataString + programString + externString;
    }
    
    public void emitProgramLabel(String str)
    {
        programString += str + ":\n";
    }
    
    public void emitProgramDirective(String str)
    {
        programString += str + "\n";
    }
    
    public void emitProgramString(String str)
    {
        programString += "  " + str + "\n";
    }
    
    public void emitDataLabel(String str)
    {
        dataString += str + ":\n";
    }
    
    public void emitDataln(String str)
    {
        dataString += "  " + str + "\n";
    }
    
    public void emitDataDirective(String str)
    {
        dataString += str + "\n";
    }
    
    public void emitExternLabel(String str)
    {
        externString += str + ":\n";
    }
    
    public void emitExternln(String str)
    {
        externString += "  " + str + "\n";
    }
    
    public void emitExternDirective(String str)
    {
        externString += str + "\n";
    }
    
    public String makeLabel(String suggestion)
    {
        String str = suggestion;
        for(int n = 2; labels.contains(str); n++)
            str = suggestion + n;
        return str;
    }
}
