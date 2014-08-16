package se.neava.compiler.symbol;

import se.neava.compiler.CodeGenerator;
import se.neava.compiler.GravelParser;
import se.neava.compiler.type.Type;

public abstract class Symbol 
{
    String name;
    
    public String getName()
    {
        return name;
    }
    
    public abstract void emitArrayLoad(CodeGenerator codeGenerator);
    public abstract void emitArrayStore(CodeGenerator codeGenerator);
    
    public abstract void emitLoad(CodeGenerator codeGenerator);
    public abstract void emitStore(CodeGenerator codeGenerator);
}
