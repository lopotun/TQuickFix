package net.kem.newtquickfix.builders;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFRequirable extends QFElement {
    protected boolean required;

    /*
    <group name="NoMDEntries" required="Y">
    <field name="MDEntryType" required="Y"/>
    <component name="YieldData" required="N"/>
     */
    protected QFRequirable(Element startElement, StringBuilder sb, CharSequence ident) throws IllegalArgumentException {
        super(startElement, sb, ident);
        String attribute = startElement.getAttribute("required");
        this.required = attribute != null && attribute.equalsIgnoreCase("Y");
    }

    public boolean isRequired() {
        return required;
    }

    protected String getFirstFiledName() {
        return name;
    }

    protected void getMethodToFIXStringPart(StringBuilder sb) {
        String memberVarName = StringUtils.uncapitalize(name);
        sb.append(ident).append("\t\t\t").append(memberVarName).append(".toFIXString(sb);\n");
    }

    protected void getImportSectionPart(StringBuilder sb) {}

    /*
    @QFMember(type = QFMember.Type.FIELD)
    private FieldStringA fieldStringA;
    public FieldStringA getFieldStringA() {
        return fieldStringA;
    }
    public void setFieldStringA(FieldStringA fieldStringA) {
        this.fieldStringA = fieldStringA;
    }
    */
    public void toJavaSource() {
        addAnnotation();
        addDeclaration();
        addGetter();
        addSetter();
        sb.append('\n'); // end of member definition
    }

    protected abstract void addAnnotation();

    // private FieldStringA fieldStringA;
    protected void addDeclaration() {
        sb.append(ident).append("\tprivate ").append(name).append(' ').append(StringUtils.uncapitalize(name)).append(";\n");
    }

    /*
    public FieldStringA getFieldStringA() {
        return fieldStringA;
    }
     */
    protected void addGetter() {
        sb.append(ident).append("\tpublic ").append(name).append(" get").append(name).append("() {\n")
                .append(ident).append("\t\treturn ").append(StringUtils.uncapitalize(name)).append(";\n").append(ident).append("\t}\n");
    }

    /*
    public void setFieldStringA(FieldStringA fieldStringA) {
        this.fieldStringA = fieldStringA;
    }
     */
    protected void addSetter() {
        sb.append(ident).append("\tpublic void set").append(name).append("(").append(name).append(' ').append(StringUtils.uncapitalize(name)).append(") {\n")
                .append(ident).append("\t\tthis.").append(StringUtils.uncapitalize(name)).append(" = ").append(StringUtils.uncapitalize(name)).append(";\n").append(ident).append("\t}\n");
    }
}