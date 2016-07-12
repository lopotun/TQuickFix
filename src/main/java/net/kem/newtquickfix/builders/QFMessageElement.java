package net.kem.newtquickfix.builders;

import net.kem.newtquickfix.blocks.QFMember;
import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 12/22/2015 at 9:33 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFMessageElement extends QFComponentElement {
    protected String messageCategory;
    public QFMessageElement(Element startElement, StringBuilder sb, CharSequence ident) throws IllegalArgumentException {
        super(startElement, sb, ident);
        this.messageCategory = startElement.getAttribute("msgcat");
    }

    public void addHeader(Element header) {
        addHeaderTrailer(header, true);
    }

    public void addTrailer(Element trailer) {
        addHeaderTrailer(trailer, false);
    }

    private void addHeaderTrailer(Element headerOrTrailer, boolean isHeader) {
        QFRequirable member = BuilderUtils.getQFRequirable(headerOrTrailer, sb, ident, this);
        if (member != null) {
            if (isHeader) {
                members.add(0, member);
            } else {
                members.add(member);
            }
        }
    }

    protected void getPackageSection() {
        sb.append("package ").append(BuilderUtils.PACKAGE_NAME_MESSAGES).append(";\n\n");
    }

    /*
        @SuppressWarnings("unused")
        public class ComponentMain extends QFComponent {
    */
    protected void getClassTitle() {
        sb.append("\t@SuppressWarnings(\"unused\")\n")
                .append("public class ").append(name).append(" extends AMessage ");
        generateImplementsSection();
        sb.append("{\n");
    }

    protected void getImportSectionFromSubElements() {
        super.getImportSectionFromSubElements(); // We still need to import QFComponent since a Message can directly contain a Group in FIX v40.
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_FIELDS).append(".MsgType;\n");
    }

    private void generateImplementsSection() {
        boolean componentChildFound = false;
        for (QFRequirable member : members) {
            if (member.type == QFMember.Type.COMPONENT) {
                if (!componentChildFound) {
                    sb.append("implements ");
                    componentChildFound = true;
                }
                sb.append("I").append(member.name).append(", ");
            }
        }
        if (componentChildFound) {
            sb.setLength(sb.length() - 2); // Get rid of the last ", ".
            sb.append(" ");
        }
    }

    protected void getValidateMethodNoParams() {
    }

    protected void preValidate() {
        // Boolean valid = super.validate(componentValidator);
        sb.append(ident).append("\t\tBoolean valid = super.validate(componentValidator);\n");
    }

    protected boolean shouldIncludeMember(QFRequirable member) {
        return !(member.getName().equals("StandardHeader") || member.getName().equals("StandardTrailer"));
    }

    @Override
    protected void getCustomMethods() {
        // //	---- Message-specific methods BEGIN
        /*
        public static MsgType getMsgType() {
            return MsgType.getInstance("BL", null);
        }

        @Override
        public MsgType getMessageType() {
            return getMsgType();
        }
        */
        sb.append(ident).append("\t//\t---- Message-specific methods BEGIN\n");
        sb.append(ident).append("\tpublic static MsgType getMsgType() {\n")
                .append(ident).append("\t\treturn MsgType.getInstance(\"").append(startElement.getAttribute("msgtype")).append("\", null);\n")
                .append(ident).append("\t}\n\n");

        sb.append(ident).append("\t@Override\n")
                .append(ident).append("\tpublic MsgType getMessageType() {\n")
                .append(ident).append("\t\treturn getMsgType();\n")
                .append(ident).append("\t}\n\n");
        sb.append(ident).append("\t//\t---- Message-specific methods END\n\n");
    }

    protected void getConstructor() {
        /*
        private AllocationReportAck() {
//		    this(null);
        }

//        private AllocationReportAck(QFComponentValidator componentValidator) {
//            setComponentValidator(componentValidator);
//        }
         */
        sb.append(ident).append("\tprivate ").append(name).append("() {\n")
//                .append("\t\tthis(null);\n")
                .append("\t}\n");
//        sb.append(ident).append("\tprivate ").append(name).append("(QFComponentValidator componentValidator) {\n")
//                .append("\t\tsetComponentValidator(componentValidator);\n")
//                .append("\t}\n\n");
    }

    protected void getMethodGetInstance() {
        // public static ComponentMain getInstance() {
        //  return new ComponentMain();
        // }
        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance() {\n")
                .append(ident).append("\t\treturn new ").append(name).append("();\n")
                .append(ident).append("\t}\n\n");

        /*
//        public static AllocationReportAck getInstance(QFComponentValidator componentValidator) {
//            return new AllocationReportAck(componentValidator);
//        }

        public static AllocationReportAck getInstance(Stack<QFField> tags, QFComponentValidator componentValidator) {
            return tags==null? new AllocationReportAck(componentValidator): getInstance(tags, null, AllocationReportAck.class, componentValidator);
        }
         */
//        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance(QFComponentValidator componentValidator) {\n")
//                .append(ident).append("\t\treturn new ").append(name).append("(componentValidator);\n")
//                .append(ident).append("\t}\n\n");

        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance(Stack<QFField> tags, QFComponentValidator componentValidator) {\n")
                .append(ident).append("\t\treturn tags==null? new ").append(name).append("(): getInstance(tags, null, ").append(name).append(".class, componentValidator);\n")
                .append(ident).append("\t}\n\n");
    }

    /**
     * This method puts the following lines:
     * <p>
     * // Show unknown tag(s) if any.
     * super.toFIXString(sb);
     * <p>
     * before the StandardTrailer component in {@link #getMethodToFIXString()} method.
     *
     * @param member .
     */
    protected void hookMethodToFIXStringMemberPre(QFRequirable member) {
        if (member.getName().equals("StandardTrailer")) {
            sb.append(ident).append("\t\t// Show unknown tag(s) if any.\n")
                    .append("\t\tsuper.toFIXString(sb);\n\n");
        }
    }
}
