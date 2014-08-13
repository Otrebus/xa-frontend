package se.neava.compiler.type;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;

public class VoidType extends Type
{
    public VoidType() 
    {
        this.isArray = false;
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

    @Override
    public
    String popTo(int fpOffset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public
    String popTo(String label) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public
    String pushFrom(int fpOffset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public
    String pushFrom(String label) {
        // TODO Auto-generated method stub
        return null;
    }

    public Type pushFrom(CodeGeneratorVisitor cgv, ArrayLookupExpContext ctx)
    {
        return this;
    }
}