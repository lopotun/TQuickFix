package net.kem.newtquickfix.builders;

import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:27 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFBooleanFieldElement extends QFFieldElement {
    public QFBooleanFieldElement(Element startElement, BuilderUtils.QFFieldBlockDef def) {
        super(startElement, def);
    }

    protected String getQuotedValue(String valueEnum) {
        if (valueEnum.charAt(0) == 'Y' || valueEnum.charAt(0) == 'y') {
            valueEnum = "true";
        } else {
            valueEnum = "false";
        }
        return valueEnum;
    }

    @Override
    protected void getMethodGetInstanceString() {
        /*
        public static AggressorIndicator getInstance(String value) {
            if(value.length() == 1 && (value.charAt(0) == 'Y' || value.charAt(0) == 'y')) {
                return getInstance(true);
            }
            return getInstance(Boolean.parseBoolean(value));
        }
        */
        if (def.typeToStringConversion != null) {
            sb.append("\tpublic static ").append(name).append(" getInstance(String value) {\n")
                    .append("\t\tif(value.length() == 1 && (value.charAt(0) == 'Y' || value.charAt(0) == 'y')) {\n")
                    .append("\t\t\treturn getInstance(true);\n")
                    .append("\t\t}\n")
                    .append("\t\treturn getInstance(")
                    .append(def.typeToStringConversion)//"Integer.parseInt(value)"
                    .append(");\n\t}\n\n");
        }
    }
}