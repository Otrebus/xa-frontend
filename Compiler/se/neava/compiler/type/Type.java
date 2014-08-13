package se.neava.compiler.type;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;
import se.neava.compiler.GravelParser.BaseTypeContext;
import se.neava.compiler.GravelParser.TypeContext;

public abstract class Type 
{
    public static final int BOOL = 0;
    public static final int CHAR = 1;
    public static final int INT = 2;
    public static final int LONG = 3;
    public static final int FUNCTIONPTR = 4;
    public static final int VOID = 5;
    public static final int CLASS = 6;
    boolean isArray;

    public static Type createType(TypeContext ctx)
    {
        Type type = createType(ctx.baseType()); // lol
        type.isArray = ctx.brackets() == null ? false : true;
        return type;
    }
    
    public static Type createType(BaseTypeContext ctx)
    {
        if(ctx.functionPtr() != null)
            return new FunctionPointerType(ctx.functionPtr());
        
        String typeStr = ctx.getText();
        if(typeStr.equals("char"))
            return new CharType(false);
        else if(typeStr.equals("int"))
            return new IntType(false);
        else if(typeStr.equals("long"))
            return new LongType(false);
        else if(typeStr.equals(false))
            return new BoolType(false);
        else if(typeStr.equals("void"))
            return new VoidType();
        return null;
    }
    
    public boolean isArray()
    {
        return isArray;
    }
    
    abstract public int getSize();
    abstract public String getSizeStr();
    
    public abstract String popTo(int fpOffset);
    public abstract String popTo(String label);
    
    public abstract String pushFrom(int fpOffset);
    public abstract String pushFrom(String label);
    
    public abstract Type pushFrom(CodeGeneratorVisitor codeGen, ArrayLookupExpContext ctx);
}