package se.neava.compiler.symbol;

import se.neava.compiler.scope.ClassScope;
import se.neava.compiler.type.Type;

public class ClassInstanceSymbol extends Symbol 
{
    ClassScope scope;
    String label;
    
    public void setLabel(String label)
    {
        this.label = label; 
    }
    
    @Override
    public void emitLoad() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void emitStore() {
        // TODO Auto-generated method stub
        
    }

    public ClassScope getClassScope()
    {
        return scope;
    }
}
