package se.neava.compiler.type;

import se.neava.compiler.CodeGenerator;
import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;


public class CharType extends Type implements Cloneable {

    public CharType(CharType voidType) 
    {
        super((Type) voidType);
        size = 1;
    }
    
    public CharType(boolean isArray) 
    {
        this.isArray = isArray;
        size = 1;
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof CharType))
            return false;
        return (isArray == ((CharType) b).isArray);
    }
    
    public boolean isAssignableFrom(Type type)
    {
        if(!(type instanceof CharType))
            return false;
        if(isArray && (arrayLength != 0 || !type.isArray))
            return false;
        return true;            
    }
    
    public boolean castTo(CodeGenerator gen, Type type)
    {
        if(type instanceof CharType)
            return true;
        else if(type instanceof LongType)
        {
            gen.emitProgramString("push 3");
            gen.emitProgramString("sll word 24");
            return true;
        }
        else if(type instanceof IntType)
        {
            gen.emitProgramString("push 1");
            gen.emitProgramString("srl word 8");
            return true;
        }
        return false;
    }
    
    public Type clone()
    {
        return new CharType(this);
    }
}
