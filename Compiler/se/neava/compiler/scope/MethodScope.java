package se.neava.compiler.scope;

import java.util.LinkedList;
import java.util.List;
import se.neava.compiler.GravelParser.MethodDefinitionContext;
import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.LocalVariableSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;
import se.neava.compiler.type.Type;

public class MethodScope implements Scope 
{
    List<LocalVariableSymbol> localVariables = new LinkedList<LocalVariableSymbol>();
    Scope parent;
    String name;
    Type returnType;
    MethodSymbol methodSymbol;
    
    public MethodScope(Scope parent, MethodDefinitionContext ctx) 
    {
        this.parent = parent;
        returnType = Type.CreateType(ctx.type(0));
        name = ctx.identifier(0).getText();
        
        for(int i = 1; i < ctx.identifier().size(); i++)
        {
            Type type = Type.CreateType(ctx.type(i));
            String name = ctx.identifier(i).getText();
            localVariables.add(new LocalVariableSymbol(name, type));
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
        for(LocalVariableSymbol s : localVariables)
            if(s.getName().equals(str))
                return s;
        return parent.getVariable(str);
    }
    
    public boolean addVariable(String str, Type type)
    {
        for(LocalVariableSymbol s : localVariables)
            if(s.getName().equals(str))
                return false;
        localVariables.add(new LocalVariableSymbol(str, type));
        return true;
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
