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
        signature.add(Type.createType(ctx.type(nTypes - 1)));
        
        for(int i = 0; i < ctx.type().size() - 1; i++)
            signature.add(Type.createType(ctx.type(i)));
    }
    
    public int getSize()
    {
        return 2;
    }
    
    public String getSizeStr()
    {
        return "word";
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
    public
    String popTo(int fpOffset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public
    String popTo(String label) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public
    String pushFrom(int fpOffset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public
    String pushFrom(String label) {
        // TODO Auto-generated method stub
        return null;
    }
}
