package se.neava.compiler.symbol;

import se.neava.compiler.CodeGenerator;
import se.neava.compiler.GravelParser;
import se.neava.compiler.GravelParser.ClassVariableDeclarationContext;
import se.neava.compiler.type.Type;

public class ObjectVariableSymbol extends VariableSymbol 
{
    private String label;

    public void setLabel(String label)
    {
        this.label = label; 
    }
    
    public String getLabel(String label)
    {
        return label;
    }
    
    public ObjectVariableSymbol(ClassVariableDeclarationContext ctx) 
    {
        name = ctx.identifier().getText();
        type = Type.createType(ctx.type());
    }
    
    public ObjectVariableSymbol(ClassVariableDeclarationContext ctx, String label) 
    {
        name = ctx.identifier().getText();
        type = Type.createType(ctx.type());
        this.label = label;
    }

    public Type getType() 
    {
        return type;
    }

    public void emitLoad(CodeGenerator codeGenerator) 
    {
        if(type.getArrayLength() == 0)
            codeGenerator.emitProgramString("push " + type.getSizeStr() + " [" + label + "]");
        else
            codeGenerator.emitProgramString("push " + label);
    }

    public void emitStore(CodeGenerator codeGenerator) 
    {
        if(type.getArrayLength() == 0)
            codeGenerator.emitProgramString("pop " + type.getSizeStr() + " [" + label + "]");
        else
            codeGenerator.emitProgramString("pop " + label);
    }

    public void emitArrayLoad(CodeGenerator codeGenerator) 
    {
        if(type.getArrayLength() == 0)
        {
            // First, we assume that the index of the array is on top of the stack
            codeGenerator.emitProgramString("push word " + type.getElementSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            emitLoad(codeGenerator);
            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("push " + type.getElementSizeStr()); // Data element now on top
        }
        else
        {
            // First, we assume that the index of the array is on top of the stack
            codeGenerator.emitProgramString("push word " + type.getElementSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push " + label); // Current object ("this")

            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("push " + type.getElementSizeStr()); // Data element now on top
        }
    }

    public void emitArrayStore(CodeGenerator codeGenerator)  
    {
        if(type.getArrayLength() == 0)
        {
            // First, we assume that the index of the array is on top of the stack, and the data to be stored
            // is below that
            codeGenerator.emitProgramString("push word " + type.getElementSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            emitLoad(codeGenerator);
            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("pop " + type.getElementSizeStr()); // Pop to element
        }
        else
        {
            // First, we assume that the index of the array is on top of the stack, and the data to be stored
            // is below that
            codeGenerator.emitProgramString("push word " + type.getElementSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push " + label); // Object pos
            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("pop " + type.getElementSizeStr()); // Pop to element
        }
    }
}