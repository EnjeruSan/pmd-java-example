package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.util.NumericConstants;

public class MethodCountRule extends ExcessiveNodeCountRule {

    public MethodCountRule() {
        super(ASTCompilationUnit.class);
        setProperty(MINIMUM_DESCRIPTOR, 1d);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        return NumericConstants.ONE;
    }
}
