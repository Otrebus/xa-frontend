package se.neava.compiler.scope;

import java.util.LinkedList;
import java.util.List;

import se.neava.compiler.CodeGenerator;
import se.neava.compiler.CompileException;
import se.neava.compiler.GravelParser.ClassDefinitionContext;
import se.neava.compiler.GravelParser.ClassVariableDeclarationContext;
import se.neava.compiler.GravelParser.MethodDefinitionContext;
import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.ClassVariableSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.ObjectVariableSymbol;
import se.neava.compiler.symbol.VariableSymbol;

public class ClassScope implements Scope 
{
    private static final int HEADER_SIZE = 4;
    boolean isObject = false;
    GlobalScope parent;
    String className;
    String label;
    List<MethodSymbol> methodSymbols = new LinkedList<MethodSymbol>();
    List<VariableSymbol> variableSymbols = new LinkedList<VariableSymbol>();
    
    public ClassScope(CodeGenerator gen, GlobalScope parent, ClassDefinitionContext ctx) throws CompileException
    {
        String classType = ctx.classType().getText();
        if(classType.equals("object"))
            isObject = true;
        className = ctx.identifier().getText();
        this.parent = parent;
        for(MethodDefinitionContext c : ctx.methodDefinition())
        {
            MethodSymbol s = new MethodSymbol(c);
            if(getMethod(s.getName()) != null)
                throw new CompileException("Duplicate method " + s.getName() + " in class " + className);
            String lbl = gen.makeLabel(className + "_" + s.getName());
            s.setLabel(lbl);
            methodSymbols.add(s);
        }
        if(isObject)
        {
            label = gen.makeLabel(className);
            ClassInstanceSymbol sym = new ClassInstanceSymbol(this, className);
            sym.setLabel(label);
            parent.addClassInstance(sym);
            gen.emitDataLabel(label);
            gen.emitDataln("dword 0");
            for(ClassVariableDeclarationContext c : ctx.classVariableDeclaration())
            {
                String label = gen.makeLabel(className + "_" + c.identifier().getText());;
                ObjectVariableSymbol objectVariableSymbol = new ObjectVariableSymbol(c, label);
                gen.emitDataLabel(label);
                gen.emitDataln("byte[" + objectVariableSymbol.getType().getMemorySize() + "]");
                variableSymbols.add(objectVariableSymbol);
            }            
        }
        else
        {
            int classVarPos = 4; // Synchronization vars are first 
            for(ClassVariableDeclarationContext c : ctx.classVariableDeclaration())
            {
                ClassVariableSymbol classVariableSymbol = new ClassVariableSymbol(c, classVarPos);
                classVarPos += classVariableSymbol.getType().getMemorySize();
                variableSymbols.add(classVariableSymbol);
            }
        }
        className = ctx.identifier().getText();
    }
    
    public MethodSymbol getExternMethod(String str)
    {
        return parent.getExternMethod(str);
    }
    
    public String getName()
    {
        return className;
    }

    public MethodSymbol getMethod(String str) 
    {
        for(MethodSymbol s : methodSymbols)
            if(str.equals(s.getName()))
                return s;
        return null;
    }

    public ClassInstanceSymbol getClassInstance(String str) 
    {
        return parent.getClassInstance(str);
    }
    
    public List<VariableSymbol> getVariableSymbols()
    {
        return variableSymbols;
    }

    public VariableSymbol getVariable(String str) {
        for(VariableSymbol s : variableSymbols)
            if(str.equals(s.getName()))
                return s;
        return parent.getVariable(str);
    }

    public ClassScope getClassScope(String str) 
    {
        if(className.equals(str))
            return this;
        return parent.getClassScope(str);
    }
    
    public ClassScope getClassScope()
    {
        return this;
    }
    
    public MethodScope getMethodScope()
    {
        return null;
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

    public String getLabel()
    {
        return label;
    }
    
    public boolean isObject()
    {
        return isObject;
    }
    
    public int getSize()
    {
        int size = 0;
        for(VariableSymbol s : variableSymbols)
            size += s.getType().getMemorySize();
        return HEADER_SIZE + size;
    }
}