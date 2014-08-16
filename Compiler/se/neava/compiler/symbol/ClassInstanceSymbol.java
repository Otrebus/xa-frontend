package se.neava.compiler.symbol;

import se.neava.compiler.CodeGenerator;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;
import se.neava.compiler.scope.ClassScope;
import se.neava.compiler.type.Type;

public class ClassInstanceSymbol extends Symbol 
{
    ClassScope scope;
    String label;
    
    public ClassInstanceSymbol(ClassScope scope, String name)
    {
        this.scope = scope;
        this.name = name;
    }
    
    public void setLabel(String label)
    {
        this.label = label;
    }
    
    public String getLabel()
    {
        return label;
    }
    
    public ClassScope getClassScope()
    {
        return scope;
    }

    @Override
    public void emitArrayLoad(CodeGenerator codeGenerator) {
        // TODO Auto-generated method stub
        
    }



    @Override
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
