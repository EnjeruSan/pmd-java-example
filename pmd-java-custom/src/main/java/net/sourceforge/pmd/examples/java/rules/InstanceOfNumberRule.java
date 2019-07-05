package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

import java.util.Random;

public class InstanceOfNumberRule extends AbstractJavaRule {

    private Random random = new Random();

    public InstanceOfNumberRule() {
        addRuleChainVisit(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        addViolation(data, node, String.valueOf(random.nextInt(2)));
        return data;
    }
}