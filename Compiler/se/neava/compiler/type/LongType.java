package se.neava.compiler.type;

public class LongType extends Type 
{
    public LongType(boolean isArray)
    {
        this.isArray = isArray;
    }
    
    public int getSize()
    {
        return 4;
    }
    
    public boolean equals(Type b)
    {
        if(!(b instanceof LongType))
            return false;
        return (isArray == ((LongType) b).isArray);
    }
}
