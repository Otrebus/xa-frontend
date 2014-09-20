package se.neava.compiler;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class Compiler 
{
    private boolean parsingError = false;
    private CodeGeneratorVisitor visitor;
    private List<String> errors = new ArrayList<String>();
    
    public Compiler()
    {
    }
    
    public boolean error()
    {
        return visitor.hadError() || parsingError;
    }
    
    public boolean parsingError()
    {
        return parsingError;
    }
    
    private void checkGrammar(String code) throws CompileException
    {
        GravelLexer lexer = new GravelLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GravelBaseVisitor<Void> parseVisitor = new GravelBaseVisitor<Void>();
        visitor = new CodeGeneratorVisitor();
        GravelParser parser = new GravelParser(tokens);
        GravelErrorListener errorListener = new GravelErrorListener();
        
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        
        parseVisitor.visit(parser.program());
        errors = errorListener.getErrors();
        
        if(!errors.isEmpty())
            throw new CompileException(errors.get(0));
    }
        
    public String compile(String code) throws CompileException
    {
        checkGrammar(code);
        GravelLexer lexer = new GravelLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GravelParser parser = new GravelParser(tokens);
        
        GravelErrorListener errorListener = new GravelErrorListener();
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        
        visitor = new CodeGeneratorVisitor();
        visitor.visit(parser.program());
        if(visitor.hadError())
            throw new CompileException(visitor.getErrors().get(0));
        if(!visitor.addEntryPoint())
            throw new CompileException(visitor.getErrors().get(0));
        return visitor.getCode();
    }
}
