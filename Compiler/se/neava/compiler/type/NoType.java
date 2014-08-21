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

    @Override
    public String popTo(int fpOffset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String popTo(String label) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String pushFrom(int fpOffset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String pushFrom(String label) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type pushFrom(CodeGeneratorVisitor codeGen, ArrayLookupExpContext ctx) {
        // TODO Auto-generated method stub
        return this;
    }
    
    public Type clone()
    {
        return new NoType(this);
    }
}
