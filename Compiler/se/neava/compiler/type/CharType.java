package se.neava.compiler.type;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;


public class CharType extends Type implements Cloneable {

    public CharType(CharType voidType) 
    {
        super((Type) voidType);
    }
    
    public CharType(boolean isArray) {
        this.isArray = isArray;
    }

    public int getSize()
    {
        return 1;
    }
    
    public String getSizeStr()
    {
        return "byte";
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof CharType))
            return false;
        return (isArray == ((CharType) b).isArray);
    }
    
    public String popTo(int fpOffset)
    {
        String sgn = fpOffset >= 0 ? "" : "-";  
        return "pop " + getSizeStr() + "[$fp" + sgn + Math.abs(fpOffset) + "]";
    }
    
    public String popTo(String label)
    {
        return "pop " + getSizeStr() + " [" + label + "]";
    }
    
    public String pushFrom(int fpOffset)
    {
        String sgn = fpOffset >= 0 ? "+" : "-";  
        return "push " + getSizeStr() + "[$fp" + sgn + Math.abs(fpOffset) + "]";
    }
    
    public String pushFrom(String label)
    {
        return "push " + getSizeStr() + " [" + label + "]";
    }
    
    public Type pushFrom(CodeGeneratorVisitor cgv, ArrayLookupExpContext ctx)
    {
        Type a = cgv.visit(ctx.expression(0));
        Type b = cgv.visit(ctx.expression(1));
        if(!a.isArray())
        {
            cgv.reportError(ctx, "Array lookup on non-array type");
            return new NoType();
        }
        if(!(b instanceof IntType))
        {
            cgv.reportError(ctx, "Array index must be of type int");
            return new NoType();
        }
        cgv.getCodeGenerator().emitProgramString("push word " + getSize());
        cgv.getCodeGenerator().emitProgramString("mul word");
        cgv.getCodeGenerator().emitProgramString("add word");
        cgv.getCodeGenerator().emitProgramString("push byte");
        return this;
    }
    
    public Type clone()
    {
        return new CharType(this);
    }
}
