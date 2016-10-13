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
    protected void generateMethodGetInstanceString() {
        /*
        public static AggregatedBook of(String value) {
		    return of(value, LiteFixMessageParser.getComponentValidator()););
        }

        public static AggregatedBook of(String value, QFComponentValidator componentValidator) {
            if(value.length() == 1 && (value.charAt(0) == 'Y' || value.charAt(0) == 'y')) {
                return of(true);
            } else {
                final Boolean newValue = componentValidator.invalidFieldValue(AggregatedBook.class, Boolean.class, value, null);
                AccountType res = of(newValue, componentValidator);
			    res.originalValue = value;
			    return res;
            }
        }
        */
        if (def.typeToStringConversion != null) {
            sb.append("\tpublic static ").append(name).append(" of(String value) {\n")
                    .append("\t\treturn of(value, LiteFixMessageParser.getComponentValidator());\n")
                    .append("\t}\n\n");

            sb.append("\tpublic static ").append(name).append(" of(String value, QFComponentValidator componentValidator) {\n")
                    .append("\t\tif(value.length() == 1 && (value.charAt(0) == 'Y' || value.charAt(0) == 'y')) {\n")
                    .append("\t\t\treturn of(true);\n")
                    .append("\t\t} else {\n")
                    .append("\t\t\tfinal Boolean newValue = componentValidator.invalidFieldValue(").append(name).append(".class, Boolean.class, value, null);\n")
                    .append("\t\t\t").append(name).append(" res = of(newValue, componentValidator);\n")
                    .append("\t\t\tres.originalValue = value;\n")
                    .append("\t\t\treturn res;\n")
                    .append("\t\t}\n")
                    .append("\t}\n\n");
        }
    }
}