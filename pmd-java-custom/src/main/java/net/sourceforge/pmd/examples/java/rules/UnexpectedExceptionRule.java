package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

import java.util.List;

public class UnexpectedExceptionRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        ASTTryStatement parent = node.getFirstParentOfType(ASTTryStatement.class);
        if (parent == null) {
            return data;
        }
        for (parent = parent.getFirstParentOfType(ASTTryStatement.class); parent != null; parent = parent
                .getFirstParentOfType(ASTTryStatement.class)) {

            List<ASTCatchStatement> list = parent.findDescendantsOfType(ASTCatchStatement.class);
            for (ASTCatchStatement catchStmt : list) {
                ASTFormalParameter fp = (ASTFormalParameter) catchStmt.jjtGetChild(0);
                ASTType type = fp.getFirstDescendantOfType(ASTType.class);
                ASTClassOrInterfaceType name = type.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
                if (node.getFirstClassOrInterfaceTypeImage() != null
                        && node.getFirstClassOrInterfaceTypeImage().equals(name.getImage())) {
                    addViolation(data, name);
                }
            }
        }
        return data;
    }
}