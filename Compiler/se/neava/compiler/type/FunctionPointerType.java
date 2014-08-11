package se.neava.compiler.type;

import java.util.LinkedList;
import java.util.List;

import se.neava.compiler.GravelParser;
import se.neava.compiler.GravelParser.FunctionPtrContext;

public class FunctionPointerType extends Type 
{
    List<Type> signature = new LinkedList<Type>();
    public FunctionPointerType(FunctionPtrContext ctx)
    {
        int nTypes = ctx.type().size();
        signature.add(Type.CreateType(ctx.type(nTypes - 1)));
        
        for(int i = 0; i < ctx.type().size() - 1; i++)
            signature.add(Type.CreateType(ctx.type(i)));
    }
    
    public int getSize()
    {
        return 2;
    }
    
    public boolean equals(Type b)
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
}
