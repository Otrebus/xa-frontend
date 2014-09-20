package se.neava.compiler;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

/**
 * Somewhat monolithic visitor class that goes through each rule in the parse tree and generates
 * assembly code.
 */
public class CodeGeneratorVisitor extends GravelBaseVisitor<Type>
{
    Scope currentScope;
    boolean hadError = false;
    List<String> error = new LinkedList<String>();
    boolean mute;
    
    String dataString = "";
    String programString = ".program\n";
    String externString = ".extern\n";
    Set<String> labels = new TreeSet<String>();
    int labelNo = 0;
    
    /**
     * Constructor.
     */
    CodeGeneratorVisitor()
    {
    }
    
    /**
     * Adds a (zero-terminated) string to the data segment.
     * @param str The string to add.
     * @return A label pointing to the string.
     */
    public String addStringLiteral(String str)
    {
        String lbl = makeLabel(str.replace(' ', '_'));
        emitDataLabel(lbl);
        emitDataString("\"" + str + "\"");
        return lbl;
    }
    
    /**
     * Creates and returns a unique label.
     * @return The name of the label.
     */
    public String makeLabel()
    {
        String lbl = "label" + labelNo++;
        if(labels.contains(lbl))
            return makeLabel(); // A mischievous user could overflow the stack here
        labels.add(lbl);
        return lbl;
    }
    
    /**
     * Creates and returns a unique label with name based on a provided suggestion.
     * @param suggestion The preferred label name.
     * @return The name of the generated label.
     */
    public String makeLabel(String suggestion)
    {
        String str = suggestion;
        for(int n = 2; labels.contains(str); n++)
            str = suggestion + n;
        labels.add(str);
        return str;
    }
    
    /**
     * Returns the code generated so far.
     * @return the code generated so far.
     */
    public String getCode()
    {
        return dataString + programString + externString;
    }
    
    // The next number of methods are too numerous and could be replaced by just two with
    // an additional argument
    /**
     * Adds a label to the program segment.
     * @param str The label to add.
     */
    public void emitProgramLabel(String str)
    {
        if(!mute)
            programString += str + ":\n";
    }
    
    /**
     * Adds a directive to the program segment.
     * @param str The directive to add.
     */
    public void emitProgramDirective(String str)
    {
        if(!mute)
            programString += str + "\n";
    }
    
    /**
     * Adds an indented line to the program segment.
     * @param str The line to add.
     */
    public void emitProgramString(String str)
    {
        if(!mute)
            programString += "  " + str + "\n";
    }
    
    /**
     * Adds a label to the data segment.
     * @param str The label to add.
     */
    public void emitDataLabel(String str)
    {
        if(!mute)
            dataString += str + ":\n";
    }
    
    /**
     * Adds an indented string to the data segment.
     * @param str The line to add.
     */
    public void emitDataString(String str)
    {
        if(!mute)
            dataString += "  " + str + "\n";
    }
    
    /**
     * Adds a directive to the data segment.
     * @param str The directive to add.
     */
    public void emitDataDirective(String str)
    {
        if(!mute)
            dataString += str + "\n";
    }
    
    /**
     * Adds a label to the extern segment.
     * @param str The label to add.
     */
    public void emitExternLabel(String str)
    {
        if(!mute)
            externString += str + ":\n";
    }
    
    /**
     * Adds a string to the extern segment.
     * @param str The string to add.
     */
    public void emitExternString(String str)
    {
        if(!mute)
            externString += "  " + str + "\n";
    }
    
    /**
     * Adds a directive to the extern segment.
     * @param str The directive to add.
     */
    public void emitExternDirective(String str)
    {
        if(!mute)
            externString += str + "\n";
    }
    
    /**
     * Stops the code generator from outputting code when visiting parse nodes.
     */
    public void mute()
    {
        mute = true;
    }
    
    /**
     * Makes the code generator resume outputting code when visiting parse nodes.
     */
    public void unmute()
    {
        mute = false;
    }
    
    /**
     * Adds an error to the error backlog and returns a NoType. Used instead of throwing an
     * unchecked exception.
     * @param ctx The rule context from which the error is thrown.
     * @param str The error string.
     * @return A NoType type.
     */
    public NoType reportError(ParserRuleContext ctx, String str)
    {
        hadError = true;
        error.add(new String(ctx != null ? ("Line " + ctx.start.getLine() + ": " + str) : "" + str));
        return new NoType();
    }
    
    /**
     * Returns the list of errors.
     * @return the list of errors.
     */
    public List<String> getErrors()
    {
        return error;
    }
    
    /**
     * Returns true if an error occurred during compilation.
     * @return True if an error occurred during compilation, false if not.
     */
    boolean hadError()
    {
        return hadError;
    }
    
    /**
     * Adds the entry point of the program.
     * @return True if succeeded, false if not.
     */
    public boolean addEntryPoint()
    {
        emitDataDirective(".entry");
        emitDataLabel(makeLabel("dummyEntryObject"));
        emitDataString("dword 0");
        
        emitProgramDirective(".entry");
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
        
        if(!mainScope.isSingleton())
            emitProgramString("push " + mainSym.getName());
        emitProgramString("push " + mainSym.getName() + "_main");
        emitProgramString("push " + mainSym.getName());
        emitProgramString("sync");
        emitProgramString("ret 0");
        return true;
    }

    /**
     * Visits the topmost rule of the grammar.
     */
    public Type visitProgram(GravelParser.ProgramContext ctx) 
    {
        try 
        {
            currentScope = new GlobalScope(this, ctx);
        } catch (CompileException e) {
            return reportError(null, e.getMessage());
        }
        return visitChildren(ctx); 
    }    
    
    /**
     * Visits an extern declaration parse node. Addition of symbols into the symbol table
     * is handled by the visitProgram method and the GlobalScope class.
     */
    public Type visitExternDeclaration(GravelParser.ExternDeclarationContext ctx) 
    {
        String name = ctx.identifier().getText();
        String lbl = makeLabel(name);
        currentScope.getMethod(name).setLabel(lbl);
        
        emitExternLabel(lbl);
        emitExternString("\"" + name + "\"");
        return new NoType(); 
    }
    
    /**
     * Visits a class instance declaration node.
     */
    public Type visitClassInstanceDeclaration(@NotNull GravelParser.ClassInstanceDeclarationContext ctx) 
    { 
        String className = ctx.identifier(0).getText();
        String identifierName = ctx.identifier(1).getText();
        
        String lbl = makeLabel(identifierName);
        emitDataLabel(lbl);
        
        ClassScope classScope = currentScope.getClassScope(className);
        if(classScope == null)
            return reportError(ctx, "Class " + className + " not found");
        if(classScope.isSingleton())
            return reportError(ctx, "Object " + identifierName + " is already defined implicitly");
        int size = classScope.getSize();
        emitDataString("byte[" + size + "]");
        ClassInstanceSymbol sym = new ClassInstanceSymbol(classScope, identifierName);
        sym.setLabel(lbl);
        ((GlobalScope)currentScope).addClassInstance(sym);
        
        return visitChildren(ctx); 
    }

    /**
     * Visits the class definition node.
     */
    public Type visitClassDefinition(GravelParser.ClassDefinitionContext ctx) 
    {
        String name = ctx.identifier().getText();
        currentScope = currentScope.getClassScope(name);
        Type t = visitChildren(ctx);
        currentScope = currentScope.getParent();
        return t;
    }
    
    /**
     * Visits the method definition node.
     */
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

        emitProgramLabel(s.getLabel());
        Type t = visitChildren(ctx);
        currentScope = currentScope.getParent();
        return t;
    }
    
    /**
     * Visits the method body node.
     */
    public Type visitMethodBody(MethodBodyContext ctx)
    {
        for(MethodVariableDefinitionContext i : ctx.methodVariableDefinition())
            visit(i);
        // Make room for local variables on the stack
        if(((MethodScope) currentScope).getLocalVariableSize() > 0)
            emitProgramString("push " + ((MethodScope) currentScope).getLocalVariableSize());
        for(StatementContext i : ctx.statement())
            visit(i);
        visit(ctx.returnStatement());
        return new NoType();
    }
    
    /**
     * Visits the method variable definition node.
     */
    public Type visitMethodVariableDefinition(MethodVariableDefinitionContext ctx) 
    { 
        if(!((MethodScope) currentScope).addVariable(ctx.identifier().getText(), Type.createType(ctx.type())))
            reportError(ctx, "May not overload argument " + ctx.identifier().getText());
        return visitChildren(ctx); 
    }
    
    /**
     * Visits the "identifier" type of expression.
     */
    public Type visitIdentifierExp(GravelParser.IdentifierExpContext ctx) 
    {
        VariableSymbol s = currentScope.getVariable(ctx.getText());
        if(s == null)
        {
            reportError(ctx, "Undeclared identifier " + ctx.getText());
            return new NoType();
        }
        // Push the variable onto the stack
        s.emitLoad(this);
        return s.getType();
    }
    
    /**
     * Visits the return statement node.
     */
    public Type visitReturnStatement(@NotNull GravelParser.ReturnStatementContext ctx) 
    {
        Type t = ctx.expression() != null ? visit(ctx.expression()) : new VoidType();
        if(!t.equals(((MethodScope) currentScope).getMethod().getReturnType()))
            return reportError(ctx, "Return type mismatch.");
        if(t.isPointer())
            return reportError(ctx, "May not return pointers"); // For encapsulation. This could be
        MethodScope methodScope = (MethodScope) currentScope;   // relaxed for less dogmatism
        MethodSymbol sym = methodScope.getMethod(methodScope.getName());
        
        // Account for possible "this" pointer, and leave room for the return value if it's
        // bigger than the arguments
        int retSize = sym.getReturnType().getMemorySize();
        int argSize = sym.getTotalArgumentSize() + (((ClassScope) (currentScope.getParent())).isSingleton() ? 0 : 2);
        int retImm = Math.max(0, argSize - retSize);
        
        if(!t.equals(new VoidType()))
            emitProgramString("pop " + t.getSizeStr() + " [$fp+" + (4 + retImm) + "]");
        emitProgramString("ret " + retImm);
        return t;
    }
    
    /**
     * Visits the literal "number" expression node.
     */
    public Type visitNumExp(GravelParser.NumExpContext ctx) 
    { 
        String s = ctx.suffix().getText();
        Type type = Type.getTypeFromSuffix(s);
        if(type == null)
            reportError(ctx, "Unknown number suffix");
        emitProgramString("push " + type.getSizeStr() + " " + Integer.parseInt(ctx.NUM().getText()));
        return type; 
    }
    
    /**
     * Visits the "cast" expression node.
     */
    public Type visitCastExp(GravelParser.CastExpContext ctx)
    {
        Type castType = Type.createType(ctx.baseType());
        Type expType = visit(ctx.expression());
        if(!expType.castTo(this, castType))
            return reportError(ctx, "Illegal cast");
        return castType;
    }

    /**
     * Visits the "indirection" expression node, which pushes a method pointer onto the stack.
     */
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
        
        emitProgramString("push " + methodSymbol.getLabel());
        emitProgramString("push " + classInstanceSymbol.getLabel());
        return new FunctionPointerType(methodSymbol);
    }

    /**
     * Visits the "true" expression node.
     */
    public Type visitTrueExp(GravelParser.TrueExpContext ctx) 
    { 
        emitProgramString("push byte 1");
        return new BoolType();
    }
    
    /**
     * Visits the "false" expression node.
     */
    public Type visitFalseExp(GravelParser.FalseExpContext ctx) 
    { 
        emitProgramString("push byte 0");
        return new BoolType();
    }

    /**
     * Visits the async statement.
     */
    public Type visitAsyncStatement(GravelParser.AsyncStatementContext ctx) 
    { 
        ClassInstanceSymbol classInstanceSymbol = null;
        MethodSymbol methodSymbol;
        FunctionCallContext c = ctx.functionCall();
        
        if(c.identifier().size() > 1) // If both method name and object name was given
        {
            String objName = c.identifier(0).getText();
            String methodName = c.identifier(1).getText();
            
            classInstanceSymbol = currentScope.getClassInstance(objName);
            if(classInstanceSymbol == null)
                return reportError(ctx, "Undeclared class " + objName);

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
                    return reportError(ctx, "Undeclared method " + methodName);
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
        
        // After the regular arguments we might also push a "this" pointer
        if(classInstanceSymbol == null) // Happens if we only gave the method name and not class
        {
            // If we are an object (not a singleton), we find our "this" pointer on [$fp+4]
            if(!((ClassScope) currentScope.getParent()).isSingleton())
                emitProgramString("push word [$fp+4]");
        }
        else if(!((ClassScope)classInstanceSymbol.getClassScope()).isSingleton())
            emitProgramString("push " + (classInstanceSymbol.getClassScope().getLabel()));
        
        // After all this, the async instruction still needs an object and a method
        emitProgramString("push " + methodSymbol.getLabel());
        
        if(classInstanceSymbol == null)
        {
            if(!((ClassScope) currentScope.getParent()).isSingleton())
                emitProgramString("push word [$fp+4]");
            else
                emitProgramString("push " + ((ClassScope) currentScope.getParent()).getLabel());
        }
        else
            emitProgramString("push " + (classInstanceSymbol.getClassScope().getLabel()));
        
        // Handle the given time expressions - warning, ugly code ahead
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
                    emitProgramString("push dword 1000");
                    emitProgramString("mul dword");
                }
                else if(unit.equals("sec"))
                {
                    emitProgramString("push dword 1000000");
                    emitProgramString("mul dword");
                }
            }
            else
                emitProgramString("push dword 0");
            a = visit(ctx.expression(0));
            if(!(a instanceof LongType))
                return reportError(ctx, "Time must be of type long");
            unit = ctx.time(0).getText();
            if(unit.equals("msec"))
            {
                emitProgramString("push dword 1000");
                emitProgramString("mul dword");
            }
            else if(unit.equals("sec"))
            {
                emitProgramString("push dword 1000000");
                emitProgramString("mul dword");
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
                emitProgramString("push dword 1000");
                emitProgramString("mul dword");
            }
            else if(unit.equals("sec"))
            {
                emitProgramString("push dword 1000000");
                emitProgramString("mul dword");
            }
            emitProgramString("push dword 0");
        }
        
        emitProgramString("push byte " + (methodSymbol.getTotalArgumentSize() + 
                ((classInstanceSymbol == null && ((ClassScope) currentScope.getParent()).isSingleton() || classInstanceSymbol != null && classInstanceSymbol.getClassScope().isSingleton()) ? 0 : 2)));
        emitProgramString("async");
        
        return methodSymbol.getReturnType();
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
            var.emitArrayStore(this);
        }
        else
        {
            Type a = visit(ctx.expression());
            if(!b.isAssignableFrom(a))
                return reportError(ctx, "Type mismatch");
            var.emitStore(this);

        }
        return b;
    }

    public Type visitWhileStatement(GravelParser.WhileStatementContext ctx) 
    {
        String loopLabel = makeLabel();
        emitProgramLabel(loopLabel);
        Type a = visit(ctx.expression());
        if(!(a instanceof BoolType))
            return reportError(ctx, "Expression in if statement must be of type bool");
        String endLabel = makeLabel();
        emitProgramString("jez " + endLabel);
        visit(ctx.statement());
        emitProgramString("jmp " + loopLabel);
        emitProgramLabel(endLabel);
        return new NoType();
    }

    public Type visitArrayLookupExp(@NotNull GravelParser.ArrayLookupExpContext ctx)
    {
        boolean wasMute = mute;
        mute();
        Type a = visit(ctx.expression(0));
        if(!wasMute)
            unmute();
           
        Type t = a.pushFrom(this, ctx).clone();
        t.isArray = false;
        return t;
    }

    public Type visitFunctionCallStatement(GravelParser.FunctionCallStatementContext ctx)
    {
        FunctionCallContext c = ctx.functionCall();
        Type t = visit(c);
        if(t.getMemorySize() > 0)
            emitProgramString("pop " + t.getMemorySize());
        return t;
    }
    
    public Type methodCall(GravelParser.FunctionCallContext ctx, ClassInstanceSymbol classInstanceSymbol, MethodSymbol methodSymbol)
    {
        List<Type> arguments = methodSymbol.getArguments();
        if(ctx.expression().size() != arguments.size())
            return reportError(ctx, "Method arity mismatch");
        
        int retSize = methodSymbol.getReturnType().getMemorySize();
        int argSize = methodSymbol.getTotalArgumentSize() + (((ClassScope) (currentScope.getParent())).isSingleton() ? 0 : 2);
        if(retSize > argSize)
            emitProgramString("push " + (retSize - argSize));
        
        for(int i = arguments.size() - 1; i >= 0; i--)
        {
            Type ts = arguments.get(i);
            Type te = visit(ctx.expression(i));
            if(!ts.equals(te))
                return reportError(ctx, "Argument mismatch");
            if(ts.isArray())
                return reportError(ctx, "May not call foreign method with array argument");
        }
        
        if(!classInstanceSymbol.getClassScope().isSingleton())
            emitProgramString("push " + classInstanceSymbol.getLabel());
        emitProgramString("push " + methodSymbol.getLabel());
        emitProgramString("push " + classInstanceSymbol.getLabel());
        emitProgramString("sync");
        return methodSymbol.getReturnType();       
    }
    
    public Type functionCall(GravelParser.FunctionCallContext ctx, MethodSymbol methodSymbol)
    {
        List<Type> arguments = methodSymbol.getArguments();
        if(ctx.expression().size() != arguments.size())
            return reportError(ctx, "Method arity mismatch");
        
        int retSize = methodSymbol.getReturnType().getMemorySize();
        int argSize = methodSymbol.getTotalArgumentSize() + (((ClassScope) (currentScope.getParent())).isSingleton() ? 0 : 2);
        if(retSize > argSize)
            emitProgramString("push " + (retSize - argSize));
        
        for(int i = arguments.size() - 1; i >= 0; i--)
        {
            Type ts = arguments.get(i);
            Type te = visit(ctx.expression(i));
            if(!ts.equals(te))
                return reportError(ctx, "Argument mismatch");
        }

        if(!((ClassScope) currentScope.getParent()).isSingleton())
            emitProgramString("push word [$fp+4]");
        emitProgramString("call " + methodSymbol.getLabel());
        return methodSymbol.getReturnType();
    }
    
    public Type visitStringExp(GravelParser.StringExpContext ctx)
    {
        String str = ctx.string().getText().replaceAll("\"", "");
        String lbl = addStringLiteral(str);
        emitProgramString("push " + lbl);
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
            emitProgramString("push " + (retSize - argSize));
        
        for(int i = arguments.size() - 1; i >= 0; i--)
        {
            Type ts = arguments.get(i);
            Type te = visit(ctx.expression(i));
            if(!ts.equals(te))
                return reportError(ctx, "Argument mismatch");
        }

        emitProgramString("call " + methodSymbol.getLabel());
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
        emitProgramString("sub " + a.getSizeStr());
        emitProgramString("sgz " + a.getSizeStr());
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
        emitProgramString("sub " + a.getSizeStr());
        emitProgramString("sez " + a.getSizeStr());
        return new BoolType();
    }
    
    public Type visitLtExp(GravelParser.LtExpContext ctx)
    { 
        Type a = visit(ctx.expression(0));
        Type b = visit(ctx.expression(1));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        emitProgramString("sub " + a.getSizeStr());
        emitProgramString("sgz " + a.getSizeStr());
        return new BoolType();
    }
    
    public Type visitLteExp(GravelParser.LteExpContext ctx) 
    { 
        Type a = visit(ctx.expression(0));
        Type b = visit(ctx.expression(1));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        emitProgramString("sub " + a.getSizeStr());
        emitProgramString("sgez " + a.getSizeStr());
        return new BoolType(); 
    }

    public Type visitLogAndExp(GravelParser.LogAndExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b) || !(a instanceof BoolType))
            return reportError(ctx, "Both arguments to && must be of type bool");
        emitProgramString("and byte");
        return new BoolType();
    }

    public Type visitGteExp(GravelParser.GteExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        emitProgramString("sub " + a.getSizeStr());
        emitProgramString("sgez " + a.getSizeStr());
        return new BoolType(); 
    }
    
    public Type visitLogOrExp(GravelParser.LogOrExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b) || !(a instanceof BoolType))
            return reportError(ctx, "Both arguments to || must be of type bool");
        emitProgramString("or byte");
        return new BoolType();
    }
    
    @Override 
    public Type visitBitOrExp(GravelParser.BitOrExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        emitProgramString("or " + a.getSizeStr());
        return a;
    }
    
    @Override 
    public Type visitBitAndExp(GravelParser.BitAndExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        emitProgramString("and " + a.getSizeStr());
        return a;
    }
    
    @Override 
    public Type visitXorExp(GravelParser.XorExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        emitProgramString("xor " + a.getSizeStr());
        return a;
    }
    
    @Override
    public Type visitNeqExp(GravelParser.NeqExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type mismatch");
        emitProgramString("sub " + a.getSizeStr());
        emitProgramString("snez " + a.getSizeStr());
        return new BoolType();
    }
    
    public Type visitDivExp(GravelParser.DivExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in div");
        emitProgramString("div " + a.getSizeStr());
        return a;
    }

    public Type visitAddExp(GravelParser.AddExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in add");
        emitProgramString("add " + a.getSizeStr());
        return a;
    }
    
    public Type visitMulExp(GravelParser.MulExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in mul");
        emitProgramString("mul " + a.getSizeStr());
        return a;
    }
    
    public Type visitModExp(GravelParser.ModExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in mul");
        emitProgramString("mod " + a.getSizeStr());
        return a;
    }
    
    public Type visitSubExp(GravelParser.SubExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
            return reportError(ctx, "Type match error in sub");
        emitProgramString("sub " + a.getSizeStr());
        return a;
    }
    @Override
    public Type visitNotExp(GravelParser.NotExpContext ctx)
    {
        Type a = visit(ctx.expression());
        if(!(a instanceof BoolType))
            return reportError(ctx, "Expression in negation must be of type bool");
        emitProgramString("sez byte");
        return a;
    }
    
    public Type visitIfStatement(GravelParser.IfStatementContext ctx) 
    { 
        Type a = visit(ctx.expression());
        if(!(a instanceof BoolType))
            return reportError(ctx, "Expression in if statement must be of type bool");
        String doneLabel = makeLabel();
        if(ctx.elseClause() == null) 
        {
            emitProgramString("jez " + doneLabel);
            visit(ctx.statement());
        }
        else
        {
            String elseLabel = makeLabel();
            emitProgramString("jez " + elseLabel);
            visit(ctx.statement());
            emitProgramString("jmp " + doneLabel);
            emitProgramLabel(elseLabel);
            visit(ctx.elseClause().statement());
        }
        emitProgramLabel(doneLabel);
        return new NoType(); 
    }
}
