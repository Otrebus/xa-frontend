package se.neava.compiler.type;

import se.neava.compiler.CodeGenerator;
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
    
    public boolean castTo(CodeGenerator gen, Type type)
    {
        if(type instanceof IntType)
            return true;
        else if(type instanceof LongType)
        {
            gen.emitProgramString("push 2");
            gen.emitProgramString("sll word 16");
            return true;
        }
        else if(type instanceof CharType)
        {
            gen.emitProgramString("srl dword 24");
            gen.emitProgramString("pop 3");
            return true;
        }
        return false;
    }
    
    public Type clone()
    {
        return new IntType(this);
    }
}
