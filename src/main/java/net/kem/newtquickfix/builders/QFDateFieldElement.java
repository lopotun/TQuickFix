package net.kem.newtquickfix.builders;

import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:27 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFDateFieldElement extends QFFieldElement {
    public QFDateFieldElement(Element startElement, BuilderUtils.QFFieldBlockDef def) {
        super(startElement, def);
    }

    @Override
    protected void getClassTitle() {
        sb.append("public class ").append(name).append(" extends ")
                .append(def.parentClassName).append(" {\n");
    }

    @Override
    protected void getMethodGetInstanceString() {
		/*
		public static EffectiveTime getInstance(String value) throws ParseException {
            return getInstance(LocalDateTime.parse(value, getDateFormat()));
        }
        */
        if (def.typeToStringConversion != null) {
            sb.append("\tpublic static ").append(name).append(" getInstance(String value) throws DateTimeParseException {\n")
                    .append("\t\treturn getInstance(").append(def.typeClass.getSimpleName()).append(".parse(value, getDateFormat()));\n")
                    .append("\t}\n\n");
        }
    }
}