package net.kem.newtquickfix.builders;

import net.kem.newtquickfix.blocks.QFMember;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFGroupBrick extends QFMemberBrick {
    protected QFGroupBrick(Element startElement, StringBuilder sb) throws IllegalArgumentException {
        super(startElement, sb);
    }

    @Override
    public void toJavaSource() {
        getClassTitle();
        getMembers();
        getConstructor();
        getMethodGetInstance();
        getMethodToFIXString();
        sb.append('}'); // end of class
    }

    public String getFirstFiledName() {
        QFBrick firstMember = members.get(0);
        if (firstMember.getFIXType() == QFMember.Type.FIELD) {
            return firstMember.getName();
        }
        return firstMember.getFirstFiledName();
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

    /*	@QFGroupDef(count = FieldIntegerGroupCount.TAG, delimiter = FieldStringGroupDelimiter.TAG)
    public static class GroupA extends QFComponent {
    private static final int $GROUP_COUNT;
        static {
            QFGroupDef groupAnnotation = GroupA.class.getAnnotation(QFGroupDef.class);
            $GROUP_COUNT = groupAnnotation.count();
        }
    */
    @Override
    protected void getClassTitle() {
        sb.append("\t@QFGroupDef(count = ")
                .append(name) //group count
                .append(".TAG, delimiter = ")
                .append(getFirstFiledName()) //group delimiter
                .append(".TAG)\n")
                .append("\tpublic static class ").append(name).append(" extends QFComponent {\n")
                .append("\tprivate static final int $GROUP_COUNT;\n")
                .append("\t\tstatic {\n")
                .append("\t\t\tQFGroupDef groupAnnotation = ").append(name).append(".class.getAnnotation(QFGroupDef.class);\n")
                .append("\t\t\t$GROUP_COUNT = groupAnnotation.count();\n")
                .append("\t\t{\n");
    }

    // @QFMember(type = QFMember.Type.GROUP, groupClass = ComponentMain.GroupA.class)
    public void getMemberAnnotation() {
        sb.append("@QFMember(type = QFMember.Type.GROUP, groupClass = ").append(name).append(".class)\n");
    }
}