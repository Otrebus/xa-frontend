package se.neava.compiler.scope;

import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;

public interface Scope 
{
    public MethodSymbol getMethod(String str);
    public ClassInstanceSymbol getClassInstance(String str);
    public VariableSymbol getVariable(String str);
    public ClassScope getClassScope(String str);
    public Scope getParent();
}
