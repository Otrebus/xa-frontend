package se.neava.compiler.type;

import java.util.LinkedList;
import java.util.List;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;
import se.neava.compiler.GravelParser.FunctionPtrContext;
import se.neava.compiler.symbol.MethodSymbol;

public class FunctionPointerType extends Type implements Cloneable 
{
    List<Type> signature = new LinkedList<Type>();
    public FunctionPointerType(FunctionPtrContext ctx)
    {
        int nTypes = ctx.type().size();
        signature.add(Type.createType(ctx.type(nTypes - 1)));
        
        for(int i = 0; i < ctx.type().size() - 1; i++)
            signature.add(Type.createType(ctx.type(i)));
        size = 4;
    }
    
    public FunctionPointerType(MethodSymbol methodSymbol)
    {
        signature.add(methodSymbol.getReturnType());
        
        for(int i = 0; i < methodSymbol.getArguments().size(); i++)
            signature.add(methodSymbol.getArguments().get(i));
        size = 4;
    }
    
    public FunctionPointerType(List<Type> signature)
    {
        this.signature = signature;
        size = 4;
    }
    
    public FunctionPointerType(FunctionPointerType type) 
    {
        super((Type) type);
        size = 4;
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof FunctionPointerType))
            return false;
        FunctionPointerType bb = (FunctionPointerType) b;        
        if(bb.signature.size() != signature.size())
            return false;
        for(int i = 0; i < bb.signature.size(); i++)
            if(!signature.get(i).equals(bb.signature.get(i)))
                return false;
        return true;
    }

    @Override
    public Type pushFrom(CodeGeneratorVisitor codeGen, ArrayLookupExpContext ctx) {
        // TODO Auto-generated method stub
        return this;
    }
    
    public boolean isAssignableFrom(Type type)
    {
        if(!(type instanceof FunctionPointerType))
            return false;
        if(isArray && (arrayLength != 0 || !type.isArray))
            return false;
        return true;            
    }
    
    public Type clone()
    {
        return new FunctionPointerType(this);
    }
}
