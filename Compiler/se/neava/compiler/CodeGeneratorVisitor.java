package se.neava.compiler;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import se.neava.compiler.scope.GlobalScope;
import se.neava.compiler.scope.Scope;
import se.neava.compiler.type.Type;

public class CodeGeneratorVisitor extends GravelBaseVisitor<Void>
{
    Scope currentScope;
    boolean hadError = false;
    List<String> error = new LinkedList<String>();
    CodeGenerator codeGen;
    
    CodeGeneratorVisitor()
    {
        codeGen = new CodeGenerator();
    }
    
    String getCode()
    {
        return codeGen.getCode();
    }
    
    void reportError(ParserRuleContext ctx, String str)
    {
        hadError = true;
        error.add(new String("Line " + ctx.start.getLine() + ": " + str));
    }
    
    boolean passed()
    {
        return !hadError;
    }

    public Void visitProgram(GravelParser.ProgramContext ctx) 
    {
        currentScope = new GlobalScope(ctx);
        return visitChildren(ctx); 
    }    
    
    public Void visitExternDeclaration(GravelParser.ExternDeclarationContext ctx) 
    {
        String name = ctx.identifier().getText();
        String lbl = codeGen.makeLabel(name);
        currentScope.getMethod(name).setLabel(lbl);
        
        codeGen.emitExternLabel(lbl);
        codeGen.emitExternln("\"" + name + "\"");
        return visitChildren(ctx); 
    }

    public Void visitClassDefinition(GravelParser.ClassDefinitionContext ctx) 
    {
        String name = ctx.identifier().getText();
        currentScope = currentScope.getClassScope(name);
        String lbl = codeGen.makeLabel(name);
        currentScope.getClassScope(name).setLabel(lbl);
        
        if(name.equals("Main"))
            codeGen.emitDataDirective(".entry");
        codeGen.emitDataLabel(lbl);
        codeGen.emitDataln("dword 0");
        
        return visitChildren(ctx); 
    }
    
    public Void visitMethodVariableDefinition(@NotNull GravelParser.MethodVariableDefinitionContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitParExp(@NotNull GravelParser.ParExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitReturnStatement(@NotNull GravelParser.ReturnStatementContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitLteExp(@NotNull GravelParser.LteExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitIndirectionExp(@NotNull GravelParser.IndirectionExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitClassInstanceDeclaration(@NotNull GravelParser.ClassInstanceDeclarationContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitTrueExp(@NotNull GravelParser.TrueExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitType(@NotNull GravelParser.TypeContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitLtExp(@NotNull GravelParser.LtExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitFunctionCall(@NotNull GravelParser.FunctionCallContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitMulExp(@NotNull GravelParser.MulExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitTime(@NotNull GravelParser.TimeContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitSubExp(@NotNull GravelParser.SubExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitBaseType(@NotNull GravelParser.BaseTypeContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitAsyncStatement(@NotNull GravelParser.AsyncStatementContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitFalseExp(@NotNull GravelParser.FalseExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitMethodDefinition(@NotNull GravelParser.MethodDefinitionContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */

    @Override public Void visitClassVariableDeclaration(@NotNull GravelParser.ClassVariableDeclarationContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitNumber(@NotNull GravelParser.NumberContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitDivExp(@NotNull GravelParser.DivExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitIfStatement(@NotNull GravelParser.IfStatementContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitFunctionCallExp(@NotNull GravelParser.FunctionCallExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitStatement(@NotNull GravelParser.StatementContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitAssignment(@NotNull GravelParser.AssignmentContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitMethodBody(@NotNull GravelParser.MethodBodyContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitLvalue(@NotNull GravelParser.LvalueContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitWhileStatement(@NotNull GravelParser.WhileStatementContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitAddExp(@NotNull GravelParser.AddExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitNumExp(@NotNull GravelParser.NumExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitFunctionPtr(@NotNull GravelParser.FunctionPtrContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitString(@NotNull GravelParser.StringContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitLogOrExp(@NotNull GravelParser.LogOrExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitArrayLookupExp(@NotNull GravelParser.ArrayLookupExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitBrackets(@NotNull GravelParser.BracketsContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitFunctionCallStatement(@NotNull GravelParser.FunctionCallStatementContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitIdentifier(@NotNull GravelParser.IdentifierContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitGtExp(@NotNull GravelParser.GtExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitEqExp(@NotNull GravelParser.EqExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitLogAndExp(@NotNull GravelParser.LogAndExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Void visitGteExp(@NotNull GravelParser.GteExpContext ctx) { return visitChildren(ctx); }
}
