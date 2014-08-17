package se.neava.compiler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class Compiler 
{
    boolean hadError = false;
    CodeGeneratorVisitor visitor;
    
    public Compiler()
    {
    }
    
    public boolean error()
    {
        return visitor.error();
    }
    
    public String dumpErrors()
    {
        return visitor.dumpErrors();
    }
    
    public String compile(String code) 
    {
        GravelLexer lexer = new GravelLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GravelParser parser = new GravelParser(tokens);
     
        visitor = new CodeGeneratorVisitor();
        visitor.visit(parser.program());
        visitor.addEntryPoint();
        return visitor.getCode();
    }
}
