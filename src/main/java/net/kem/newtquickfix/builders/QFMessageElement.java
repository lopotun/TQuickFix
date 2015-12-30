package net.kem.newtquickfix.builders;

import net.kem.newtquickfix.blocks.QFMember;
import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 12/22/2015 at 9:33 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFMessageElement extends QFComponentElement {
    public QFMessageElement(Element startElement, StringBuilder sb, CharSequence ident) throws IllegalArgumentException {
        super(startElement, sb, ident);
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
                .append("public class ").append(name).append(" extends QFMessage ");
        generateImplementsSection();
        sb.append("{\n");
    }

    protected void getImportSection() {
        super.getImportSection();
    }

    protected void getImportSectionFromSubElements() {
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFMessage;\n");
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_FIELDS).append(".BeginString;\n");
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

    @Override
    protected void getCustomMethods() {
        super.getCustomMethods();
        // //	---- Message-specific methods BEGIN
        // private static final ValidationErrorsHandler VALIDATION_HANDLER = QFUtils.getMessageValidationErrorsHandler(AllocationReportAck.class);
        // public MsgType getMsgType() {
        //    return standardHeader.getMsgType();
        // }
        sb.append(ident).append("\t//\t---- Message-specific methods BEGIN\n");
        sb.append(ident).append("\tpublic MsgType getMsgType() {\n")
                .append(ident).append("\t\treturn standardHeader.getMsgType();\n")
                .append(ident).append("\t}\n\n");

        // //	---- Message-specific methods END
        sb.append(ident).append("\t//\t---- Message-specific methods END\n\n");
    }

    protected void getConstructor() {
        /*
        private AllocationReportAck() {
		    this(QFComponentValidator.getDefaultComponentValidator());
        }

        private AllocationReportAck(QFComponentValidator componentValidator) {
            standardHeader = StandardHeader.getInstance();
            standardHeader.setBeginString(BeginString.getInstance("", componentValidator));
            standardHeader.setMsgType(MsgType.getInstance("AT", componentValidator));

            standardTrailer = StandardTrailer.getInstance();
        }
         */
        sb.append(ident).append("\tprivate ").append(name).append("() {\n")
                .append("\t\tthis(QFComponentValidator.getDefaultComponentValidator());\n")
                .append("\t}\n");
        sb.append(ident).append("\tprivate ").append(name).append("(QFComponentValidator componentValidator) {\n")
                .append("\t\tstandardHeader = StandardHeader.getInstance();\n")
                .append("\t\tstandardHeader.setBeginString(BeginString.getInstance(\"").append(BuilderUtils.FIX_VERSION).append("\", componentValidator));\n")
                .append("\t\tstandardHeader.setMsgType(MsgType.getInstance(\"").append(startElement.getAttribute("msgtype")).append("\", componentValidator));\n\n")
                .append("\t\tstandardTrailer = StandardTrailer.getInstance();\n")
                .append("\t}\n\n");
    }

    protected void getMethodGetInstance() {
        // public static ComponentMain getInstance() {
        //  return new ComponentMain();
        // }
        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance() {\n")
                .append(ident).append("\t\treturn new ").append(name).append("();\n")
                .append(ident).append("\t}\n\n");

        // public static ComponentMain getInstance(Stack<QFField> tags, QFComponentValidator componentValidator) {
        //  return tags==null? new ComponentMain(): getInstance(tags, null, ComponentMain.class, QFComponentValidator componentValidator);
        // }
        /*
        public static AllocationReportAck getInstance(QFComponentValidator componentValidator) {
            return new AllocationReportAck(componentValidator);
        }

        public static AllocationReportAck getInstance(Stack<QFField> tags, QFComponentValidator componentValidator) {
            return tags==null? new AllocationReportAck(componentValidator): getInstance(tags, null, AllocationReportAck.class, componentValidator);
        }
         */
        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance(QFComponentValidator componentValidator").append(") {\n")
                .append(ident).append("\t\treturn new ").append(name).append("(componentValidator);\n")
                .append(ident).append("\t}\n\n");

        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance(Stack<QFField> tags, QFComponentValidator componentValidator").append(") {\n")
                .append(ident).append("\t\treturn tags==null? new ").append(name).append("(componentValidator): getInstance(tags, null, ").append(name).append(".class, componentValidator);\n")
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
