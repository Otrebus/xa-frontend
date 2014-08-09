package se.neava.compiler.type;

import java.util.LinkedList;
import java.util.List;

import se.neava.compiler.GravelParser;
import se.neava.compiler.GravelParser.FunctionPtrContext;

public class FunctionPointerType extends Type 
{
    public FunctionPointerType(FunctionPtrContext ctx)
    {
        List<Type> signature = new LinkedList<Type>();
        int nTypes = ctx.type().size();
        signature.add(Type.CreateType(ctx.type(nTypes - 1)));
        
        for(int i = 0; i < ctx.type().size() - 1; i++)
            signature.add(Type.CreateType(ctx.type(i)));
    }
}
