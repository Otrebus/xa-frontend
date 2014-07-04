package se.neava.Assembler;

import java.util.ArrayList;
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
    private ArrayList<Statement> errata = new ArrayList<Statement>();
    
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
    
    public void addErrata(Statement s)
    {
        errata.add(s);
    }
}
