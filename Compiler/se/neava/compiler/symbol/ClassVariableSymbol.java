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
    
    public String getLabel(String label)
    {
        return label;
    }
    
    public ClassVariableSymbol(ClassVariableDeclarationContext ctx) 
    {
        name = ctx.identifier().getText();
        type = Type.CreateType(ctx.type());
    }

    @Override
    public void emitLoad() {
        // TODO Auto-generated method stub
    }

    @Override
    public void emitStore() {
        // TODO Auto-generated method stub
    }

    public Type getType() 
    {
        return type;
    }
}
