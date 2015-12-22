package net.kem.newtquickfix.builders;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 *
     @QFMember(type = QFMember.Type.GROUP, groupClass = ComponentMain.GroupA.class)
     private List<ComponentMain.GroupA> groupA;
     public List<ComponentMain.GroupA> getGroupA() {
     return groupA;
     }
     public void setGroupA(List<ComponentMain.GroupA> groupA) {
     this.groupA = groupA;
     }
 */
public class QFGroupBrick extends QFRequirable {
    protected QFGroupBrick(Element startElement, StringBuilder sb, CharSequence ident) throws IllegalArgumentException {
        super(startElement, sb, ident);
    }

    // for (NoAffectedOrders item: noAffectedOrders) {
    //  item.toFIXString(sb);
    // }
    @Override
    protected void getMethodToFIXStringPart(StringBuilder sb) {
        String memberVarName = StringUtils.uncapitalize(name);
        sb.append(ident).append("\t\t\tfor (").append(name).append(" item: ").append(memberVarName).append(") {\n");
        sb.append(ident).append("\t\t\t\titem.toFIXString(sb);\n");
        sb.append(ident).append("\t\t\t}\n");
    }

    @Override
    protected void getImportSectionPart(StringBuilder sb) {
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFGroupDef;\n").append("import java.util.List;\n");
    }

    public void toJavaSource() {
        addDefinition();
        super.toJavaSource();
    }

    /*
    @QFGroupDef(count = FieldIntegerGroupCount.TAG, delimiter = FieldStringGroupDelimiter.TAG)
    public static class GroupA extends QFComponent {
        private static final int $GROUP_COUNT;
        static {
            QFGroupDef groupAnnotation = GroupA.class.getAnnotation(QFGroupDef.class);
            $GROUP_COUNT = groupAnnotation.count();
        }
        @QFMember
        private FieldStringGroupDelimiter fieldStringGroupDelimiter;
        public FieldStringGroupDelimiter getFieldStringGroupDelimiter() {
            return fieldStringGroupDelimiter;
        }
        public void setFieldStringGroupDelimiter(FieldStringGroupDelimiter fieldStringGroupDelimiter) {
            this.fieldStringGroupDelimiter = fieldStringGroupDelimiter;
        }

        // ... group members

        private GroupA() {
        }

        public static GroupA getInstance(Stack<QFField> tags, GroupA instance) {
            return getInstance(tags, instance, GroupA.class);
        }

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
    }
     */
    private void addDefinition() {
        QFGroupElement groupBlock = new QFGroupElement(startElement, sb);
        groupBlock.toJavaSource();
    }

    // @QFMember(type = QFMember.Type.GROUP, groupClass = ComponentMain.GroupA.class)
    @Override
    protected void addAnnotation() {
        sb.append(ident).append("\t@QFMember(type = QFMember.Type.GROUP, groupClass = ").append(name).append(".class)\n");
    }

    // private List<ComponentMain.GroupA> groupA;
    protected void addDeclaration() {
        sb.append(ident).append("\tprivate List<").append(name).append("> ").append(StringUtils.uncapitalize(name)).append(";\n");
    }

    /*
    public List<ComponentMain.GroupA> getGroupA() {
        return groupA;
    }
     */
    protected void addGetter() {
        sb.append(ident).append("\tpublic List<").append(name).append("> get").append(name).append("() {\n")
                .append("\t\treturn ").append(StringUtils.uncapitalize(name)).append(";\n\t}\n");
    }

    /*
    public void setGroupA(List<ComponentMain.GroupA> groupA) {
        this.groupA = groupA;
    }
     */
    protected void addSetter() {
        sb.append(ident).append("\tpublic void set").append(name).append("(List<").append(name).append("> ").append(StringUtils.uncapitalize(name)).append(") {\n")
                .append("\t\tthis.").append(StringUtils.uncapitalize(name)).append(" = ").append(StringUtils.uncapitalize(name)).append(";\n\t}\n");
    }
}