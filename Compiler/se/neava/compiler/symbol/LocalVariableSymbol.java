package se.neava.compiler.symbol;

import se.neava.compiler.type.Type;

public class LocalVariableSymbol extends VariableSymbol 
{
    Type type;
    int position;
    
    public LocalVariableSymbol(String name, Type type, int position)
    {
        this.name = name;
        this.type = type;
        this.position = position;
    }
    
    public String emitLoad() 
    {
        return type.pushFrom(-position);        
    }

    public String emitStore() 
    {
        return type.popTo(-position);
    }

    @Override
    public Type getType() {
        // TODO Auto-generated method stub
        return type;
    }
}
