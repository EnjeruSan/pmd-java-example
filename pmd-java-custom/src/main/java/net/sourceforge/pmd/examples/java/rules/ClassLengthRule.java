package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.stat.DataPoint;

public class ClassLengthRule extends ExcessiveLengthRule {
    public ClassLengthRule() {
        super(ASTAnyTypeDeclaration.class);
        setProperty(MINIMUM_DESCRIPTOR, 1d);
    }

    @Override
    public Object visit(JavaNode node, Object data) {
        if (nodeClass.isInstance(node)) {
            DataPoint point = new DataPoint();
            point.setNode(node);
            point.setScore(1.0 * (node.getEndLine() - node.getBeginLine()));
            point.setMessage(getMessage());
            addDataPoint(point);
            Double result = point.getScore();

            if (result > 0) {
                addViolation(data, node, result.toString());
            }
        }

        return node.childrenAccept(this, data);
    }
}