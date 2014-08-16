package se.neava.compiler.type;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;
import se.neava.compiler.GravelParser.BaseTypeContext;
import se.neava.compiler.GravelParser.TypeContext;

public abstract class Type implements Cloneable
{
    public static final int BOOL = 0;
    public static final int CHAR = 1;
    public static final int INT = 2;
    public static final int LONG = 3;
    public static final int FUNCTIONPTR = 4;
    public static final int VOID = 5;
    public static final int CLASS = 6;
    public boolean isArray;
    int arrayLength;
    
    public Type()
    {}
    
    public Type(Type voidType) {
        isArray = voidType.isArray;
        arrayLength = voidType.arrayLength;
    }

    public int getArrayLength()
    {
        return arrayLength;
    }

    public static Type createType(TypeContext ctx)
    {
        Type type = createType(ctx.baseType());
        type.isArray = ctx.brackets() == null ? false : true;
        if(type.isArray)
        {
            if(ctx.brackets().NUM() != null)
            {
                type.arrayLength = Integer.parseInt(ctx.brackets().NUM().getText());
            }
        }   
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
    
    public int getMemorySize()
    {
        if(arrayLength > 0)
            return arrayLength*getSize();
        else if(isArray)
            return 2;
        return getSize();
    }
    
    abstract public int getSize();
    abstract public String getSizeStr();
    
    public abstract String popTo(int fpOffset);
    public abstract String popTo(String label);
    
    public abstract String pushFrom(int fpOffset);
    public abstract String pushFrom(String label);
    
    public abstract Type pushFrom(CodeGeneratorVisitor codeGen, ArrayLookupExpContext ctx);
    
    public abstract Type clone();
}