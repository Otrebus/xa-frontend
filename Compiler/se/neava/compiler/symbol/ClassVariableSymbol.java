package se.neava.compiler.symbol;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser;
import se.neava.compiler.GravelParser.ClassVariableDeclarationContext;
import se.neava.compiler.type.Type;

public class ClassVariableSymbol extends VariableSymbol 
{
    int position;
    
    public ClassVariableSymbol(ClassVariableDeclarationContext ctx) 
    {
        name = ctx.identifier().getText();
        type = Type.createType(ctx.type());
    }
    
    public ClassVariableSymbol(ClassVariableDeclarationContext ctx, int position) 
    {
        name = ctx.identifier().getText();
        type = Type.createType(ctx.type());
        this.position = position;
    }

    public Type getType() 
    {
        return type;
    }

    public void emitLoad(CodeGeneratorVisitor codeGenerator) 
    {
        codeGenerator.emitProgramString("push word [$fp+4]");
        codeGenerator.emitProgramString("push word " + position);
        codeGenerator.emitProgramString("add word");

        if(type.getArrayLength() == 0)
            codeGenerator.emitProgramString("push " + type.getSizeStr());
    }

    public void emitStore(CodeGeneratorVisitor codeGenerator) 
    {
        codeGenerator.emitProgramString("push word [$fp+4]");
        codeGenerator.emitProgramString("push word " + position);
        codeGenerator.emitProgramString("add word");
        codeGenerator.emitProgramString("pop " + type.getSizeStr()); 
    }

    public void emitArrayLoad(CodeGeneratorVisitor codeGenerator) 
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
            
            codeGenerator.emitProgramString("push word [$fp+4]"); // Current object ("this")
            codeGenerator.emitProgramString("push word " + position);
            codeGenerator.emitProgramString("add word"); // Array start address now on top

            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("push " + type.getElementSizeStr()); // Data element now on top
        }
    }

    public void emitArrayStore(CodeGeneratorVisitor codeGenerator)  
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
            
            codeGenerator.emitProgramString("push word [$fp+4]"); // Current object ("this")
            codeGenerator.emitProgramString("push word " + position);
            codeGenerator.emitProgramString("add word"); // Array start address now on top

            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("pop " + type.getElementSizeStr()); // Pop to element
        }
    }
}