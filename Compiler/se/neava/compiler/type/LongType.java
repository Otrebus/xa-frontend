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
    
    public boolean castTo(CodeGeneratorVisitor gen, Type type)
    {
        if(type instanceof LongType)
            return true;
        else if(type instanceof IntType)
        {
            gen.emitProgramString("sll dword 16");
            gen.emitProgramString("pop 2");
            return true;
        }
        else if(type instanceof CharType)
        {
            gen.emitProgramString("sll dword 24");
            gen.emitProgramString("pop 3");
            return true;
        }
        return false;
    }
    
    public Type clone()
    {
        return new LongType(this);
    }
}
