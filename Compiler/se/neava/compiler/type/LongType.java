package se.neava.compiler.type;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;

public class LongType extends Type implements Cloneable
{
    public LongType(LongType voidType) 
    {
        super((Type) voidType);
        size = 4;
    }
    
    public LongType(boolean isArray)
    {
        this.isArray = isArray;
        size = 4;
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof LongType))
            return false;
        return (isArray == ((LongType) b).isArray);
    }
    
    public boolean isAssignableFrom(Type type)
    {
        if(!(type instanceof LongType))
            return false;
        if(isArray && (arrayLength != 0 || !type.isArray))
            return false;
        return true;            
    }
    
    public Type clone()
    {
        return new LongType(this);
    }
}
