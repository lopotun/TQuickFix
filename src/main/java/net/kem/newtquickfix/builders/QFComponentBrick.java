package net.kem.newtquickfix.builders;

import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/24/2015 at 2:41 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 *
 * @QFMember(type = QFMember.Type.COMPONENT)
 * private ComponentC componentC;
 * public ComponentC getComponentC() {
 * return componentC;
 * }
 * public void setComponentC(ComponentC componentC) {
 * this.componentC = componentC;
 * }
 */
public class QFComponentBrick extends QFRequirable {
    protected QFComponentBrick(Element startElement, StringBuilder sb, CharSequence ident) throws IllegalArgumentException {
        super(startElement, sb, ident);
    }

    // @QFMember(type = QFMember.Type.COMPONENT)
    @Override
    protected void addAnnotation() {
        sb.append(ident).append("\t@QFMember(type = QFMember.Type.COMPONENT)\n");
    }

    @Override
    protected void getImportSectionPart(StringBuilder sb) {
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_COMPONENTS).append('.').append(name).append(";\n");
    }
}
