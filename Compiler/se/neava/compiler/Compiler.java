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
        return visitor.error() || parsingError;
    }
    
    public boolean parsingError()
    {
        return parsingError;
    }
        
    public String compile(String code) throws CompileException
    {
        GravelLexer lexer = new GravelLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        visitor = new CodeGeneratorVisitor();
        GravelParser parser = new GravelParser(tokens);
        GravelErrorListener errorListener = new GravelErrorListener();
        
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        
        visitor.visit(parser.program());
        visitor.addEntryPoint();
        errors = errorListener.getErrors();
        
        if(!errors.isEmpty())
            throw new CompileException(errors.get(0));
        else if(visitor.error())
            throw new CompileException(visitor.getErrors().get(0));
        return visitor.getCode();
    }
}
