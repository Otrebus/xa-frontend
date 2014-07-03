package se.neava.Assembler;

import java.text.ParseException;

public class Parser {
    Lexer lexer;
    Token currentToken;
    
    interface InstructionParser
    {
        Instruction parseInstruction(Lexer lexer);
    }
    
    class PushParser implements InstructionParser
    {
        
        
        public Instruction parseInstruction(Lexer lexer) {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    
    int getSize(String str) throws ParseException
    {
        if(str.equals("byte"))
            return 1;
        else if(str.equals("word"))
            return 2;
        else if(str.equals("dword"))
            return 4;
        throw new ParseException("bad size", 0);
    }
    
    Token accept(Token.Type type)
    {
        if(currentToken.type.equals(type))
        {
            Token acceptedToken = currentToken;
            currentToken = lexer.nextToken();
            return acceptedToken;
        }
        return null;
    }
    
    Token expect(Token.Type type) throws ParseException
    {
        if(!currentToken.type.equals(type))
            throw new ParseException("Expected token " + type.name(), 0);
        Token acceptedToken = currentToken;
        currentToken = lexer.nextToken();
        return acceptedToken;
    }
    
    Token accept(Token tok)
    {
        if(currentToken.equals(tok))
        {
            Token acceptedToken = currentToken;
            currentToken = lexer.nextToken();
            return acceptedToken;
        }
        return null;
    }
    
    Token expect(Token tok) throws ParseException
    {
        if(!currentToken.equals(tok))
            throw new ParseException("Expected token " + tok.type.name() + ", specifically \"" + tok.str + "\"", 0);
        Token acceptedToken = currentToken;
        currentToken = lexer.nextToken();
        return acceptedToken;
    }
    
    public Parser(Lexer lexer)
    {
        this.lexer = lexer;
        currentToken = lexer.nextToken();
    }
    
    Statement section()
    {
        Token tok;
        tok = accept(new Token(Token.Type.IDENTIFIER, "data"));
        if(tok != null)
            return new Section("data");
        tok = accept(new Token(Token.Type.IDENTIFIER, "code"));
        if(tok != null)
            return new Section("code");
        tok = accept(new Token(Token.Type.IDENTIFIER, "extern"));
        if(tok != null)
            return new Section("extern");
        return null;
    }
    
    Statement data(String size) throws ParseException
    {
        Token tok;
        tok = accept(Token.Type.END);
        if(tok != null)
            return new Data(new byte[getSize(size)]);
        expect(Token.Type.NUMBER);
        // TODO: Actually fill in data
        expect(Token.Type.END);
        return new Data(new byte[getSize(size)]);
    }
    
    Statement label(String str) throws ParseException
    {
        expect(Token.Type.END);
        return new Label(str.substring(0, str.length() - 1));
    }
    
    Statement parse() throws ParseException
    {
        Token tok;
        tok = accept(Token.Type.SECTION);
        if(tok != null)
            return section();
        tok = accept(Token.Type.SIZE);
        if(tok != null)
            return data(tok.str);
        tok = accept(Token.Type.LABEL);
        if(tok != null)
            return label(tok.str);
        return null;
    }
}
