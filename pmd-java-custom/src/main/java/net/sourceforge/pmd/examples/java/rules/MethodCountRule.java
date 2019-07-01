package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.util.NumericConstants;

public class MethodCountRule extends ExcessiveNodeCountRule {

    private int counter = NumericConstants.ZERO;

    public MethodCountRule() {
        super(ASTCompilationUnit.class);
        setProperty(MINIMUM_DESCRIPTOR, 1d);
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        System.out.println("ASTMethodDeclaration " + counter++ + "instance is " + nodeClass.isInstance(node));
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        System.out.println("ASTConstructorDeclaration " + counter++ + "instance is " + nodeClass.isInstance(node));
        return NumericConstants.ONE;
    }
}
