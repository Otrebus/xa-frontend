package se.neava.compiler.scope;

import se.neava.compiler.GravelParser.MethodDefinitionContext;
import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;

public class MethodScope implements Scope 
{
    Scope parent;
    public MethodScope(Scope parent, MethodDefinitionContext ctx) 
    {
        this.parent = parent;
        ctx.
    }

    public MethodSymbol getMethod(String str) 
    {
        return parent.getMethod(str);
    }

    public ClassInstanceSymbol getClassInstance(String str) 
    {
        return parent.getClassInstance(str);
    }

    public VariableSymbol getVariable(String str) 
    {
        return parent.getVariable(str);
    }

    public ClassScope getClassScope(String str) 
    {
        return parent.getClassScope(str);
    }

    public Scope getParent() 
    {
        return parent;
    }
}
