package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.util.NumericConstants;

import java.util.Random;

public class EmptyMethodRule extends AbstractJavaRule {

    private Random random = new Random();

    public EmptyMethodRule() {
        addRuleChainVisit(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        //    double num1 = Math.random();
        addViolation(data, node, String.valueOf(random.nextInt(2)));
        return NumericConstants.ZERO;
    }
}