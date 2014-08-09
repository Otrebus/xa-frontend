package se.neava.compiler.scope;

import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;

public class MethodScope implements Scope 
{
    Scope parent;
    @Override
    public MethodSymbol getMethod(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ClassInstanceSymbol getClassInstance(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VariableSymbol getVariable(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ClassScope getClassScope(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Scope getParent() 
    {
        return parent;
    }

}
