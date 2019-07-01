package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.util.NumericConstants;

public class ImportNumberRule extends ExcessiveNodeCountRule {

    public ImportNumberRule() {
        super(ASTCompilationUnit.class);
        setProperty(MINIMUM_DESCRIPTOR, 1d);
    }

    /**
     * Hook method to count imports. This is a user defined value.
     *
     * @param node
     * @param data
     * @return Object
     */
    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        return NumericConstants.ONE;
    }
}
