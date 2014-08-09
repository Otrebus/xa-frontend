package se.neava.compiler.type;

public class IntType extends Type 
{
    IntType(boolean isArray)
    {
        this.isArray = isArray;
    }
    
    public int getSize()
    {
        return 2;
    }
}
