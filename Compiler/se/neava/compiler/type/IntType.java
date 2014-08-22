package se.neava.compiler.type;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;

public class IntType extends Type implements Cloneable
{
    public IntType(IntType voidType) 
    {
        super((Type) voidType);
        size = 2;
    }
    
    public IntType(boolean isArray)
    {
        this.isArray = isArray;
        size = 2;
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof IntType))
            return false;
        return (isArray == ((IntType) b).isArray);
    }
    
    public boolean isAssignableFrom(Type type)
    {
        if(!(type instanceof IntType))
            return false;
        if(isArray && (arrayLength != 0 || !type.isArray))
            return false;
        return true;            
    }
    
    public Type clone()
    {
        return new IntType(this);
    }
}
