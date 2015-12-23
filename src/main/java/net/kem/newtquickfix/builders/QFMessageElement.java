package net.kem.newtquickfix.builders;

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
            if(isHeader) {
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
                .append("public class ").append(name).append(" extends QFMessage {\n");
    }

    protected void getImportSection() {
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFMessage;\n");
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFField;\n");
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFMember;\n");
        for (QFRequirable member : members) {
            member.getImportSectionPart(sb);
        }
        sb.append("import java.util.Stack;\n");
        sb.append('\n');
    }

    /**
     * This method puts the following lines:
     *
     * // Show unknown ta(s) if any.
     * super.toFIXString(sb);
     *
     * before the StandardTrailer component in {@link #getMethodToFIXString()} method.
     * @param member    .
     */
    protected void hookMethodToFIXString(QFRequirable member) {
        if(member.getName().equals("StandardTrailer")) {
            sb.append(ident).append("\t\t// Show unknown tag(s) if any.\n")
                    .append("\t\tsuper.toFIXString(sb);\n\n");
        }
    }
}
