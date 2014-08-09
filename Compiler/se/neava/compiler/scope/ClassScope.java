package se.neava.compiler.scope;

import java.util.LinkedList;
import java.util.List;

import se.neava.compiler.GravelParser.ClassDefinitionContext;
import se.neava.compiler.GravelParser.ClassVariableDeclarationContext;
import se.neava.compiler.GravelParser.MethodDefinitionContext;
import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.ClassVariableSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;

public class ClassScope implements Scope 
{
    Scope parent;
    String className;
    String label;
    List<MethodSymbol> methodSymbols = new LinkedList<MethodSymbol>();
    List<ClassVariableSymbol> variableSymbols = new LinkedList<ClassVariableSymbol>();
    
    public ClassScope(Scope parent, ClassDefinitionContext ctx)
    {
        this.parent = parent;
        for(MethodDefinitionContext c : ctx.methodDefinition())
            methodSymbols.add(new MethodSymbol(c));
        for(ClassVariableDeclarationContext c : ctx.classVariableDeclaration())
            variableSymbols.add(new ClassVariableSymbol(c));
        className = ctx.identifier().getText();
    }
    
    public String getName()
    {
        return className;
    }

    @Override
    public MethodSymbol getMethod(String str) 
    {
        for(MethodSymbol s : methodSymbols)
            if(str.equals(s.getName()))
                return s;
        return parent.getMethod(str);
    }

    @Override
    public ClassInstanceSymbol getClassInstance(String str) 
    {
        return parent.getClassInstance(str);
    }

    @Override
    public VariableSymbol getVariable(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ClassScope getClassScope(String str) 
    {
        if(className.equals(str))
            return this;
        return parent.getClassScope(str);
    }

    @Override
    public Scope getParent() 
    {
        return parent;
    }

    public void setLabel(String lbl) 
    {
        label = lbl;
    }
}