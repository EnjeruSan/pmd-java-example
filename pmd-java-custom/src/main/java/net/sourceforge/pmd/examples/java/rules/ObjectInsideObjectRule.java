package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.util.NumericConstants;

public class ObjectInsideObjectRule extends ExcessiveNodeCountRule{
    public ObjectInsideObjectRule() {
        super(ASTCompilationUnit.class);
        setProperty(MINIMUM_DESCRIPTOR, 1d);
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        System.out.println("ASTVariableDeclaratorId " + node.getVariableName() + " Image " + node.getImage());
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTVariableDeclarator node, Object data) {
        System.out.println("ASTVariableDeclarator " + node.getName() + " Image " + node.getImage());
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTVariableInitializer node, Object data) {
        System.out.println("ASTVariableInitializer " + node.getXPathNodeName() + " Image " + node.getImage());
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        System.out.println("ASTLocalVariableDeclaration " + node.getVariableName() + " Image " + node.getImage());
        return NumericConstants.ONE;
    }
}
