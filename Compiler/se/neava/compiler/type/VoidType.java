package se.neava.compiler.type;

import se.neava.compiler.CodeGenerator;
import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;

public class VoidType extends Type
{
    public VoidType() 
    {
        this.isArray = false;
    }
    
    public VoidType(VoidType voidType) 
    {
        super((Type) voidType);
    }

    public int getSize()
    {
        return 0;
    }
    
    public String getSizeStr()
    {
        return "";
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof VoidType))
            return false;
        return (isArray == ((VoidType) b).isArray);
    }

    public Type pushFrom(CodeGeneratorVisitor cgv, ArrayLookupExpContext ctx)
    {
        return this;
    }
    
    public boolean castTo(CodeGenerator gen, Type type)
    {
        return false;
    }
    
    public boolean isAssignableFrom(Type type)
    {
        return false;            
    }
    
    public Type clone()
    {
        return new VoidType(this);
    }
}