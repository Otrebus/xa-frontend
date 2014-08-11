package se.neava.compiler.symbol;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.RuleContext;

import se.neava.compiler.GravelParser.ClassDefinitionContext;
import se.neava.compiler.GravelParser.ExternDeclarationContext;
import se.neava.compiler.GravelParser.MethodDefinitionContext;
import se.neava.compiler.GravelParser.TypeContext;
import se.neava.compiler.type.Type;

public class MethodSymbol extends Symbol 
{
    List<Type> signature;
    String name;
    String label;
    
    public MethodSymbol(ExternDeclarationContext ctx)
    {
        List<Type> types = new LinkedList<Type>();
        String methodName = ctx.identifier().getText();
        for(TypeContext c : ctx.type())
            types.add(Type.CreateType(c));
        signature = types;
        name = methodName;
    }
    
    public MethodSymbol(MethodDefinitionContext ctx)
    {
        List<Type> types = new LinkedList<Type>();
        String methodName = ctx.identifier(0).getText();
        for(TypeContext c : ctx.type())
            types.add(Type.CreateType(c));
        signature = types;
        name = methodName;
    }
    
    public MethodSymbol(String name, List<Type> signature)
    {
        this.signature = signature;
        this.name = name;
    }
    
    public List<Type> getSignature()
    {
        return signature;
    }
    
    public String getName()
    {
        return name;
    }

    @Override 
    public void emitLoad() 
    {
    }

    @Override
    public void emitStore() {
        // TODO Auto-generated method stub
        
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
    
    public String getLabel()
    {
        return label;
    }
}
