package se.neava.compiler.type;

public class BoolType extends Type 
{
    public BoolType(boolean isArray)
    {
        this.isArray = isArray;
    }
    
    public int getSize()
    {
        return 1;
    }
    
    public String getSizeStr()
    {
        return "byte";
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof BoolType))
            return false;
        return (isArray == ((BoolType) b).isArray);
    }
}
