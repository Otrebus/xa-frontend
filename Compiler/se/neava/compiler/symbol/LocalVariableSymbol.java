package se.neava.compiler.symbol;

import se.neava.compiler.CodeGeneratorVisitor;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;
import se.neava.compiler.type.Type;

public class LocalVariableSymbol extends VariableSymbol 
{
    int position;
    
    public LocalVariableSymbol(String name, Type type, int position)
    {
        this.name = name;
        this.type = type;
        this.position = position;
    }
    
    public void emitArrayLoad(CodeGeneratorVisitor codeGenerator) 
    {
        if(type.getArrayLength() == 0)
        {
            // First, we assume that the index of the array is on top of the stack
            codeGenerator.emitProgramString("push word " + type.getElementSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push word [$fp-" + position + "]"); // Array address now on top
            codeGenerator.emitProgramString("add word"); // Add with offset
            codeGenerator.emitProgramString("push " + type.getElementSizeStr()); // Data element now on top
        }
        else
        {
            // First, we assume that the index of the array is on top of the stack
            codeGenerator.emitProgramString("push word " + type.getElementSize());
            codeGenerator.emitProgramString("mul word"); // Offset into the array now on top of the stack
            
            codeGenerator.emitProgramString("push $fp-" + position + ""); // Array address now on top
            codeGenerator.emitProgramString("add word"); // Add with offset

            codeGenerator.emitProgramString("push " + type.getElementSizeStr()); // Data element now on top
        }
    }

    public void emitArrayStore(CodeGeneratorVisitor codeGenerator) 
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
            
            codeGenerator.emitProgramString("push $fp-" + position + ""); // Array address now on top
            codeGenerator.emitProgramString("add word"); // Add with offset

            codeGenerator.emitProgramString("pop " + type.getElementSizeStr()); // Pop to element
        }
    }

    public void emitLoad(CodeGeneratorVisitor codeGenerator) 
    {
        if(type.getArrayLength() == 0)
            codeGenerator.emitProgramString("push " + type.getSizeStr() + " [$fp-" + position + "]");
        else
            codeGenerator.emitProgramString("push " + " $fp-" + position + "");
    }

    public void emitStore(CodeGeneratorVisitor codeGenerator) 
    {
        if(type.getArrayLength() == 0)
            codeGenerator.emitProgramString("pop " + type.getSizeStr() + " [$fp-" + position + "]");
        else
            codeGenerator.emitProgramString("pop " + " $fp-" + position + "");
    }
}
