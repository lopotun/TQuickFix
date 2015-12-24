package net.kem.newtquickfix.builders;

import net.kem.newtquickfix.blocks.QFMember;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFComponentElement extends QFElement {
    protected List<QFRequirable> members;

    public QFComponentElement(Element startElement, StringBuilder sb, CharSequence ident) throws IllegalArgumentException {
        super(startElement, sb, ident);
        NodeList values = startElement.getChildNodes();
        if (values != null && values.getLength() > 0) {
            members = new LinkedList<>();
            for (int j = 0; j < values.getLength(); j++) {
                Node value = values.item(j);
                if (value instanceof Element) {
                    QFRequirable member = BuilderUtils.getQFRequirable((Element) value, sb, ident, this);
                    if (member != null) {
                        members.add(member);
                    }
                }
            }
        }
    }

    @Override
    public void toJavaSource() {
        getPackageSection();
        getImportSection();
        generateCreditsSection();
        getClassTitle();
        getMembers();
        getCustomMethods();
        getConstructor();
        getMethodGetInstance();
        getMethodToFIXString();
        sb.append('}'); // end of class
    }

    public String getFirstFiledName() {
        QFRequirable firstMember = members.get(0);
        if (firstMember.getTagType() == QFMember.Type.FIELD) {
            return BuilderUtils.PACKAGE_NAME_FIELDS + "." + firstMember.getName();
        }
        CharSequence fieldName = BuilderUtils.COMPONENTS_FIRST_FIELD.get(firstMember.name);
        return BuilderUtils.PACKAGE_NAME_FIELDS + "." + fieldName.toString();
    }

    protected void getPackageSection() {
        sb.append("package ").append(BuilderUtils.PACKAGE_NAME_COMPONENTS).append(";\n\n");
    }

    protected void getImportSection() {
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFComponent;\n");
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFField;\n");
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFMember;\n");
        for (QFRequirable member : members) {
            // In some cases (e.g. in net.kem.newtquickfix.components.RateSource) we have class member (of type QFField)
            // that has the same name as Component class. In this case we have to use full-specified name for this class member.
            if(member.getName().equals(name)) {
                member.useFQDN(true);
            } else {
                member.getImportSectionPart(sb);
            }
        }

        sb.append("import java.util.Stack;\n");
        sb.append('\n');
    }

    /*
        @SuppressWarnings("unused")
        public class ComponentMain extends QFComponent {
    */
    protected void getClassTitle() {
        sb.append("\t@SuppressWarnings(\"unused\")\n")
                .append("public class ").append(name).append(" extends QFComponent {\n");
    }

    /*
    @QFMember(type = QFMember.Type.FIELD)
    private FieldStringGroupDelimiter fieldStringGroupDelimiter;
    public FieldStringGroupDelimiter getFieldStringGroupDelimiter() {
        return fieldStringGroupDelimiter;
    }
    public void setFieldStringGroupDelimiter(FieldStringGroupDelimiter fieldStringGroupDelimiter) {
        this.fieldStringGroupDelimiter = fieldStringGroupDelimiter;
    }
     */
    protected void getMembers() {
        for (QFRequirable member : members) {
            member.ident = ident;
            member.toJavaSource();
        }
    }

    protected void getCustomMethods() {}

    protected void getConstructor() {
        sb.append(ident).append("\tprivate ").append(name).append("() {}\n\n");
    }


    protected void getMethodGetInstance() {
        // public static ComponentMain getInstance() {
        //  return new ComponentMain();
        // }
        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance() {\n")
                .append(ident).append("\t\treturn new ").append(name).append("();\n")
                .append(ident).append("\t}\n\n");

        // public static ComponentMain getInstance(Stack<QFField> tags) {
        //  return tags==null? new ComponentMain(): getInstance(tags, null, ComponentMain.class);
        // }
        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance(Stack<QFField> tags").append(") {\n")
                .append(ident).append("\t\treturn tags==null? new ").append(name).append("(): getInstance(tags, null, ").append(name).append(".class);\n")
                .append(ident).append("\t}\n\n");
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
        sb.append(ident).append("\t@Override\n")
          .append(ident).append("\tpublic void toFIXString(StringBuilder sb) {\n");
        for (QFRequirable member : members) {
            hookMethodToFIXStringMemberPre(member);
            String memberClassName = member.getName();
            String memberVarName = StringUtils.uncapitalize(memberClassName);
            sb.append(ident).append("\t\tif(").append(memberVarName).append(" != null) {\n");
            member.getMethodToFIXStringPart(sb);
            sb.append(ident).append("\t\t}\n");
        }
        sb.append(ident).append("\t}\n");
    }

    protected void hookMethodToFIXStringMemberPre(QFRequirable member) {
    }
}