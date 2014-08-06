package se.neava.compiler;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import se.neava.compiler.GravelParser.AddExpContext;
import se.neava.compiler.GravelParser.ArgListContext;
import se.neava.compiler.GravelParser.ArgTypeContext;
import se.neava.compiler.GravelParser.ArrayLookupExpContext;
import se.neava.compiler.GravelParser.AssignmentContext;
import se.neava.compiler.GravelParser.AsyncStatementContext;
import se.neava.compiler.GravelParser.BaseTypeContext;
import se.neava.compiler.GravelParser.ClassDeclarationContext;
import se.neava.compiler.GravelParser.ClassDefinitionContext;
import se.neava.compiler.GravelParser.ClassVariableDeclarationContext;
import se.neava.compiler.GravelParser.ClassVariableInitializerContext;
import se.neava.compiler.GravelParser.DeclarationArgListContext;
import se.neava.compiler.GravelParser.DivExpContext;
import se.neava.compiler.GravelParser.EqExpContext;
import se.neava.compiler.GravelParser.ExternArgTypeContext;
import se.neava.compiler.GravelParser.ExternDeclarationArgListContext;
import se.neava.compiler.GravelParser.ExternDeclarationContext;
import se.neava.compiler.GravelParser.FalseExpContext;
import se.neava.compiler.GravelParser.FunctionCallContext;
import se.neava.compiler.GravelParser.FunctionCallExpContext;
import se.neava.compiler.GravelParser.FunctionCallStatementContext;
import se.neava.compiler.GravelParser.FunctionPtrContext;
import se.neava.compiler.GravelParser.GtExpContext;
import se.neava.compiler.GravelParser.GteExpContext;
import se.neava.compiler.GravelParser.IdentifierContext;
import se.neava.compiler.GravelParser.IfStatementContext;
import se.neava.compiler.GravelParser.IndirectionExpContext;
import se.neava.compiler.GravelParser.LogAndExpContext;
import se.neava.compiler.GravelParser.LogOrExpContext;
import se.neava.compiler.GravelParser.LtExpContext;
import se.neava.compiler.GravelParser.LteExpContext;
import se.neava.compiler.GravelParser.LvalueContext;
import se.neava.compiler.GravelParser.MethodBodyContext;
import se.neava.compiler.GravelParser.MethodDefinitionContext;
import se.neava.compiler.GravelParser.MethodVariableDefinitionContext;
import se.neava.compiler.GravelParser.MulExpContext;
import se.neava.compiler.GravelParser.NumExpContext;
import se.neava.compiler.GravelParser.NumberContext;
import se.neava.compiler.GravelParser.ParExpContext;
import se.neava.compiler.GravelParser.ProgramContext;
import se.neava.compiler.GravelParser.ReturnStatementContext;
import se.neava.compiler.GravelParser.StatementContext;
import se.neava.compiler.GravelParser.StringContext;
import se.neava.compiler.GravelParser.SubExpContext;
import se.neava.compiler.GravelParser.TimeContext;
import se.neava.compiler.GravelParser.TrueExpContext;
import se.neava.compiler.GravelParser.TypeContext;
import se.neava.compiler.GravelParser.VoidTypeContext;
import se.neava.compiler.GravelParser.WhileStatementContext;

public class CodeGeneratorVisitor extends GravelBaseVisitor {

}
