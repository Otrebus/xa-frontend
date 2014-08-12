package se.neava.compiler.type;

public class VoidType extends Type
{
    public VoidType() 
    {
        this.isArray = false;
    }
    
    public int getSize()
    {
        return 0;
    }
    
    public String getSizeStr()
    {
        return "";
    }
}