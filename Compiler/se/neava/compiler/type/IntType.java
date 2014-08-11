package se.neava.compiler.type;

public class IntType extends Type 
{
    public IntType(boolean isArray)
    {
        this.isArray = isArray;
    }
    
    public int getSize()
    {
        return 2;
    }
    
    public boolean equals(Type b)
    {
        if(!(b instanceof IntType))
            return false;
        return (isArray == ((IntType) b).isArray);
    }
}
