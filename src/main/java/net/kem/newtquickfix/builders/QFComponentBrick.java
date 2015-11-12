package net.kem.newtquickfix.builders;

import net.kem.newtquickfix.blocks.QFMember;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFComponentBrick extends QFMemberBrick {
    protected QFComponentBrick(Element startElement, StringBuilder sb) throws IllegalArgumentException {
        super(startElement, sb);
    }

    @Override
    public void toJavaSource() {
        getPackageSection();
        getImportSection();
        getCreditsSection();
        getClassTitle();
        getMembers();
        getConstructor();

        getMethodToFIXString();
        sb.append('}'); // end of class
    }

    // @QFMember(type = QFMember.Type.COMPONENT)
    @Override
    public void getMemberAnnotation() {
        sb.append("@QFMember(type = QFMember.Type.COMPONENT)\n");
    }

    @Override
    public String getFirstFiledName() {
        QFBrick firstMember = members.get(0);
        if (firstMember.getFIXType() == QFMember.Type.FIELD) {
            return firstMember.getName();
        }
        return firstMember.getFirstFiledName();
    }

    private void getPackageSection() {
        sb.append("package net.kem.newtquickfix.components;\n\n");
    }

    private void getImportSection() {
        sb.append("import net.kem.newtquickfix.blocks.QFComponent;\n\n");
        for (QFBrick member : members) {
            if(member.getFIXType() == QFMember.Type.FIELD) {
                sb.append("import net.kem.newtquickfix.fields.").append(member.getName()).append(";\n");
            }
        }
        sb.append('\n');
    }


    /*
    @Override
    public void toFIXString(StringBuilder sb) {
        if(fieldStringGroupDelimiter != null) {
            fieldStringGroupDelimiter.toFIXString(sb);
        }
        if(fieldStringA != null) {
            fieldStringA.toFIXString(sb);
        }
        if(componentC != null) {
            componentC.toFIXString(sb);
        }
        if(fieldIntegerA != null) {
            fieldIntegerA.toFIXString(sb);
        }
    }
    */
    protected void getMethodToFIXString() {
        sb.append("\t@Override\n\tpublic void toFIXString(StringBuilder sb) {");
        for (QFBrick member : members) {
            String memberClassName = member.getName();
            String memberVarName = StringUtils.uncapitalize(memberClassName);
            sb.append("\t\tif(").append(memberVarName).append(" != null) {\n")
                    .append("\t\t\t").append(memberVarName).append(".toFIXString(sb);\n\t\t}\n");
        }
        sb.append("\t}\n");
    }


    /*
    	@SuppressWarnings("unused")
        public class ComponentMain extends QFComponent {
    */
    @Override
    protected void getClassTitle() {
        sb.append("\t@SuppressWarnings(\"unused\")\n")
                .append("public class ").append(name).append(" extends QFComponent {\n");
    }
}