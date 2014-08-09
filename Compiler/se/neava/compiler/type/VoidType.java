package se.neava.compiler.type;

public class VoidType extends Type
{
    public VoidType(boolean isArray) 
    {
        this.isArray = isArray;
    }
    
    public int getSize()
    {
        return 0;
    }
}
