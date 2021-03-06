package se.neava.compiler.scope;

import java.util.LinkedList;
import java.util.List;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.CompileException;
import se.neava.compiler.GravelParser.ClassDefinitionContext;
import se.neava.compiler.GravelParser.ExternDeclarationContext;
import se.neava.compiler.GravelParser.ProgramContext;
import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;

public class GlobalScope implements Scope 
{
    List<ClassInstanceSymbol> classInstances = new LinkedList<ClassInstanceSymbol>();
    List<MethodSymbol> externMethods = new LinkedList<MethodSymbol>();
    List<ClassScope> classScopes = new LinkedList<ClassScope>();
    
    public GlobalScope(CodeGeneratorVisitor codeGeneratorVisitor, ProgramContext ctx) throws CompileException
    {
        for(ExternDeclarationContext e : ctx.externDeclaration())
            addExternMethod(new MethodSymbol(e));
        for(ClassDefinitionContext e : ctx.classDefinition())
            addClassScope(new ClassScope(codeGeneratorVisitor, this, e));
    }

    public MethodSymbol getExternMethod(String name)
    {
        for(MethodSymbol m : externMethods)
            if(name.equals(m.getName()))
                return m;
        return null;
    }
    
    public void addExternMethod(MethodSymbol symbol) throws CompileException
    {
        if(getExternMethod(symbol.getName()) != null)
            throw new CompileException("Duplicate extern method " + symbol.getName());
        externMethods.add(symbol);
    }
    
    public void addClassScope(ClassScope scope) throws CompileException
    {
        if(getClassScope(scope.getName()) != null)
            throw new CompileException("Duplicate class " + scope.getName());
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
        for(ClassInstanceSymbol s : classInstances)
            if(str.equals(s.getName()))
                return s;
        return null;
    }
    
    public void addClassInstance(ClassInstanceSymbol sym)
    {
        classInstances.add(sym);
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
    
    public ClassScope getClassScope()
    {
        return null;
    }
    
    public MethodScope getMethodScope()
    {
        return null;
    }
}