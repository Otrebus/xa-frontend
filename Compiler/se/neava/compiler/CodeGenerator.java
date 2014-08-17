package se.neava.compiler;

import java.util.Set;
import java.util.TreeSet;

public class CodeGenerator 
{
    String dataString = "";
    String programString = ".program\n";
    String externString = ".extern\n";
    Set<String> labels = new TreeSet<String>();
    public boolean mute;
    int labelNo = 0;
    
    public String makeLabel()
    {
        String lbl = "label" + labelNo++;
        if(labels.contains(lbl))
            return makeLabel();
        return lbl;
    }
    
    public String getCode()
    {
        return dataString + programString + externString;
    }
    
    public void emitProgramLabel(String str)
    {
        if(!mute)
            programString += str + ":\n";
    }
    
    public void emitProgramDirective(String str)
    {
        if(!mute)
            programString += str + "\n";
    }
    
    public void emitProgramString(String str)
    {
        if(!mute)
            programString += "  " + str + "\n";
    }
    
    public void emitDataLabel(String str)
    {
        if(!mute)
            dataString += str + ":\n";
    }
    
    public void emitDataln(String str)
    {
        if(!mute)
            dataString += "  " + str + "\n";
    }
    
    public void emitDataDirective(String str)
    {
        if(!mute)
            dataString += str + "\n";
    }
    
    public void emitExternLabel(String str)
    {
        if(!mute)
            externString += str + ":\n";
    }
    
    public void emitExternln(String str)
    {
        if(!mute)
            externString += "  " + str + "\n";
    }
    
    public void emitExternDirective(String str)
    {
        if(!mute)
            externString += str + "\n";
    }
    
    public String makeLabel(String suggestion)
    {
        String str = suggestion;
        for(int n = 2; labels.contains(str); n++)
            str = suggestion + n;
        return str;
    }
    
    public void mute()
    {
        mute = true;
    }
    
    public void unmute()
    {
        mute = false;
    }
    
}
