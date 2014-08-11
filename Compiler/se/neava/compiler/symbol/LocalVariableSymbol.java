package se.neava.compiler.symbol;

import se.neava.compiler.type.Type;

public class LocalVariableSymbol extends VariableSymbol 
{
    Type type;
    
    public LocalVariableSymbol(String name, Type type)
    {
        this.name = name;
        this.type = type;
    }
    
    @Override
    public void emitLoad() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void emitStore() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Type getType() {
        // TODO Auto-generated method stub
        return type;
    }

}
