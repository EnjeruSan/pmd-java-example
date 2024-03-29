package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.AbstractLombokAwareRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class MethodsClusterNumberRule extends AbstractLombokAwareRule {

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return Arrays.asList("lombok.experimental.UtilityClass");
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (hasIgnoredAnnotation(node)) {
            return data;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody decl, Object data) {
        Object result = super.visit(decl, data);

        if (decl.jjtGetParent() instanceof ASTClassOrInterfaceDeclaration) {
            ASTClassOrInterfaceDeclaration parent = (ASTClassOrInterfaceDeclaration) decl.jjtGetParent();
            if (parent.isAbstract() || parent.isInterface() || parent.getSuperClassTypeNode() != null) {
                return result;
            }

            if (hasLombokNoArgsConstructor(parent)) {
                return result;
            }

            int i = decl.jjtGetNumChildren();
            int methodCount = 0;
            boolean isOK = false;
            while (i > 0) {
                Node p = decl.jjtGetChild(--i);
                if (p.jjtGetNumChildren() == 0) {
                    continue;
                }
                Node n = skipAnnotations(p);
                if (n instanceof ASTFieldDeclaration) {
                    if (!((ASTFieldDeclaration) n).isStatic()) {
                        isOK = true;
                        break;
                    }
                } else if (n instanceof ASTConstructorDeclaration) {
                    if (((ASTConstructorDeclaration) n).isPrivate()) {
                        isOK = true;
                        break;
                    }
                } else if (n instanceof ASTMethodDeclaration) {
                    ASTMethodDeclaration m = (ASTMethodDeclaration) n;
                    if (!m.isPrivate()) {
                        methodCount++;
                    }
                    if (!m.isStatic()) {
                        isOK = true;
                        break;
                    }

                    // TODO use symbol table
                    if (m.getMethodName().equals("suite")) {
                        ASTResultType res = m.getResultType();
                        ASTClassOrInterfaceType c = res.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
                        if (c != null && c.hasImageEqualTo("Test")) {
                            isOK = true;
                            break;
                        }
                    }
                }
            }
            if (!isOK && methodCount > 0) {
                addViolation(data, decl);
            }
        }
        return result;
    }

    private boolean hasLombokNoArgsConstructor(ASTClassOrInterfaceDeclaration parent) {
        // check if there's a lombok no arg private constructor, if so skip the rest of the rules
        ASTAnnotation annotation = parent.getAnnotation("lombok.NoArgsConstructor");

        if (annotation != null) {

            List<ASTMemberValuePair> memberValuePairs = annotation.findDescendantsOfType(ASTMemberValuePair.class);

            for (ASTMemberValuePair memberValuePair : memberValuePairs) {
                // to set the access level of a constructor in lombok, you set the access property on the annotation
                if ("access".equals(memberValuePair.getImage())) {
                    List<ASTName> names = memberValuePair.findDescendantsOfType(ASTName.class);

                    for (ASTName name : names) {
                        // check to see if the value of the member value pair ends PRIVATE.  This is from the AccessLevel enum in Lombok
                        if (name.getImage().endsWith("PRIVATE")) {
                            // if the constructor is found and the accesslevel is private no need to check anything else
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private Node skipAnnotations(Node p) {
        int index = 0;
        Node n = p.jjtGetChild(index++);
        while (n instanceof ASTAnnotation && index < p.jjtGetNumChildren()) {
            n = p.jjtGetChild(index++);
        }
        return n;
    }
}