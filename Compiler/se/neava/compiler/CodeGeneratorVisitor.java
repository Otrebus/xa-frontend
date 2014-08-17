package se.neava.compiler;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import se.neava.compiler.GravelParser.FunctionCallContext;
import se.neava.compiler.scope.ClassScope;
import se.neava.compiler.scope.GlobalScope;
import se.neava.compiler.scope.MethodScope;
import se.neava.compiler.scope.Scope;
import se.neava.compiler.symbol.ClassInstanceSymbol;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;
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
        error.add(new String(ctx != null ? ("Line " + ctx.start.getLine() + ": ") : "" + str));
        return new NoType();
    }
    
    public String dumpErrors()
    {
        String ret = "";
        for(String s : error)
            ret += s + "\n";
        return ret;
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
            reportError(null, "No main object!");
            return false;
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
            reportError(null, "Main object not of type Main!");
            return false;
        }
        
        codeGenerator.emitProgramString("push Main_main");
        codeGenerator.emitProgramString("push main");
        codeGenerator.emitProgramString("sync");
        return true;
    }

    public Type visitProgram(GravelParser.ProgramContext ctx) 
    {
        currentScope = new GlobalScope(codeGenerator, ctx);
        return visitChildren(ctx); 
    }    
    
    public Type visitExternDeclaration(GravelParser.ExternDeclarationContext ctx) 
    {
        String name = ctx.identifier().getText();
        String lbl = codeGenerator.makeLabel(name);
        currentScope.getMethod(name).setLabel(lbl);
        
        codeGenerator.emitExternLabel(lbl);
        codeGenerator.emitExternln("\"" + name + "\"");
        return visitChildren(ctx); 
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
    
    public Type visitMethodDefinition(GravelParser.MethodDefinitionContext ctx) 
    { 
        currentScope = new MethodScope(currentScope, ctx);
        MethodSymbol s = currentScope.getMethod(ctx.identifier(0).getText());

        codeGenerator.emitProgramLabel(s.getLabel());
        Type t = visitChildren(ctx);
        currentScope = currentScope.getParent();
        return t;
    }
    
    public Type visitMethodVariableDefinition(@NotNull GravelParser.MethodVariableDefinitionContext ctx) 
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
        {
            reportError(ctx, "Return type mismatch.");
            return new NoType();
        }
        MethodScope methodScope = (MethodScope) currentScope;
        MethodSymbol sym = methodScope.getMethod(methodScope.getName());
        int retSize = sym.getReturnType().getMemorySize();
        int argSize = sym.getTotalArgumentSize();
        int retImm = Math.max(0, argSize - retSize);
        
        if(!t.equals(new VoidType()))
            codeGenerator.emitProgramString("pop " + t.getSizeStr() + " [$fp+" + (4 + retImm) + "]");
        codeGenerator.emitProgramString("ret " + retImm);
        return t;
    }
    
    @Override public Type visitNumExp(GravelParser.NumExpContext ctx) 
    { 
        Type type = Type.createType(ctx.baseType());
        codeGenerator.emitProgramString("push " + type.getSizeStr() + " " + Integer.parseInt(ctx.NUM().getText()));
        return type; 
    }

    @Override public Type visitLteExp(@NotNull GravelParser.LteExpContext ctx) { return visitChildren(ctx); }

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

    @Override public Type visitTrueExp(@NotNull GravelParser.TrueExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitType(@NotNull GravelParser.TypeContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitLtExp(@NotNull GravelParser.LtExpContext ctx) { return visitChildren(ctx); }

    public Type visitFunctionCall(@NotNull GravelParser.FunctionCallContext ctx)
    { 
        return visitChildren(ctx); 
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitMulExp(@NotNull GravelParser.MulExpContext ctx) { return visitChildren(ctx); }

    @Override public Type visitSubExp(@NotNull GravelParser.SubExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitBaseType(@NotNull GravelParser.BaseTypeContext ctx) { return visitChildren(ctx); }

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
            codeGenerator.emitProgramString("push word [$fp+4]");
        else
            codeGenerator.emitProgramString("push " + classInstanceSymbol.getLabel());
        
        codeGenerator.emitProgramString("push " + methodSymbol.getLabel());
        
        if(classInstanceSymbol == null)
            codeGenerator.emitProgramString("push word [$fp+4]");
        else
            codeGenerator.emitProgramString("push " + classInstanceSymbol.getLabel());
        
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
        
        codeGenerator.emitProgramString("push byte " + methodSymbol.getTotalArgumentSize());
        codeGenerator.emitProgramString("async");
        
        return methodSymbol.getReturnType();
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitFalseExp(@NotNull GravelParser.FalseExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */

    @Override public Type visitClassVariableDeclaration(@NotNull GravelParser.ClassVariableDeclarationContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitNumber(@NotNull GravelParser.NumberContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitDivExp(@NotNull GravelParser.DivExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitIfStatement(@NotNull GravelParser.IfStatementContext ctx) { return visitChildren(ctx); }

    public Type visitFunctionCallExp(GravelParser.FunctionCallExpContext ctx) 
    { 
        FunctionCallContext c = ctx.functionCall();
        if(c.identifier().size() > 1)
        {
            String objName = c.identifier(0).getText();
            String methodName = c.identifier(1).getText();
            
            ClassInstanceSymbol classInstanceSymbol = currentScope.getClassInstance(objName);
            if(classInstanceSymbol == null)
                return reportError(ctx, "Undeclared identifier " + objName);

            ClassScope classScope = classInstanceSymbol.getClassScope();
            MethodSymbol methodSymbol = classScope.getMethod(methodName);
            if(methodSymbol == null)
                return reportError(ctx, "Undeclared method " + methodName);
           
            List<Type> signature = methodSymbol.getArguments();
            if(c.expression().size() != signature.size())
                return reportError(ctx, "Method arity mismatch");
            for(int i = signature.size() - 1; i >= 0; i--)
            {
                Type ts = signature.get(i);
                Type te = visit(c.expression(i));
                if(!ts.equals(te))
                    return reportError(ctx, "Argument mismatch");
            }

            codeGenerator.emitProgramString("push " + classInstanceSymbol.getLabel());
            codeGenerator.emitProgramString("push " + methodSymbol.getLabel());
            codeGenerator.emitProgramString("push " + classInstanceSymbol.getLabel());
            codeGenerator.emitProgramString("sync");
            return signature.get(0);
        }
        else
        {
            String methodName = c.identifier(0).getText();
            
            MethodSymbol methodSymbol = currentScope.getMethod(methodName);
            if(methodSymbol == null)
            {
                methodSymbol = currentScope.getExternMethod(methodName);
                if(methodSymbol == null)
                    return reportError(ctx, "Undeclared method " + methodName);
            }
           
            List<Type> signature = methodSymbol.getArguments();
            if(c.expression().size() != signature.size() - 1)
                return reportError(ctx, "Method arity mismatch");
            for(int i = signature.size() - 1; i >= 0; i--)
            {
                Type ts = signature.get(i);
                Type te = visit(c.expression(i));
                if(!ts.equals(te))
                    return reportError(ctx, "Argument mismatch");
            }

            codeGenerator.emitProgramString("push word [$fp+4]");
            codeGenerator.emitProgramString("call");
            return signature.get(0);
        }
    }

    public Type visitAssignment(GravelParser.AssignmentContext ctx) 
    { 
        // TODO: this is horrible, put in visitLvalue instead
        String varName = ctx.lvalue().identifier().getText();
        VariableSymbol var = currentScope.getVariable(varName);
        boolean arrayAssignment = (ctx.lvalue().expression() != null);
        Type b = var.getType();
        if(arrayAssignment)
        {
            Type a = visit(ctx.expression());
            Type indexType = visit(ctx.lvalue().expression());
            if(!(indexType instanceof IntType))
            {
                reportError(ctx, "Index must be of type int");
                return new NoType();
            }
            
            b.isArray = false;
            if(!b.equals(a))
            {
                reportError(ctx, "Type mismatch");
                b.isArray = true;
                return new NoType();
            }
            b.isArray = true;
            var.emitArrayStore(codeGenerator);
        }
        else
        {
            Type a = visit(ctx.expression());
            if(!b.equals(a))
            {
                reportError(ctx, "Type mismatch");
                return new NoType();
            }
            var.emitStore(codeGenerator);

        }
        return b;
    }

    @Override public Type visitWhileStatement(@NotNull GravelParser.WhileStatementContext ctx) { return visitChildren(ctx); }
    
    public Type visitAddExp(GravelParser.AddExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in addition");
        codeGenerator.emitProgramString("add " + a.getSizeStr());
        return a;
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */

    @Override public Type visitFunctionPtr(@NotNull GravelParser.FunctionPtrContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitString(@NotNull GravelParser.StringContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitLogOrExp(@NotNull GravelParser.LogOrExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    public Type visitArrayLookupExp(@NotNull GravelParser.ArrayLookupExpContext ctx)
    {
        boolean wasMute = codeGenerator.mute;
        codeGenerator.mute(); // TODO: replace this shit with a straight TypeVisitor
        Type a = visit(ctx.expression(0));
        if(!wasMute)
            codeGenerator.unmute();
           
        Type t =  a.pushFrom(this, ctx);
        t.isArray = false;
        return t;
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitBrackets(@NotNull GravelParser.BracketsContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    public Type visitFunctionCallStatement(GravelParser.FunctionCallStatementContext ctx)
    {
        FunctionCallContext c = ctx.functionCall();
        if(c.identifier().size() > 1)
        {
            String objName = c.identifier(0).getText();
            String methodName = c.identifier(1).getText();
            
            ClassInstanceSymbol classInstanceSymbol = currentScope.getClassInstance(objName);
            if(classInstanceSymbol == null)
                return reportError(ctx, "Undeclared identifier " + objName);
    
            ClassScope classScope = classInstanceSymbol.getClassScope();
            MethodSymbol methodSymbol = classScope.getMethod(methodName);
            if(methodSymbol == null)
                return reportError(ctx, "Undeclared method " + methodName);
           
            List<Type> signature = methodSymbol.getArguments();
            if(c.expression().size() != signature.size() - 1)
                return reportError(ctx, "Method arity mismatch");
            for(int i = signature.size() - 1; i >= 0; i--)
            {
                Type ts = signature.get(i);
                Type te = visit(c.expression(i));
                if(!ts.equals(te))
                    return reportError(ctx, "Argument mismatch");
            }
    
            codeGenerator.emitProgramString("push " + classInstanceSymbol.getLabel());
            codeGenerator.emitProgramString("push " + methodSymbol.getLabel());
            codeGenerator.emitProgramString("push " + classInstanceSymbol.getLabel());
            codeGenerator.emitProgramString("sync");
            if(methodSymbol.getReturnType().getMemorySize() > 0)
                codeGenerator.emitProgramString("pop " + methodSymbol.getReturnType().getMemorySize());
            return signature.get(0);
        }
        else
        {
            String methodName = c.identifier(0).getText();
            
            MethodSymbol methodSymbol = currentScope.getMethod(methodName);
            if(methodSymbol == null)
            {
                methodSymbol = currentScope.getExternMethod(methodName);
                if(methodSymbol == null)
                    return reportError(ctx, "Undeclared method " + methodName);
            }
           
            List<Type> signature = methodSymbol.getArguments();
            if(c.expression().size() != signature.size())
                return reportError(ctx, "Method arity mismatch");
            for(int i = signature.size() - 1; i >= 0; i--)
            {
                Type ts = signature.get(i);
                Type te = visit(c.expression(i));
                if(!ts.equals(te))
                    return reportError(ctx, "Argument mismatch");
            }
    
            codeGenerator.emitProgramString("push word [$fp+4]");
            codeGenerator.emitProgramString("call");
            
            if(methodSymbol.getReturnType().getMemorySize() > 0)
                codeGenerator.emitProgramString("pop " + methodSymbol.getReturnType().getMemorySize());
            return methodSymbol.getReturnType();
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitGtExp(@NotNull GravelParser.GtExpContext ctx) { return visitChildren(ctx); }
    
    @Override public Type visitParExp(@NotNull GravelParser.ParExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitEqExp(@NotNull GravelParser.EqExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitLogAndExp(@NotNull GravelParser.LogAndExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitGteExp(@NotNull GravelParser.GteExpContext ctx) { return visitChildren(ctx); }
}
