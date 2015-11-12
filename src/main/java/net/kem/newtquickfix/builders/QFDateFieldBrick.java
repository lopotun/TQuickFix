package net.kem.newtquickfix.builders;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:27 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFDateFieldBrick extends QFFieldBrick {
    public QFDateFieldBrick(String parentClassName, Class typeClass, CharSequence importLine, CharSequence typeToStringConversion) {
        super(parentClassName, typeClass, importLine, typeToStringConversion);
    }

    @Override
    protected void getClassTitle() {
        sb.append("public class ").append(fixName).append(" extends ")
                .append(parentClassName).append(" {\n");
    }

    @Override
    protected void getMethodGetInstanceString() {
		/*
		public static EffectiveTime getInstance(String value) throws ParseException {
            return getInstance(LocalDateTime.parse(value, getDateFormat()));
        }
        */
        if (typeToStringConversion != null) {
            sb.append("\tpublic static ").append(fixName).append(" getInstance(String value) throws DateTimeParseException {\n")
                    .append("\t\treturn getInstance(").append(typeClass.getSimpleName()).append(".parse(value, getDateFormat()));\n")
                    .append("\t}\n\n");
        }
    }
}