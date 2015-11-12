package net.kem.newtquickfix.builders;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:27 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFBooleanFieldBrick extends QFFieldBrick {
    public QFBooleanFieldBrick(String parentClassName, Class typeClass, CharSequence importLine, CharSequence typeToStringConversion) {
        super(parentClassName, typeClass, importLine, typeToStringConversion);
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
        if (typeToStringConversion != null) {
            sb.append("\tpublic static ").append(fixName).append(" getInstance(String value) {\n")
                    .append("\t\tif(value.length() == 1 && (value.charAt(0) == 'Y' || value.charAt(0) == 'y')) {\n")
                    .append("\t\t\treturn getInstance(true);\n")
                    .append("\t\t}\n")
                    .append("\t\treturn getInstance(")
                    .append(typeToStringConversion)//"Integer.parseInt(value)"
                    .append(");\n\t}\n\n");
        }
    }
}