package se.neava.compiler.symbol;

import se.neava.compiler.type.Type;

public abstract class VariableSymbol extends Symbol 
{
    Type type;
    
    public Type getType()
    {
        return type;
    }
}
