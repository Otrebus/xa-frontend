package se.neava.compiler.scope;

import java.util.LinkedList;
import java.util.List;

import se.neava.compiler.GravelParser.MethodDefinitionContext;
import se.neava.compiler.symbol.ArgumentVariableSymbol;
import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.LocalVariableSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;
import se.neava.compiler.type.Type;

public class MethodScope implements Scope 
{
    List<VariableSymbol> localVariables = new LinkedList<VariableSymbol>();
    Scope parent;
    String name;
    Type returnType;
    MethodSymbol methodSymbol;
    
    int argumentVariableSize = 0;
    int localVariableSize = 0;
    
    public MethodScope(ClassScope parent, MethodDefinitionContext ctx) 
    {
        if(!parent.isObject())
            argumentVariableSize = 2;
        this.parent = parent;
        returnType = Type.createType(ctx.type(0));
        name = ctx.identifier(0).getText();
        
        for(int i = 1; i < ctx.identifier().size(); i++)
        {
            Type type = Type.createType(ctx.type(i));
            String name = ctx.identifier(i).getText();
            localVariables.add(new ArgumentVariableSymbol(name, type, argumentVariableSize));
            argumentVariableSize += type.getMemorySize();
        }
        methodSymbol = getMethod(name);
    }
    
    public String getName()
    {
        return name;
    }

    public MethodSymbol getMethod(String str) 
    {
        return parent.getMethod(str);
    }
    
    public MethodSymbol getExternMethod(String str)
    {
        return parent.getExternMethod(str);
    }
    
    public MethodSymbol getMethod()
    {
        return methodSymbol;
    }

    public ClassInstanceSymbol getClassInstance(String str) 
    {
        return parent.getClassInstance(str);
    }

    public VariableSymbol getVariable(String str) 
    {
        for(VariableSymbol s : localVariables)
            if(s.getName().equals(str))
                return s;
        return parent.getVariable(str);
    }
    
    public boolean addVariable(String str, Type type)
    {
        for(VariableSymbol s : localVariables)
            if(s.getName().equals(str))
                return false;
        localVariableSize += type.getMemorySize();
        localVariables.add(new LocalVariableSymbol(str, type, localVariableSize));
        return true;
    }
    
    public int getLocalVariableSize()
    {
        return localVariableSize;
    }

    public ClassScope getClassScope(String str) 
    {
        return parent.getClassScope(str);
    }

    public Scope getParent() 
    {
        return parent;
    }
    
    public ClassScope getClassScope()
    {
        return parent.getClassScope();
    }
    
    public MethodScope getMethodScope()
    {
        return this;
    }
}
