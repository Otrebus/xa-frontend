package se.neava.compiler;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import se.neava.compiler.scope.GlobalScope;
import se.neava.compiler.scope.MethodScope;
import se.neava.compiler.scope.Scope;
import se.neava.compiler.symbol.MethodSymbol;
import se.neava.compiler.symbol.VariableSymbol;
import se.neava.compiler.type.NoType;
import se.neava.compiler.type.Type;
import se.neava.compiler.type.VoidType;

public class CodeGeneratorVisitor extends GravelBaseVisitor<Type>
{
    Scope currentScope;
    boolean hadError = false;
    List<String> error = new LinkedList<String>();
    CodeGenerator codeGenerator;
    
    CodeGeneratorVisitor()
    {
        codeGenerator = new CodeGenerator();
    }
    
    String getCode()
    {
        return codeGenerator.getCode();
    }
    
    void reportError(ParserRuleContext ctx, String str)
    {
        hadError = true;
        error.add(new String("Line " + ctx.start.getLine() + ": " + str));
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
        
        if(identifierName.equals("main") && className.equals("Main"))
            codeGenerator.emitDataDirective(".entry");
        String lbl = codeGenerator.makeLabel(identifierName);
        codeGenerator.emitDataLabel(lbl);
        int size = currentScope.getClassScope(className).getSize();
        codeGenerator.emitDataln("byte[" + size + "]");
        
        return visitChildren(ctx); 
    }

    public Type visitClassDefinition(GravelParser.ClassDefinitionContext ctx) 
    {
        String name = ctx.identifier().getText();
        currentScope = currentScope.getClassScope(name);
        return visitChildren(ctx); 
    }
    
    public Type visitMethodDefinition(GravelParser.MethodDefinitionContext ctx) 
    { 
        currentScope = new MethodScope(currentScope, ctx);
        MethodSymbol s = currentScope.getMethod(ctx.identifier(0).getText());
        codeGenerator.emitProgramLabel(s.getLabel());
        return visitChildren(ctx);
    }
    
    public Type visitMethodVariableDefinition(@NotNull GravelParser.MethodVariableDefinitionContext ctx) 
    { 
        if(!((MethodScope) currentScope).addVariable(ctx.identifier().getText(), Type.createType(ctx.type())))
            reportError(ctx, "Maybe not overload argument " + ctx.identifier().getText());
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
        return s.getType();
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
       
    @Override public Type visitReturnStatement(@NotNull GravelParser.ReturnStatementContext ctx) 
    {
        if(ctx.expression() == null)
            return new VoidType();
        Type t = visit(ctx.expression());
        if(!t.equals(((MethodScope) currentScope).getMethod().getSignature().get(0)))
        {
            reportError(ctx, "Return type mismatch.");
            return new NoType();
        }
        return t;
    }
    
    @Override public Type visitNumExp(GravelParser.NumExpContext ctx) 
    { 
        Type type = Type.createType(ctx.baseType());
        codeGenerator.emitProgramString("push " + type.getSizeStr() + " " + Integer.parseInt(ctx.NUM().getText()));
        return type; 
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitLteExp(@NotNull GravelParser.LteExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitIndirectionExp(@NotNull GravelParser.IndirectionExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */

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
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitFunctionCall(@NotNull GravelParser.FunctionCallContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitMulExp(@NotNull GravelParser.MulExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitTime(@NotNull GravelParser.TimeContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitSubExp(@NotNull GravelParser.SubExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitBaseType(@NotNull GravelParser.BaseTypeContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitAsyncStatement(@NotNull GravelParser.AsyncStatementContext ctx) { return visitChildren(ctx); }
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
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitFunctionCallExp(@NotNull GravelParser.FunctionCallExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitStatement(@NotNull GravelParser.StatementContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitAssignment(@NotNull GravelParser.AssignmentContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitMethodBody(@NotNull GravelParser.MethodBodyContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitLvalue(@NotNull GravelParser.LvalueContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitWhileStatement(@NotNull GravelParser.WhileStatementContext ctx) { return visitChildren(ctx); }
    
    public Type visitAddExp(@NotNull GravelParser.AddExpContext ctx) 
    { 
        Type a = visit(ctx.expression(1));
        Type b = visit(ctx.expression(0));
        if(!a.equals(b))
        {
            reportError(ctx, "Type match error in addition");
            return new NoType();
        }
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
    @Override public Type visitArrayLookupExp(@NotNull GravelParser.ArrayLookupExpContext ctx) { return visitChildren(ctx); }
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
    @Override public Type visitFunctionCallStatement(@NotNull GravelParser.FunctionCallStatementContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Type visitIdentifier(@NotNull GravelParser.IdentifierContext ctx) { return visitChildren(ctx); }
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
