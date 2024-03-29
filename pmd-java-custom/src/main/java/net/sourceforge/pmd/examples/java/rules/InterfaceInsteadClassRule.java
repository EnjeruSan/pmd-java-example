package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;

import java.util.ArrayList;
import java.util.List;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;


public class InterfaceInsteadClassRule extends AbstractJavaRule {
    private final List<String> exceptions;
    private boolean ignoreAnnotations;
    private static final String CLONE = "clone";
    private static final String OBJECT = "Object";

    private static final PropertyDescriptor<Boolean> IGNORE_ANNOTATIONS_DESCRIPTOR = booleanProperty("ignoreAnnotations").defaultValue(false).desc("Ignore annotations").build();


    public InterfaceInsteadClassRule() {
        definePropertyDescriptor(IGNORE_ANNOTATIONS_DESCRIPTOR);

        exceptions = new ArrayList<>(1);
        exceptions.add("CloneNotSupportedException");
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        init();
        return super.visit(node, data);
    }

    private void init() {
        ignoreAnnotations = getProperty(IGNORE_ANNOTATIONS_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration clz, Object data) {
        if (clz.isInterface()) {
            return data;
        }
        return super.visit(clz, data);
    }

    private boolean isMethodType(ASTMethodDeclaration node, String methodType) {
        boolean result = false;
        ASTResultType type = node.getResultType();
        if (type != null) {
            result = type.hasDescendantMatchingXPath(
                    "./Type/ReferenceType/ClassOrInterfaceType[@Image = '" + methodType + "']");
        }
        return result;
    }

    private boolean isMethodThrowingType(ASTMethodDeclaration node, List<String> exceptedExceptions) {
        boolean result = false;
        ASTNameList thrownsExceptions = node.getFirstChildOfType(ASTNameList.class);
        if (thrownsExceptions != null) {
            List<ASTName> names = thrownsExceptions.findChildrenOfType(ASTName.class);
            for (ASTName name : names) {
                for (String exceptedException : exceptedExceptions) {
                    if (exceptedException.equals(name.getImage())) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private boolean hasArguments(ASTMethodDeclaration node) {
        return node.hasDescendantMatchingXPath("./MethodDeclarator/FormalParameters/*");
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isAbstract() || node.isFinal() || node.isNative() || node.isSynchronized()) {
            return super.visit(node, data);
        }
        if (CLONE.equals(node.getMethodName()) && node.isPublic() && !this.hasArguments(node)
                && this.isMethodType(node, OBJECT) && this.isMethodThrowingType(node, exceptions)) {
            return super.visit(node, data);
        }

        ASTBlock block = node.getBlock();
        if (block == null) {
            return super.visit(node, data);
        }
        // Only process functions with one BlockStatement
        if (block.jjtGetNumChildren() != 1 || block.findDescendantsOfType(ASTStatement.class).size() != 1) {
            return super.visit(node, data);
        }

        Node statement = block.jjtGetChild(0).jjtGetChild(0);
        if (statement.jjtGetChild(0).jjtGetNumChildren() == 0) {
            return data; // skips empty return statements
        }
        Node statementGrandChild = statement.jjtGetChild(0).jjtGetChild(0);
        ASTPrimaryExpression primaryExpression;

        if (statementGrandChild instanceof ASTPrimaryExpression) {
            primaryExpression = (ASTPrimaryExpression) statementGrandChild;
        } else {
            List<ASTPrimaryExpression> primaryExpressions = findFirstDegreeChildrenOfType(statementGrandChild,
                    ASTPrimaryExpression.class);
            if (primaryExpressions.size() != 1) {
                return super.visit(node, data);
            }
            primaryExpression = primaryExpressions.get(0);
        }

        ASTPrimaryPrefix primaryPrefix = findFirstDegreeChildrenOfType(primaryExpression, ASTPrimaryPrefix.class)
                .get(0);
        if (!primaryPrefix.usesSuperModifier()) {
            return super.visit(node, data);
        }

        List<ASTPrimarySuffix> primarySuffixList = findFirstDegreeChildrenOfType(primaryExpression,
                ASTPrimarySuffix.class);
        if (primarySuffixList.size() != 2) {
            // extra method call on result of super method
            return super.visit(node, data);
        }

        ASTMethodDeclarator methodDeclarator = findFirstDegreeChildrenOfType(node, ASTMethodDeclarator.class).get(0);
        ASTPrimarySuffix primarySuffix = primarySuffixList.get(0);
        if (!primarySuffix.hasImageEqualTo(methodDeclarator.getImage())) {
            return super.visit(node, data);
        }

        primarySuffix = primarySuffixList.get(1);
        ASTArguments arguments = (ASTArguments) primarySuffix.jjtGetChild(0);
        ASTFormalParameters formalParameters = (ASTFormalParameters) methodDeclarator.jjtGetChild(0);
        if (formalParameters.jjtGetNumChildren() != arguments.jjtGetNumChildren()) {
            return super.visit(node, data);
        }

        if (!ignoreAnnotations) {
            ASTClassOrInterfaceBodyDeclaration parent = (ASTClassOrInterfaceBodyDeclaration) node.jjtGetParent();
            for (int i = 0; i < parent.jjtGetNumChildren(); i++) {
                Node n = parent.jjtGetChild(i);
                if (n instanceof ASTAnnotation) {
                    if (n.jjtGetChild(0) instanceof ASTMarkerAnnotation) {
                        // @Override is ignored
                        if ("Override".equals(((ASTName) n.jjtGetChild(0).jjtGetChild(0)).getImage())) {
                            continue;
                        }
                    }
                    return super.visit(node, data);
                }
            }
        }

        if (arguments.jjtGetNumChildren() == 0) {
            addViolation(data, node, getMessage());
        } else {
            ASTArgumentList argumentList = (ASTArgumentList) arguments.jjtGetChild(0);
            for (int i = 0; i < argumentList.jjtGetNumChildren(); i++) {
                Node expressionChild = argumentList.jjtGetChild(i).jjtGetChild(0);
                if (!(expressionChild instanceof ASTPrimaryExpression) || expressionChild.jjtGetNumChildren() != 1) {
                    // The arguments are not simply passed through
                    return super.visit(node, data);
                }

                ASTPrimaryExpression argumentPrimaryExpression = (ASTPrimaryExpression) expressionChild;
                ASTPrimaryPrefix argumentPrimaryPrefix = (ASTPrimaryPrefix) argumentPrimaryExpression.jjtGetChild(0);
                if (argumentPrimaryPrefix.jjtGetNumChildren() == 0) {
                    // The arguments are not simply passed through (using "this" for instance)
                    return super.visit(node, data);
                }
                Node argumentPrimaryPrefixChild = argumentPrimaryPrefix.jjtGetChild(0);
                if (!(argumentPrimaryPrefixChild instanceof ASTName)) {

                    return super.visit(node, data);
                }

                if (formalParameters.jjtGetNumChildren() < i + 1) {
                    return super.visit(node, data); // different number of args
                }

                ASTName argumentName = (ASTName) argumentPrimaryPrefixChild;
                ASTFormalParameter formalParameter = (ASTFormalParameter) formalParameters.jjtGetChild(i);
                ASTVariableDeclaratorId variableId = findFirstDegreeChildrenOfType(formalParameter,
                        ASTVariableDeclaratorId.class).get(0);
                if (!argumentName.hasImageEqualTo(variableId.getImage())) {
                    return super.visit(node, data);
                }

            }
            // All arguments are passed through directly
            addViolation(data, node, getMessage());
        }
        return super.visit(node, data);
    }

    public <T> List<T> findFirstDegreeChildrenOfType(Node n, Class<T> targetType) {
        List<T> l = new ArrayList<>();
        lclFindChildrenOfType(n, targetType, l);
        return l;
    }

    private <T> void lclFindChildrenOfType(Node node, Class<T> targetType, List<T> results) {
        if (node.getClass().equals(targetType)) {
            results.add((T) node);
        }

        if (node instanceof ASTClassOrInterfaceDeclaration && ((ASTClassOrInterfaceDeclaration) node).isNested()) {
            return;
        }

        if (node instanceof ASTClassOrInterfaceBodyDeclaration
                && ((ASTClassOrInterfaceBodyDeclaration) node).isAnonymousInnerClass()) {
            return;
        }

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child.getClass().equals(targetType)) {
                results.add((T) child);
            }
        }
    }
}