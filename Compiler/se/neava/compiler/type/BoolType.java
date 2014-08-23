package se.neava.compiler.type;

import se.neava.compiler.CodeGenerator;
import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;

public class BoolType extends Type implements Cloneable 
{
    public BoolType(BoolType type)
    {
        super((Type) type);
        size = 1;
    }
    
    public BoolType()
    {
        size = 1;
        isArray = false;
    }
    
    public BoolType(boolean isArray)
    {
        this.isArray = isArray;
        size = 1;
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof BoolType))
            return false;
        return (isArray == ((BoolType) b).isArray);
    }
    
    public Type clone()
    {
        return new BoolType(this);
    }
    
    public boolean castTo(CodeGenerator gen, Type type)
    {
        if(type instanceof BoolType)
            return true;
        return false;
    }
    
    public boolean isAssignableFrom(Type type)
    {
        if(!(type instanceof BoolType))
            return false;
        if(isArray && (arrayLength != 0 || !type.isArray))
            return false;
        return true;            
    }
}