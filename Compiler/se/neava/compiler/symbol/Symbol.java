package se.neava.compiler.symbol;

import se.neava.compiler.type.Type;

public abstract class Symbol 
{
    String name;
    
    public String getName()
    {
        return name;
    }
    
    abstract String emitLoad();
    abstract String emitStore();
}
