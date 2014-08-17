package se.neava.compiler.symbol;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.RuleContext;

import se.neava.compiler.CodeGenerator;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;
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
            types.add(Type.createType(c));
        signature = types;
        name = methodName;
    }
    
    public MethodSymbol(MethodDefinitionContext ctx)
    {
        List<Type> types = new LinkedList<Type>();
        String methodName = ctx.identifier(0).getText();
        for(TypeContext c : ctx.type())
            types.add(Type.createType(c));
        signature = types;
        name = methodName;
    }
    
    public MethodSymbol(String name, List<Type> signature)
    {
        this.signature = signature;
        this.name = name;
    }
    
    public List<Type> getArguments()
    {
        return signature.subList(1, signature.size());
    }
    
    public int getTotalArgumentSize()
    {
        int size = 0;
        for(int i = 1; i < signature.size(); i++)
            size += signature.get(i).getMemorySize();
        return size + 2;
    }
    
    public Type getReturnType()
    {
        return signature.get(0);
    }
    
    public String getName()
    {
        return name;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
    
    public String getLabel()
    {
        return label;
    }

    public void emitArrayLoad(CodeGenerator codeGenerator) {
        // TODO Auto-generated method stub
        
    }

    public void emitArrayStore(CodeGenerator codeGenerator) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void emitLoad(CodeGenerator codeGenerator) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void emitStore(CodeGenerator codeGenerator) {
        // TODO Auto-generated method stub
        
    }
}
