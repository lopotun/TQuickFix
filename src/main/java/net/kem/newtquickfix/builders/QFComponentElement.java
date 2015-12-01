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
                    QFRequirable member = BuilderUtils.getQFRequirable((Element) value, sb, ident);
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
        getCreditsSection();
        getClassTitle();
        getMembers();
        getConstructor();
        getMethodGetInstance();
        getMethodToFIXString();
        sb.append('}'); // end of class
    }

    public String getFirstFiledName() {
        QFRequirable firstMember = members.get(0);
        if (firstMember.getTagType() == QFMember.Type.FIELD) {
            return firstMember.getName();
        }
        //TODO Implement
//        String blockName = "net.kem.newtquickfix.components." + name + "." + firstMember.getName();
//        Class<QFComponentElement> clazz = Class.forName(blockName);
        return firstMember.getFirstFiledName();
    }

    private void getPackageSection() {
        sb.append("package net.kem.newtquickfix.components;\n\n");
    }

    private void getImportSection() {
        sb.append("import net.kem.newtquickfix.blocks.QFComponent;\n");
        sb.append("import net.kem.newtquickfix.blocks.QFField;\n");
//        sb.append("import net.kem.newtquickfix.blocks.QFGroupDef;\n");
        sb.append("import net.kem.newtquickfix.blocks.QFMember;\n");
        sb.append("import net.kem.newtquickfix.fields.*;\n\n");
        for (QFRequirable member : members) {
//            if (member.getTagType() == QFMember.Type.FIELD) {
//                sb.append("import net.kem.newtquickfix.fields.").append(member.getName()).append(";\n");
//            }
            member.getImportSectionPart(sb);
        }

//        sb.append("import java.util.List;\n");
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

    protected void getConstructor() {
        sb.append(ident).append("\tprivate ").append(name).append("() {}\n\n");
    }


    // public static ComponentMain getInstance(Stack<QFField> tags, ComponentMain instance) {
    //  return getInstance(tags, instance, ComponentMain.class);
    // }
    protected void getMethodGetInstance() {
        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance(Stack<QFField> tags, ").append(name).append(" instance) {\n")
                .append(ident).append("\t\treturn getInstance(tags, instance, ").append(name).append(".class);\n")
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
            String memberClassName = member.getName();
            String memberVarName = StringUtils.uncapitalize(memberClassName);
            sb.append(ident).append("\t\tif(").append(memberVarName).append(" != null) {\n");
            member.getMethodToFIXStringPart(sb);
            sb.append(ident).append("\t\t}\n");
        }
        sb.append(ident).append("\t}\n");
    }
}