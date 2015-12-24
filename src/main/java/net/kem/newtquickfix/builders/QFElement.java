package net.kem.newtquickfix.builders;

import net.kem.newtquickfix.blocks.QFMember;
import org.w3c.dom.Element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFElement {
    protected Element startElement;
    protected StringBuilder sb;
    protected String name;
    protected QFMember.Type type;
    protected CharSequence ident;

    protected QFElement(Element startElement, StringBuilder sb, CharSequence ident) throws IllegalArgumentException {
        this.startElement = startElement;
        this.sb = sb;
        this.name = startElement.getAttribute("name");
        this.type = QFMember.Type.valueOf(startElement.getTagName().toUpperCase());
        this.ident = ident;
    }

    public QFMember.Type getTagType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getJavaSourceFileName() {
        return name;
    }

    public StringBuilder getJavaSource() {
        return sb;
    }

    public abstract void toJavaSource();

    protected void generateCreditsSection() {
        LocalDateTime now = LocalDateTime.now();
        sb.append("/**\n")
                .append(" * Eugene Kurtzer\n")
                .append(" * Date: ").append(now.format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n")
                .append(" * Time: ").append(now.format(DateTimeFormatter.ISO_TIME)).append("\n")
                .append(" * <a href=mailto:Lopotun@gmail.com>Eugene Kurtzer</a>\n")
                .append(" */\n");
    }

    protected void getImportSection() {
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFField;\n");
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFFieldUtils;\n\n");
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFMember;\n");
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("ValidationHandler;\n\n");
        sb.append("import java.util.Stack;\n");
        sb.append('\n');
    }
}