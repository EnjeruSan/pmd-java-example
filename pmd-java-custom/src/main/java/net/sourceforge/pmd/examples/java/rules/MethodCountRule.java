package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.util.NumericConstants;

public class MethodCountRule extends ExcessiveNodeCountRule {

    private int counter = NumericConstants.ZERO;

    public MethodCountRule() {
        super(ASTMethodOrConstructorDeclaration.class);
        setProperty(MINIMUM_DESCRIPTOR, 1d);
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        System.out.println("ASTMethodDeclaration " + counter++);
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        System.out.println("ASTConstructorDeclaration " + counter++);
        return NumericConstants.ONE;
    }
}
