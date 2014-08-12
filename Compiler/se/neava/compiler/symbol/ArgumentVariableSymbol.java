package se.neava.compiler.symbol;

import se.neava.compiler.type.Type;

public class ArgumentVariableSymbol extends VariableSymbol 
{
    Type type;
    int position;
    
    public ArgumentVariableSymbol(String name, Type type)
    {
        this.name = name;
        this.type = type;
    }
    
    public ArgumentVariableSymbol(String name, Type type, int position)
    {
        this.name = name;
        this.type = type;
        this.position = position;
    }
    
    public String emitLoad() 
    {
        return type.pushFrom(position + 4);
    }

    public String emitStore() 
    {
        return type.popTo(position + 4);        
    }

    @Override
    public Type getType() {
        // TODO Auto-generated method stub
        return type;
    }
}
