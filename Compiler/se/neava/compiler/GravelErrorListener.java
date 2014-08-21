package se.neava.compiler;

import java.util.ArrayList;

import org.antlr.v4.runtime.*;

public class GravelErrorListener extends BaseErrorListener 
{
    private ArrayList<String> errors = new ArrayList<String>();
    
    public ArrayList<String> getErrors()
    {
        return errors;
    }
    
    @Override public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                      int line, int charPositionInLine, String msg, RecognitionException e)
    {
        errors.add(line+":"+charPositionInLine+" "+msg);
    }
}