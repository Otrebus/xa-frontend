package se.neava.compiler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import se.neava.compiler.GravelParser.DeclarationContext;

public class CompileTester {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        GravelLexer lexer = new GravelLexer(new ANTLRInputStream("int x"));
        
        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);
     
        // Pass the tokens to the parser
        GravelParser parser = new GravelParser(tokens);
     
        // Specify our entry point
        DeclarationContext gravelContext = parser.declaration();
     
        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        GravelListener listener = new CodeGeneratorListener();
        walker.walk(listener, gravelContext);
    }

}
