/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.examples.java.rules;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractStatisticalJavaRule;
import net.sourceforge.pmd.stat.DataPoint;

public class ExcessiveLengthRule extends AbstractStatisticalJavaRule {
    protected Class<?> nodeClass;

    public ExcessiveLengthRule(Class<?> nodeClass) {
        this.nodeClass = nodeClass;
    }

    @Override
    public Object visit(JavaNode node, Object data) {
        if (nodeClass.isInstance(node)) {
            DataPoint point = new DataPoint();
            point.setNode(node);
            point.setScore(1.0 * (node.getEndLine() - node.getBeginLine()));
            point.setMessage(getMessage());
            addDataPoint(point);
        }

        return node.childrenAccept(this, data);
    }
}
