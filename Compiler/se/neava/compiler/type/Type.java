package se.neava.compiler.type;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;
import se.neava.compiler.GravelParser.BaseTypeContext;
import se.neava.compiler.GravelParser.TypeContext;

public abstract class Type implements Cloneable
{
    /*public static final int BOOL = 0;
    public static final int CHAR = 1;
    public static final int INT = 2;
    public static final int LONG = 3;
    public static final int FUNCTIONPTR = 4;
    public static final int VOID = 5;
    public static final int CLASS = 6;*/

    public boolean isArray;
    int arrayLength;
    int size;
    
    public int getSize()
    {
        if(isArray)
            return 2;
        return size;
    }
    
    public String getSizeStr()
    {
        if(isArray)
            return "word";
        return getElementSizeStr();
    }
    
    public int getElementSize()
    {
        return size;
    }
    
    public String getElementSizeStr()
    {
        if(size == 1)
            return "byte";
        else if(size == 2)
            return "word";
        else if(size == 4)
            return "dword";
        else
            throw new IllegalArgumentException();
    }

    
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
        else if(typeStr.equals("bool"))
            return new BoolType();
        throw new IllegalArgumentException();
    }
    
    public boolean isArray()
    {
        return isArray;
    }
    
    public boolean isPointer()
    {
        return isArray && arrayLength == 0;
    }
    
    public Type pushFrom(CodeGeneratorVisitor cgv, ArrayLookupExpContext ctx)
    {
        Type b = cgv.visit(ctx.expression(1));
        if(!(b instanceof IntType))
        {
            cgv.reportError(ctx, "Array index must be of type int");
            return new NoType();
        }
        cgv.emitProgramString("push word " + getElementSize());
        cgv.emitProgramString("mul word");
        Type a = cgv.visit(ctx.expression(0));
        if(!a.isArray())
        {
            cgv.reportError(ctx, "Array lookup on non-array type");
            return new NoType();
        }
        cgv.emitProgramString("add word");
        cgv.emitProgramString("push " + getElementSizeStr());
        return this;
    }
    
    public int getMemorySize()
    {
        if(arrayLength > 0)
            return arrayLength*getElementSize();
        else if(isArray)
            return 2;
        return getSize();
    }
    
    public abstract boolean castTo(CodeGeneratorVisitor codeGeneratorVisitor, Type type);    
    public abstract boolean isAssignableFrom(Type type);
    public abstract Type clone();

    public static Type getTypeFromSuffix(String s) 
    {
        Type type;
        if(s.equals("c"))
            type = new CharType(false);
        else if(s.equals("i"))
            type = new IntType(false);
        else if(s.equals("l"))
            type = new LongType(false);
        else
            return null;
        return type;
    }
}