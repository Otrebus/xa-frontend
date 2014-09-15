package se.neava.compiler;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import se.neava.compiler.GravelParser.FunctionCallContext;
import se.neava.compiler.GravelParser.MethodBodyContext;
import se.neava.compiler.GravelParser.MethodDefinitionContext;
import se.neava.compiler.GravelParser.MethodVariableDefinitionContext;
import se.neava.compiler.GravelParser.StatementContext;
import se.neava.compiler.scope.ClassScope;
import se.neava.compiler.scope.GlobalScope;
import se.neava.compiler.scope.MethodScope;
import se.neava.compiler.scope.Scope;
import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;
import se.neava.compiler.type.BoolType;
import se.neava.compiler.type.CharType;
import se.neava.compiler.type.FunctionPointerType;
import se.neava.compiler.type.IntType;
import se.neava.compiler.type.LongType;
import se.neava.compiler.type.NoType;
import se.neava.compiler.type.Type;
import se.neava.compiler.type.VoidType;

public class CodeGeneratorVisitor extends GravelBaseVisitor<Type>
{
    Scope currentScope;
    boolean hadError = false;
    List<String> error = new LinkedList<String>();
    CodeGenerator codeGenerator;
    boolean mute;
    
    public CodeGenerator getCodeGenerator()
    {
        return codeGenerator;
    }
    
    CodeGeneratorVisitor()
    {
        codeGenerator = new CodeGenerator();
    }
    
    String getCode()
    {
        return codeGenerator.getCode();
    }
    
    public NoType reportError(ParserRuleContext ctx, String str)
    {
        hadError = true;
        error.add(new String(ctx != null ? ("Line " + ctx.start.getLine() + ": " + str) : "" + str));
        return new NoType();
    }
    
    public List<String> getErrors()
    {
        return error;
    }
    
    boolean error()
    {
        return hadError;
    }
    
    public boolean addEntryPoint()
    {
        codeGenerator.emitDataDirective(".entry");
        codeGenerator.emitDataLabel(codeGenerator.makeLabel("dummyEntryObject"));
        codeGenerator.emitDataln("dword 0");
        
        codeGenerator.emitProgramDirective(".entry");
        ClassInstanceSymbol mainSym = currentScope.getClassInstance("main");
        if(mainSym == null)
        {
            mainSym = currentScope.getClassInstance("Main");
            if(mainSym == null)
            {
                reportError(null, "No main object!");
                return false;
            }
        }
        ClassScope mainScope = mainSym.getClassScope();
        if(!mainScope.getName().equals("Main"))
        {
            reportError(null, "Main object not of type Main!");
            return false;
        }
        
        MethodSymbol methodSymbol = mainScope.getMethod("main");
        if(methodSymbol == null)
        {
            reportError(null, "No main method!");
            return false;
        }
        
        if(!mainScope.isObject())
            codeGenerator.emitProgramString("push " + mainSym.getName());
        codeGenerator.emitProgramString("push " + mainSym.getName() + "_main");
        codeGenerator.emitProgramString("push " + mainSym.getName());
        codeGenerator.emitProgramString("sync");
        codeGenerator.emitProgramString("ret 0");
        return true;
    }

    public Type visitProgram(GravelParser.ProgramContext ctx) 
    {
        try {
            currentScope = new GlobalScope(codeGenerator, ctx);
        } catch (CompileException e) {
            return reportError(null, e.getMessage());
        }
        return visitChildren(ctx); 
    }    
    
    public Type visitExternDeclaration(GravelParser.ExternDeclarationContext ctx) 
    {
        String name = ctx.identifier().getText();
        String lbl = codeGenerator.makeLabel(name);
        currentScope.getMethod(name).setLabel(lbl);
        
        codeGenerator.emitExternLabel(lbl);
        codeGenerator.emitExternln("\"" + name + "\"");
        return new NoType(); 
    }
    
    public Type visitClassInstanceDeclaration(@NotNull GravelParser.ClassInstanceDeclarationContext ctx) 
    { 
        String className = ctx.identifier(0).getText();
        String identifierName = ctx.identifier(1).getText();
        
        String lbl = codeGenerator.makeLabel(identifierName);
        codeGenerator.emitDataLabel(lbl);
        
        ClassScope classScope = currentScope.getClassScope(className);
        if(classScope == null)
            return reportError(ctx, "Class " + className + " not found");
        if(classScope.isObject())
            return reportError(ctx, "Object " + identifierName + " is already defined implicitly");
        int size = classScope.getSize();
        codeGenerator.emitDataln("byte[" + size + "]");
        ClassInstanceSymbol sym = new ClassInstanceSymbol(classScope, identifierName);
        sym.setLabel(lbl);
        ((GlobalScope)currentScope).addClassInstance(sym);
        
        return visitChildren(ctx); 
    }

    public Type visitClassDefinition(GravelParser.ClassDefinitionContext ctx) 
    {
        String name = ctx.identifier().getText();
        currentScope = currentScope.getClassScope(name);
        Type t = visitChildren(ctx);
        currentScope = currentScope.getParent();
        return t;
    }
    
    public Type visitMethodDefinition(MethodDefinitionContext ctx) 
    { 
        try 
        {
            currentScope = new MethodScope(((ClassScope) currentScope), ctx);
        } 
        catch (CompileException e) 
        {
            return reportError(ctx, e.getMessage());
        }
        MethodSymbol s = currentScope.getMethod(ctx.identifier(0).getText());

        codeGenerator.emitProgramLabel(s.getLabel());
        Type t = visitChildren(ctx);
        currentScope = currentScope.getParent();
        return t;
    }
    
    public Type visitMethodBody(MethodBodyContext ctx)
    {
        for(MethodVariableDefinitionContext i : ctx.methodVariableDefinition())
            visit(i);
        if(((MethodScope) currentScope).getLocalVariableSize() > 0)
            codeGenerator.emitProgramString("push " + ((MethodScope) currentScope).getLocalVariableSize());
        for(StatementContext i : ctx.statement())
            visit(i);
        visit(ctx.returnStatement());
        return new NoType();
    }
    
    public Type visitMethodVariableDefinition(MethodVariableDefinitionContext ctx) 
    { 
        if(!((MethodScope) currentScope).addVariable(ctx.identifier().getText(), Type.createType(ctx.type())))
            reportError(ctx, "May not overload argument " + ctx.identifier().getText());
        return visitChildren(ctx); 
    }
    
    public Type visitIdentifierExp(@NotNull GravelParser.IdentifierExpContext ctx) 
    {
        VariableSymbol s = currentScope.getVariable(ctx.getText());
        if(s == null)
        {
            reportError(ctx, "Undeclared identifier " + ctx.getText());
            return new NoType();
        }
        s.emitLoad(codeGenerator);
        return s.getType();
    }
       
    public Type visitReturnStatement(@NotNull GravelParser.ReturnStatementContext ctx) 
    {
        Type t = ctx.expression() != null ? visit(ctx.expression()) : new VoidType();
        if(!t.equals(((MethodScope) currentScope).getMethod().getReturnType()))
            return reportError(ctx, "Return type mismatch.");
        if(t.isPointer())
            return reportError(ctx, "May not return pointers");
        MethodScope methodScope = (MethodScope) currentScope;
        MethodSymbol sym = methodScope.getMethod(methodScope.getName());
        int retSize = sym.getReturnType().getMemorySize();
        int argSize = sym.getTotalArgumentSize() + (((ClassScope) (currentScope.getParent())).isObject() ? 0 : 2);
        int retImm = Math.max(0, argSize - retSize);
        
        if(!t.equals(new VoidType()))
            codeGenerator.emitProgramString("pop " + t.getSizeStr() + " [$fp+" + (4 + retImm) + "]");
        codeGenerator.emitProgramString("ret " + retImm);
        return t;
    }
    
    public Type visitNumExp(GravelParser.NumExpContext ctx) 
    { 
        String s = ctx.suffix().getText();
        Type type = Type.getTypeFromSuffix(s);
        if(type == null)
            reportError(ctx, "Unknown number suffix");
        codeGenerator.emitProgramString("push " + type.getSizeStr() + " " + Integer.parseInt(ctx.NUM().getText()));
        return type; 
    }
    
    public Type visitCastExp(GravelParser.CastExpContext ctx)
    {
        Type castType = Type.createType(ctx.baseType());
        Type expType = visit(ctx.expression());
        if(!expType.castTo(codeGenerator, castType))
            return reportError(ctx, "Illegal cast");
        return castType;
    }

    public Type visitIndirectionExp(GravelParser.IndirectionExpContext ctx) 
    {
        String objName = ctx.identifier(0).getText();
        String methodName = ctx.identifier(1).getText();
        
        ClassInstanceSymbol classInstanceSymbol = currentScope.getClassInstance(objName);
        if(classInstanceSymbol == null)
            return reportError(ctx, "Undeclared identifier " + objName);

        ClassScope classScope = classInstanceSymbol.getClassScope();
        MethodSymbol methodSymbol = classScope.getMethod(methodName);
        if(methodSymbol == null)
            return reportError(ctx, "Undeclared method " + methodName);
        
        codeGenerator.emitProgramString("push " + methodSymbol.getLabel());
        codeGenerator.emitProgramString("push " + classInstanceSymbol.getLabel());
        return new FunctionPointerType(methodSymbol);
    }

    public Type visitTrueExp(GravelParser.TrueExpContext ctx) 
    { 
        codeGenerator.emitProgramString("push byte 1");
        return new BoolType();
    }

    public Type visitAsyncStatement(GravelParser.AsyncStatementContext ctx) 
    { 
        ClassInstanceSymbol classInstanceSymbol = null;
        MethodSymbol methodSymbol;
        FunctionCallContext c = ctx.functionCall();
        
        if(c.identifier().size() > 1)
        {
            String objName = c.identifier(0).getText();
            String methodName = c.identifier(1).getText();
            
            classInstanceSymbol = currentScope.getClassInstance(objName);
            if(classInstanceSymbol == null)
                return reportError(ctx, "Undeclared identifier " + objName);

            ClassScope classScope = classInstanceSymbol.getClassScope();
            methodSymbol = classScope.getMethod(methodName);
            if(methodSymbol == null)
                return reportError(ctx, "Undeclared method " + methodName);
        }
        else
        {
            String methodName = c.identifier(0).getText();
            
            methodSymbol = currentScope.getMethod(methodName);
            if(methodSymbol == null)
            {
                methodSymbol = currentScope.getExternMethod(methodName);
                if(methodSymbol == null)
                {
                    methodSymbol = currentScope.getExternMethod(methodName);
                    if(methodSymbol == null)
                        return reportError(ctx, "Undeclared method " + methodName);
                }
            }
        }

        List<Type> arguments = methodSymbol.getArguments();
        if(c.expression().size() != arguments.size())
            return reportError(ctx, "Method arity mismatch");        
        
        for(int i = arguments.size() - 1; i >= 0; i--)
        {
            Type ts = arguments.get(i);
            Type te = visit(c.expression(i));
            if(!ts.equals(te))
                return reportError(ctx, "Argument mismatch");
        }
        
        // Last push is "this", but this is repeated due to the requirements of async
        // TODO: make this stuff less retarded?
        if(classInstanceSymbol == null)
        {
            if(!((ClassScope) currentScope.getParent()).isObject())
                codeGenerator.emitProgramString("push word [$fp+4]");
            else
                codeGenerator.emitProgramString("push " + ((ClassScope) currentScope.getParent()).getLabel());
        }
        else
        {
            if(!classInstanceSymbol.getClassScope().isObject())
                codeGenerator.emitProgramString("push word [$fp+4]");
            else
                codeGenerator.emitProgramString("push " + (classInstanceSymbol.getClassScope().getLabel()));
        }
        
        codeGenerator.emitProgramString("push " + methodSymbol.getLabel());
        
        if(classInstanceSymbol == null)
        {
            if(!((ClassScope) currentScope.getParent()).isObject())
                codeGenerator.emitProgramString("push word [$fp+4]");
            else
                codeGenerator.emitProgramString("push " + ((ClassScope) currentScope.getParent()).getLabel());
        }
        else
        {
            if(!classInstanceSymbol.getClassScope().isObject())
                codeGenerator.emitProgramString("push word [$fp+4]");
            else
                codeGenerator.emitProgramString("push " + (classInstanceSymbol.getClassScope().getLabel()));
        }
        
        if(ctx.after() != null)
        {
            Type a;
            String unit;
            if(ctx.before() != null)
            {
                a = visit(ctx.expression(1));
                
                if(!(a instanceof LongType))
                    return reportError(ctx, "Time must be of type long");
                unit = ctx.time(1).getText();
                if(unit.equals("msec"))
                {
                    codeGenerator.emitProgramString("push dword 1000");
                    codeGenerator.emitProgramString("mul dword");
                }
                else if(unit.equals("sec"))
                {
                    codeGenerator.emitProgramString("push dword 1000000");
                    codeGenerator.emitProgramString("mul dword");
                }
            }
            else
                codeGenerator.emitProgramString("push dword 0");
            a = visit(ctx.expression(0));
            if(!(a instanceof LongType))
                return reportError(ctx, "Time must be of type long");
            unit = ctx.time(0).getText();
            if(unit.equals("msec"))
            {
                codeGenerator.emitProgramString("push dword 1000");
                codeGenerator.emitProgramString("mul dword");
            }
            else if(unit.equals("sec"))
            {
                codeGenerator.emitProgramString("push dword 1000000");
                codeGenerator.emitProgramString("mul dword");
            }
        }
        else
        {
            Type a = visit(ctx.expression(0));
            
            if(!(a instanceof LongType))
                return reportError(ctx, "Time must be of type long");
            String unit = ctx.time(0).getText();
            if(unit.equals("msec"))
            {
                codeGenerator.emitProgramString("push dword 1000");
                codeGenerator.emitProgramString("mul dword");
            }
            else if(unit.equals("sec"))
            {
                codeGenerator.emitProgramString("push dword 1000000");
                codeGenerator.emitProgramString("mul dword");
            }
            codeGenerator.emitProgramString("push dword 0");
        }
        
        codeGenerator.emitProgramString("push byte " + (methodSymbol.getTotalArgumentSize() + (((ClassScope) (currentScope.getParent())).isObject() ? 0 : 2)));
        codeGenerator.emitProgramString("async");
        
        return methodSymbol.getReturnType();
    }

    public Type visitFalseExp(GravelParser.FalseExpContext ctx) 
    { 
        codeGenerator.emitProgramString("push byte 0");
        return new BoolType();
    }

    public Type visitFunctionCallExp(GravelParser.FunctionCallExpContext ctx) 
    { 
        return visit(ctx.functionCall());
    }

    public Type visitAssignment(GravelParser.AssignmentContext ctx) 
    { 
        // TODO: this is horrible, put in visitLvalue instead
        String varName = ctx.lvalue().identifier().getText();
        VariableSymbol var = currentScope.getVariable(varName);
        if(var == null)
            return reportError(ctx, "Unknown identifier " + varName);
        boolean arrayAssignment = (ctx.lvalue().expression() != null);
        Type b = var.getType().clone();
        
        if(arrayAssignment)
        {
            if(!b.isArray())
                return reportError(ctx, "Array assignment on a non-array");
            b.isArray = false;
            Type a = visit(ctx.expression());
            Type indexType = visit(ctx.lvalue().expression());
            if(!(indexType instanceof IntType))
                return reportError(ctx, "Index must be of type int");
            if(!b.isAssignableFrom(a))
                return reportError(ctx, "Type mismatch");
            var.emitArrayStore(codeGenerator);
        }
        else
        {
            Type a = visit(ctx.expression());
            if(!b.isAssignableFrom(a))
                return reportError(ctx, "Type mismatch");
            var.emitStore(codeGenerator);

        }
        return b;
    }

    public Type visitWhileStatement(GravelParser.WhileStatementContext ctx) 
    {
        String loopLabel = codeGenerator.makeLabel();
        codeGenerator.emitProgramLabel(loopLabel);
        Type a = visit(ctx.expression());
        if(!(a instanceof BoolType))
            return reportError(ctx, "Expression in if statement must be of type bool");
        String endLabel = codeGenerator.makeLabel();
        codeGenerator.emitProgramString("jez " + endLabel);
        visit(ctx.statement());
        codeGenerator.emitProgramString("jmp " + loopLabel);
        codeGenerator.emitProgramLabel(endLabel);
        return new NoType();
    }

    public Type visitArrayLookupExp(@NotNull GravelParser.ArrayLookupExpContext ctx)
    {
        boolean wasMute = codeGenerator.mute;
        codeGenerator.mute(); // TODO: replace this shit with a straight TypeVisitor
        Type a = visit(ctx.expression(0));
        if(!wasMute)
            codeGenerator.unmute();
           
        Type t = a.pushFrom(this, ctx).clone();
        t.isArray = false;
        return t;
    }

    public Type visitFunctionCallStatement(GravelParser.FunctionCallStatementContext ctx)
    {
        FunctionCallContext c = ctx.functionCall();
        Type t = visit(c);
        if(t.getMemorySize() > 0)
            codeGenerator.emitProgramString("pop " + t.getMemorySize());
        return t;
    }
    
    public Type methodCall(GravelParser.FunctionCallContext ctx, ClassInstanceSymbol classInstanceSymbol, MethodSymbol methodSymbol)
    {
        List<Type> arguments = methodSymbol.getArguments();
        if(ctx.expression().size() != arguments.size())
            return reportError(ctx, "Method arity mismatch");
        
        int retSize = methodSymbol.getReturnType().getMemorySize();
        int argSize = methodSymbol.getTotalArgumentSize() + (((ClassScope) (currentScope.getParent())).isObject() ? 0 : 2);
        if(retSize > argSize)
            codeGenerator.emitProgramString("push " + (retSize - argSize));
        
        for(int i = arguments.size() - 1; i >= 0; i--)
        {
            Type ts = arguments.get(i);
            Type te = visit(ctx.expression(i));
            if(!ts.equals(te))
                return reportError(ctx, "Argument mismatch");
            if(ts.isArray())
                return reportError(ctx, "May not call foreign method with array argument");
        }
        
        if(!classInstanceSymbol.getClassScope().isObject())
            codeGenerator.emitProgramString("push " + classInstanceSymbol.getLabel());
        codeGenerator.emitProgramString("push " + methodSymbol.getLabel());
        codeGenerator.emitProgramString("push " + classInstanceSymbol.getLabel());
        codeGenerator.emitProgramString("sync");
        return methodSymbol.getReturnType();       
    }
    
    public Type functionCall(GravelParser.FunctionCallContext ctx, MethodSymbol methodSymbol)
    {
        List<Type> arguments = methodSymbol.getArguments();
        if(ctx.expression().size() != arguments.size())
            return reportError(ctx, "Method arity mismatch");
        
        int retSize = methodSymbol.getReturnType().getMemorySize();
        int argSize = methodSymbol.getTotalArgumentSize() + (((ClassScope) (currentScope.getParent())).isObject() ? 0 : 2);
        if(retSize > argSize)
            codeGenerator.emitProgramString("push " + (retSize - argSize));
        
        for(int i = arguments.size() - 1; i >= 0; i--)
        {
            Type ts = arguments.get(i);
            Type te = visit(ctx.expression(i));
            if(!ts.equals(te))
                return reportError(ctx, "Argument mismatch");
        }

        if(!((ClassScope) currentScope.getParent()).isObject())
            codeGenerator.emitProgramString("push word [$fp+4]");
        codeGenerator.emitProgramString("call " + methodSymbol.getLabel());
        return methodSymbol.getReturnType();
    }
    
    public Type visitStringExp(GravelParser.StringExpContext ctx)
    {
        String str = ctx.string().getText().replaceAll("\"", "");
        String lbl = codeGenerator.addStringLiteral(str);
        codeGenerator.emitProgramString("push " + lbl);
        return new CharType(true);
    }

    public Type externFunctionCall(GravelParser.FunctionCallContext ctx, MethodSymbol methodSymbol)
    {
        List<Type> arguments = methodSymbol.getArguments();
        if(ctx.expression().size() != arguments.size())
            return reportError(ctx, "Method arity mismatch");
        
        int retSize = methodSymbol.getReturnType().getMemorySize();
        int argSize = methodSymbol.getTotalArgumentSize();
        if(retSize > argSize)
            codeGenerator.emitProgramString("push " + (retSize - argSize));
        
        for(int i = arguments.size() - 1; i >= 0; i--)
        {
            Type ts = arguments.get(i);
            Type te = visit(ctx.expression(i));
            if(!ts.equals(te))
                return reportError(ctx, "Argument mismatch");
        }

        codeGenerator.emitProgramString("call " + methodSymbol.getLabel());
        return methodSymbol.getReturnType();
    }
    
    public Type visitFunctionCall(GravelParser.FunctionCallContext ctx)
    {
        if(ctx.identifier().size() > 1)
        {
            String objName = ctx.identifier(0).getText();
            String methodName = ctx.identifier(1).getText();
            
            ClassInstanceSymbol classInstanceSymbol = currentScope.getClassInstance(objName);
            if(classInstanceSymbol == null)
                return reportError(ctx, "Undeclared identifier " + objName);
            
            ClassScope classScope = classInstanceSymbol.getClassScope();
            MethodSymbol methodSymbol = classScope.getMethod(methodName);

            if(methodSymbol == null)
                return reportError(ctx, "Undeclared method " + methodName);
            return methodCall(ctx, classInstanceSymbol, methodSymbol);
        }
        else
        {
            String methodName = ctx.identifier(0).getText();
            MethodSymbol methodSymbol = currentScope.getMethod(methodName);
            
            if(methodSymbol == null)
            {
                methodSymbol = currentScope.getExternMethod(methodName);
                if(methodSymbol == null)
                    return reportError(ctx, "Undeclared method " + methodName);
                return externFunctionCall(ctx, methodSymbol);
            }
            return functionCall(ctx, methodSymbol);
        }

    }

    public Type visitGtExp(GravelParser.GtExpContext ctx) 
    {
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        codeGenerator.emitProgramString("sub " + a.getSizeStr());
        codeGenerator.emitProgramString("sgz " + a.getSizeStr());
        return new BoolType();
    }
    
    public Type visitParExp(GravelParser.ParExpContext ctx) 
    { 
        return visit(ctx.expression());
    }
    
    public Type visitEqExp(GravelParser.EqExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        codeGenerator.emitProgramString("sub " + a.getSizeStr());
        codeGenerator.emitProgramString("sez " + a.getSizeStr());
        return new BoolType();
    }
    
    public Type visitLtExp(GravelParser.LtExpContext ctx)
    { 
        Type a = visit(ctx.expression(0));
        Type b = visit(ctx.expression(1));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        codeGenerator.emitProgramString("sub " + a.getSizeStr());
        codeGenerator.emitProgramString("sgz " + a.getSizeStr());
        return new BoolType();
    }
    
    public Type visitLteExp(GravelParser.LteExpContext ctx) 
    { 
        Type a = visit(ctx.expression(0));
        Type b = visit(ctx.expression(1));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        codeGenerator.emitProgramString("sub " + a.getSizeStr());
        codeGenerator.emitProgramString("sgez " + a.getSizeStr());
        return new BoolType(); 
    }

    public Type visitLogAndExp(GravelParser.LogAndExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b) || !(a instanceof BoolType))
            return reportError(ctx, "Both arguments to && must be of type bool");
        codeGenerator.emitProgramString("and byte");
        return new BoolType();
    }

    public Type visitGteExp(GravelParser.GteExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        codeGenerator.emitProgramString("sub " + a.getSizeStr());
        codeGenerator.emitProgramString("sgez " + a.getSizeStr());
        return new BoolType(); 
    }
    
    public Type visitLogOrExp(GravelParser.LogOrExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b) || !(a instanceof BoolType))
            return reportError(ctx, "Both arguments to || must be of type bool");
        codeGenerator.emitProgramString("or byte");
        return new BoolType();
    }
    
    @Override 
    public Type visitBitOrExp(GravelParser.BitOrExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        codeGenerator.emitProgramString("or " + a.getSizeStr());
        return a;
    }
    
    @Override 
    public Type visitBitAndExp(GravelParser.BitAndExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        codeGenerator.emitProgramString("and " + a.getSizeStr());
        return a;
    }
    
    @Override 
    public Type visitXorExp(GravelParser.XorExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        codeGenerator.emitProgramString("xor " + a.getSizeStr());
        return a;
    }
    
    @Override
    public Type visitNeqExp(GravelParser.NeqExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        codeGenerator.emitProgramString("sub " + a.getSizeStr());
        codeGenerator.emitProgramString("snez " + a.getSizeStr());
        return new BoolType();
    }
    
    public Type visitDivExp(GravelParser.DivExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in div");
        codeGenerator.emitProgramString("div " + a.getSizeStr());
        return a;
    }

    public Type visitAddExp(GravelParser.AddExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in add");
        codeGenerator.emitProgramString("add " + a.getSizeStr());
        return a;
    }
    
    public Type visitMulExp(GravelParser.MulExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in mul");
        codeGenerator.emitProgramString("mul " + a.getSizeStr());
        return a;
    }
    
    public Type visitModExp(GravelParser.ModExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in mul");
        codeGenerator.emitProgramString("mod " + a.getSizeStr());
        return a;
    }
    
    public Type visitSubExp(GravelParser.SubExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in sub");
        codeGenerator.emitProgramString("sub " + a.getSizeStr());
        return a;
    }
    @Override
    public Type visitNotExp(GravelParser.NotExpContext ctx)
    {
        Type a = visit(ctx.expression());
        if(!(a instanceof BoolType))
            return reportError(ctx, "Expression in negation must be of type bool");
        codeGenerator.emitProgramString("sez byte");
        return a;
    }
    
    public Type visitIfStatement(GravelParser.IfStatementContext ctx) 
    { 
        Type a = visit(ctx.expression());
        if(!(a instanceof BoolType))
            return reportError(ctx, "Expression in if statement must be of type bool");
        String doneLabel = codeGenerator.makeLabel();
        if(ctx.elseClause() == null) 
        {
            codeGenerator.emitProgramString("jez " + doneLabel);
            visit(ctx.statement());
        }
        else
        {
            String elseLabel = codeGenerator.makeLabel();
            codeGenerator.emitProgramString("jez " + elseLabel);
            visit(ctx.statement());
            codeGenerator.emitProgramString("jmp " + doneLabel);
            codeGenerator.emitProgramLabel(elseLabel);
            visit(ctx.elseClause().statement());
        }
        codeGenerator.emitProgramLabel(doneLabel);
        return new NoType(); 
    }
}
