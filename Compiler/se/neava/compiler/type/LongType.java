package se.neava.compiler.type;

public class LongType extends Type 
{
    LongType(boolean isArray)
    {
        this.isArray = isArray;
    }
    
    public int getSize()
    {
        return 4;
    }
}
