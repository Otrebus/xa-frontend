package se.neava.compiler.type;

public class BoolType extends Type 
{
    BoolType(boolean isArray)
    {
        this.isArray = isArray;
    }
    
    public int getSize()
    {
        return 1;
    }
}
