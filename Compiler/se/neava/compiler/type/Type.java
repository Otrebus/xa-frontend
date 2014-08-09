package se.neava.compiler.type;

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

    public static Type CreateType(TypeContext ctx)
    {
        boolean isArray = true;
        if(ctx.brackets() == null)
            isArray = false;
        
        if(ctx.baseType().functionPtr() != null)
            return new FunctionPointerType(ctx.baseType().functionPtr());
        
        String typeStr = ctx.baseType().getText();
        if(typeStr.equals("char"))
            return new CharType(isArray);
        else if(typeStr.equals("int"))
            return new IntType(isArray);
        else if(typeStr.equals("long"))
            return new LongType(isArray);
        else if(typeStr.equals("bool"))
            return new BoolType(isArray);
        else if(typeStr.equals("void"))
            return new VoidType(isArray);
        return null;
    }
}