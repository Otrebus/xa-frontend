package se.neava.compiler;

import java.util.LinkedList;
import java.util.List;

import se.neava.compiler.GravelParser.*;
import se.neava.compiler.scope.ClassScope;
import se.neava.compiler.scope.GlobalScope;
import se.neava.compiler.scope.Scope;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.type.Type;

// TODO: this doesn't have to be a visitor
public class GlobalScopeVisitor extends GravelBaseVisitor<Scope> 
{
    /*
    GlobalScope globalScope;
    
    public GlobalScope visitProgram(ProgramContext ctx) 
    {
        globalScope = new GlobalScope(ctx);
        return globalScope;
    }*/
    }
