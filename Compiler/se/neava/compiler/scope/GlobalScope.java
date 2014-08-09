package se.neava.compiler.scope;

import java.util.LinkedList;
import java.util.List;

import se.neava.compiler.GravelParser.ClassDefinitionContext;
import se.neava.compiler.GravelParser.ExternDeclarationContext;
import se.neava.compiler.GravelParser.ProgramContext;
import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;

public class GlobalScope implements Scope 
{
    List<MethodSymbol> externMethods = new LinkedList<MethodSymbol>();
    List<ClassScope> classScopes = new LinkedList<ClassScope>();
    
    public GlobalScope(ProgramContext ctx)
    {
        for(ExternDeclarationContext e : ctx.externDeclaration())
            externMethods.add(new MethodSymbol(e));
        for(ClassDefinitionContext e : ctx.classDefinition())
            classScopes.add(new ClassScope(this, e));
    }
    
    public void addExternMethod(MethodSymbol symbol)
    {
        externMethods.add(symbol);
    }
    
    public void addClassScope(ClassScope scope)
    {
        classScopes.add(scope);
    }
    
    public ClassScope getClassScope(String str)
    {
        for(ClassScope c : classScopes)
            if(str.equals(c.getName()))
                return c;
        return null;
    }

    @Override
    public MethodSymbol getMethod(String str) {
        for(MethodSymbol s : externMethods)
            if(str.equals(s.getName()))
                return s;
        return null;
    }

    @Override
    public ClassInstanceSymbol getClassInstance(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VariableSymbol getVariable(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Scope getParent() 
    {
        return null;
    }
}