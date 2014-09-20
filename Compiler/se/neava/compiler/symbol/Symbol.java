package se.neava.compiler.symbol;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser;
import se.neava.compiler.type.Type;

public abstract class Symbol 
{
    String name;
    
    public String getName()
    {
        return name;
    }
    
    public abstract void emitArrayLoad(CodeGeneratorVisitor codeGenerator);
    public abstract void emitArrayStore(CodeGeneratorVisitor codeGeneratorVisitor);
    
    public abstract void emitLoad(CodeGeneratorVisitor codeGeneratorVisitor);
    public abstract void emitStore(CodeGeneratorVisitor codeGeneratorVisitor);
}
