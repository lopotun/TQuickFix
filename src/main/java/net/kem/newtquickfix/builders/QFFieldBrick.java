package net.kem.newtquickfix.builders;

import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/24/2015 at 2:41 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 *
 * @QFMember(type = QFMember.Type.FIELD)
 * private FieldStringA fieldStringA;
 * public FieldStringA getFieldStringA() {
 * return fieldStringA;
 * }
 * public void setFieldStringA(FieldStringA fieldStringA) {
 * this.fieldStringA = fieldStringA;
 * }
 */
public class QFFieldBrick extends QFRequirable {
    protected QFFieldBrick(Element startElement, StringBuilder sb, CharSequence ident) throws IllegalArgumentException {
        super(startElement, sb, ident);
    }

    // @QFMember(type = QFMember.Type.FIELD)
    @Override
    protected void addAnnotation() {
        sb.append(ident).append("\t@QFMember(type = QFMember.Type.FIELD)\n");
    }
}
