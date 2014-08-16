package se.neava.compiler.symbol;

import se.neava.compiler.CodeGenerator;
import se.neava.compiler.GravelParser;
import se.neava.compiler.GravelParser.ClassVariableDeclarationContext;
import se.neava.compiler.type.Type;

public class ClassVariableSymbol extends VariableSymbol 
{
    private String label;
    int position;

    public void setLabel(String label)
    {
        this.label = label; 
    }
    
    public String getLabel(String label)
    {
        return label;
    }
    
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

    public void emitLoad(CodeGenerator codeGenerator) 
    {
        // TODO Auto-generated method stub
        codeGenerator.emitProgramString("push word [$fp+4]");
        codeGenerator.emitProgramString("push word " + (4 + position));
        codeGenerator.emitProgramString("add word");
        codeGenerator.emitProgramString("push word ");
    }

    public void emitStore(CodeGenerator codeGenerator) 
    {
        codeGenerator.emitProgramString("push word [$fp+4]");
        codeGenerator.emitProgramString("push word " + (4 + position));
        codeGenerator.emitProgramString("add word");
        codeGenerator.emitProgramString("pop word ");    
    }

    public void emitArrayLoad(CodeGenerator codeGenerator) 
    {
        if(type.getArrayLength() == 0)
        {
            // First, we assume that the index of the array is on top of the stack
            codeGenerator.emitProgramString("push word " + type.getSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push word [$fp+4]"); // Current object ("this")
            codeGenerator.emitProgramString("push word " + position);
            codeGenerator.emitProgramString("add word"); // Address of array variable now on top
            codeGenerator.emitProgramString("push word"); // Array address now on top
            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("push " + type.getSizeStr()); // Data element now on top
        }
        else
        {
            // First, we assume that the index of the array is on top of the stack
            codeGenerator.emitProgramString("push word " + type.getSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push word [$fp+4]"); // Current object ("this")
            codeGenerator.emitProgramString("push word " + position);
            codeGenerator.emitProgramString("add word"); // Array start address now on top

            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("push " + type.getSizeStr()); // Data element now on top
        }
    }

    public void emitArrayStore(CodeGenerator codeGenerator)  
    {
        if(type.getArrayLength() == 0)
        {
            // First, we assume that the index of the array is on top of the stack, and the data to be stored
            // is below that
            codeGenerator.emitProgramString("push word " + type.getSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push word [$fp+4]"); // Current object ("this")
            codeGenerator.emitProgramString("push word " + position);
            codeGenerator.emitProgramString("add word"); // Address of array variable now on top
            codeGenerator.emitProgramString("push word"); // Array address now on top
            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("pop " + type.getSizeStr()); // Pop to element
        }
        else
        {
            // First, we assume that the index of the array is on top of the stack, and the data to be stored
            // is below that
            codeGenerator.emitProgramString("push word " + type.getSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push word [$fp + 4]"); // Current object ("this")
            codeGenerator.emitProgramString("push word " + position);
            codeGenerator.emitProgramString("add word"); // Array start address now on top

            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("pop " + type.getSizeStr()); // Pop to element
        }
    }
}