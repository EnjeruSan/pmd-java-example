package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

import java.util.Random;

public class DependencyNumberRule extends AbstractJavaRule {

    private Random random = new Random();

    public DependencyNumberRule() {
        addRuleChainVisit(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        addViolation(data, node, String.valueOf(random.nextInt(2)));
        return data;
    }
}