package se.neava.compiler.symbol;

import se.neava.compiler.type.Type;

public abstract class Symbol 
{
    String name;
    
    String getName()
    {
        return name;
    }
    
    abstract void emitLoad();
    abstract void emitStore();
    abstract Type getType();
}
