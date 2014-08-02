package se.neava.compiler;

import se.neava.compiler.GravelParser.*;

public class CodeGeneratorListener extends GravelBaseListener {
    
    @Override
    public void enterDeclaration(DeclarationContext ctx)
    {
        System.out.println("declaration");
        System.out.println(ctx.getText());
    }
    
    @Override
    public void enterIdentifier(IdentifierContext ctx)
    {
        System.out.println("identifier");
        System.out.println(ctx.getText());
    }
    
    @Override
    public void enterDataType(DataTypeContext ctx)
    {
        System.out.println("data type");
        System.out.println(ctx.getText());
    }
}
