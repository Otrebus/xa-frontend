package se.neava.compiler.symbol;

import se.neava.compiler.GravelParser.ClassVariableDeclarationContext;
import se.neava.compiler.type.Type;

public class ClassVariableSymbol extends VariableSymbol 
{
    private String label;

    public void setLabel(String label)
    {
        this.label = label; 
    }
    
    public ClassVariableSymbol(ClassVariableDeclarationContext ctx) 
    {
        name = ctx.identifier().getText();
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
        return null;
    }

}
