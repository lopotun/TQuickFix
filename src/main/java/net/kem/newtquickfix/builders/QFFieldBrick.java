package net.kem.newtquickfix.builders;

import org.apache.commons.lang3.StringUtils;
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

    @Override
    protected void getImportSectionPart(StringBuilder sb) {
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_FIELDS).append('.').append(name).append(";\n");
    }

    // private FieldStringA fieldStringA;
    @Override
    protected void addDeclaration() {
        if(useFQDN) {
            sb.append(ident).append("\tprivate ").append(BuilderUtils.PACKAGE_NAME_FIELDS).append('.').append(name).append(' ').append(StringUtils.uncapitalize(name)).append(";\n");
        } else {
            super.addDeclaration();
        }
    }

    /*
    public FieldStringA getFieldStringA() {
        return fieldStringA;
    }
     */
    @Override
    protected void addGetter() {
        if(useFQDN) {
            sb.append(ident).append("\tpublic ").append(BuilderUtils.PACKAGE_NAME_FIELDS).append('.').append(name).append(" get").append(name).append("() {\n")
                    .append(ident).append("\t\treturn ").append(StringUtils.uncapitalize(name)).append(";\n").append(ident).append("\t}\n");
        } else {
            super.addGetter();
        }
    }

    /*
    public void setFieldStringA(FieldStringA fieldStringA) {
        this.fieldStringA = fieldStringA;
    }
     */
    @Override
    protected void addSetter() {
        if(useFQDN) {
            sb.append(ident).append("\tpublic void set").append(name).append("(").append(BuilderUtils.PACKAGE_NAME_FIELDS).append('.').append(name).append(' ').append(StringUtils.uncapitalize(name)).append(") {\n")
                    .append(ident).append("\t\tthis.").append(StringUtils.uncapitalize(name)).append(" = ").append(StringUtils.uncapitalize(name)).append(";\n").append(ident).append("\t}\n");
        } else {
            super.addSetter();
        }
    }
}
