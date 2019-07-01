package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

import java.util.Iterator;
import java.util.List;

public class JustReturnMethodRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isAbstract()) {
            return data;
        }

        List<ASTReturnStatement> returnNodes = node.findDescendantsOfType(ASTReturnStatement.class);

        if (returnNodes.size() > 1) {
            for (Iterator<ASTReturnStatement> i = returnNodes.iterator(); i.hasNext();) {
                Node problem = i.next();
                // skip the last one, it's OK
                if (!i.hasNext()) {
                    continue;
                }
                addViolation(data, problem);
            }
        }
        return data;
    }
}