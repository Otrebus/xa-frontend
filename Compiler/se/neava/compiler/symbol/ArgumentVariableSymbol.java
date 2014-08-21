package se.neava.compiler.symbol;

import se.neava.compiler.CodeGenerator;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;
import se.neava.compiler.type.Type;

public class ArgumentVariableSymbol extends VariableSymbol 
{
    int position;
    
    public ArgumentVariableSymbol(String name, Type type)
    {
        this.name = name;
        this.type = type;
    }
    
    public ArgumentVariableSymbol(String name, Type type, int position)
    {
        this.name = name;
        this.type = type;
        this.position = position;
    }
    
    public void emitArrayLoad(CodeGenerator codeGenerator) 
    {
        if(type.getArrayLength() == 0)
        {
            // First, we assume that the index of the array is on top of the stack
            codeGenerator.emitProgramString("push word " + type.getElementSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push word [$fp+" + (4 + position) + "]"); // Array address now on top
            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("push " + type.getElementSizeStr()); // Data element now on top
        }
        else
        {
            // First, we assume that the index of the array is on top of the stack
            codeGenerator.emitProgramString("push word " + type.getElementSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push word [$fp+" + (4 + position) + "]"); // Array address now on top
            codeGenerator.emitProgramString("add word"); // Add with offset

            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("push " + type.getElementSizeStr()); // Data element now on top
        }
    }

    public void emitArrayStore(CodeGenerator codeGenerator) 
    {
        if(type.getArrayLength() == 0)
        {
            // First, we assume that the index of the array is on top of the stack
            codeGenerator.emitProgramString("push word " + type.getElementSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push word [$fp-" + position + "]"); // Array address now on top
            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("pop " + type.getElementSizeStr()); // Pop to element
        }
        else
        {
            // First, we assume that the index of the array is on top of the stack
            codeGenerator.emitProgramString("push word " + type.getElementSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push word [$fp-" + position + "]"); // Array address now on top
            codeGenerator.emitProgramString("add word"); // Add with offset

            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("pop " + type.getElementSizeStr()); // Pop to element
        }
    }

    public void emitLoad(CodeGenerator codeGenerator) 
    {
        codeGenerator.emitProgramString("push " + type.getSizeStr() + " [$fp+" + (4 + position) + "]");
    }

    public void emitStore(CodeGenerator codeGenerator) 
    {
        codeGenerator.emitProgramString("pop " + type.getSizeStr() + " [$fp+" + (4 + position) + "]");
    }
}
