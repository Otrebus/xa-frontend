package se.neava.compiler.symbol;

import se.neava.compiler.GravelParser.ClassVariableDeclarationContext;
import se.neava.compiler.type.Type;

public class ClassVariableSymbol extends VariableSymbol 
{
    private String label;
    int position;

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
        type = Type.createType(ctx.type());
    }
    
    public ClassVariableSymbol(ClassVariableDeclarationContext ctx, int position) 
    {
        name = ctx.identifier().getText();
        type = Type.createType(ctx.type());
        this.position = position;
    }

    @Override
    public String emitLoad() {
        return label;
        // TODO Auto-generated method stub
    }

    @Override
    public String emitStore() {
        return label;
        // TODO Auto-generated method stub
    }

    public Type getType() 
    {
        return type;
    }
}
