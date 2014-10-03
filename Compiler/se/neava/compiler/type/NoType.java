package se.neava.compiler.type;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;

public class NoType extends Type implements Cloneable {

    public NoType(NoType voidType) 
    {
        super((Type) voidType);
    }
    
    public NoType() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getSizeStr()
    {
        return "dword";
    }

    
    public Type clone()
    {
        return new NoType(this);
    }
    
    public boolean isAssignableFrom(Type type)
    {
        return false;
    }

    @Override
    public Type pushFrom(CodeGeneratorVisitor codeGen, ArrayLookupExpContext ctx) {
        return new NoType();
    }
    

    
    public boolean castTo(CodeGeneratorVisitor gen, Type type)
    {
        return false;
    }
}
