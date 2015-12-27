package net.kem.newtquickfix.builders;

import net.kem.newtquickfix.blocks.QFMember;
import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFGroupElement extends QFComponentElement {
    private QFElement parent;
    protected QFGroupElement(Element startElement, StringBuilder sb, QFElement parent) throws IllegalArgumentException {
        super(startElement, sb, "\t");
        this.type = QFMember.Type.GROUP;
        this.parent = parent;

        // In group, the first field is used as group element delimiter. So, it must be present and valid.
        String delimiterTagName = getFirstFiledName();
        for (QFRequirable member : members) {
            if(delimiterTagName.endsWith(member.getName())) {
                member.required = true;
                break;
            }
        }
    }

    /*
    @QFGroupDef(count = net.kem.newtquickfix.fields.GroupA.TAG, delimiter = FieldStringGroupDelimiter.TAG)
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
    @Override
    public void toJavaSource() {
//        getPackageSection();
//        getImportSection();
//        getCreditsSection();
        getClassTitle();
        getStaticSection();
        getMembers();
        getCustomMethods();
        getConstructor();
        getMethodGetInstance();
        getMethodToFIXString();
        sb.append(ident).append("}\n"); // end of class
    }

    protected void getImportSection() {
        for (QFRequirable member : members) {
            if (member.getTagType() == QFMember.Type.FIELD) {
                if(member.getName().equals(parent.getName())) {
                    // In some cases (e.g. in net.kem.newtquickfix.components.RateSource) we have class member (of type QFField)
                    // that has the same name as Component class. In this case we have to use full-specified name for this class member.
                    member.useFQDN(true);
                } else {
                    member.getImportSectionPart(sb);
                }
            }
        }
        sb.append('\n');
    }

    // @QFGroupDef(count = net.kem.newtquickfix.fields.GroupA.TAG, delimiter = FieldStringGroupDelimiter.TAG)
    // public static class GroupA extends QFComponent {
    @Override
    protected void getClassTitle() {
        sb.append(ident).append("@QFGroupDef(count = ").append(BuilderUtils.PACKAGE_NAME_FIELDS).append('.').append(name).append(".TAG, delimiter = ").append(getFirstFiledName()).append(".TAG)\n")
                .append(ident).append("@SuppressWarnings(\"unused\")\n")
                .append(ident).append("public static class ").append(name).append(" extends QFComponent {\n");
    }

    // private static final int $GROUP_COUNT;
    // static {
    //     QFGroupDef groupAnnotation = GroupA.class.getAnnotation(QFGroupDef.class);
    //     $GROUP_COUNT = groupAnnotation.count();
    // }
    protected void getStaticSection() {
        sb.append(ident).append("\tprivate static final int $GROUP_COUNT;\n")
                .append(ident).append("\tstatic {\n")
                .append(ident).append("\t\tQFGroupDef groupAnnotation = ").append(name).append(".class.getAnnotation(QFGroupDef.class);\n")
                .append(ident).append("\t\t$GROUP_COUNT = groupAnnotation.count();\n")
                .append(ident).append("\t}\n\n");
    }
}