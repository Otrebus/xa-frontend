package se.neava.compiler.type;

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
    
    public Type clone()
    {
        return new CharType(this);
    }
}
