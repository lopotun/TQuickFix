package net.kem.newtquickfix.builders;

import java.util.Map;

/**
 * Here I mention the Java Currency class explicitly, i.e "java.util.Currency" and not just "Currency".
 * That is done because of existing FIX field named "Currency" that clashes with java.util.Currency
 * Created by Evgeny Kurtser on 11/9/2015 at 12:27 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFCurrencyFieldBrick extends QFFieldBrick {
    public QFCurrencyFieldBrick(String parentClassName, Class typeClass, CharSequence importLine, CharSequence typeToStringConversion) {
        super(parentClassName, typeClass, importLine, typeToStringConversion);
    }

    @Override
    protected void getMethodGetInstanceType() {
        /*
		public static Currency getInstance(java.util.Currency value) {
            return new Currency(value);
        }
        */
        sb.append("\tpublic static ").append(fixName).append(" getInstance(").append(typeClass.getName()).append(" value) {\n");
        if (defaultValues != null) {
            sb.append("\t\t").append(fixName).append(" res = STATIC_VALUES_MAPPING.get(value);\n")
                    .append("\t\tif (res == null) {\n").append("\t\t\tres = new ").append(fixName).append("(value);\n")
                    .append("\t\t}\n")
                    .append("\t\treturn res;\n");
        } else {
            sb.append("\t\treturn new ").append(fixName).append("(value);\n");
        }
        sb.append("\t}\n");
    }

    @Override
    protected void getConstructor() {
		/*
		private Currency(java.util.Currency value) {
            this.value = value;
        }
		 */
        sb.append("\tprivate ").append(fixName).append('(').append(typeClass.getName()).append(" value) {\n")
                .append("\t\tthis.value = value;\n")
                .append("\t}\n\n");
    }

    @Override
    protected void getPredefinedStaticMembers() {
        if (defaultValues != null) {
            // "	private static final Map<Integer, FieldIntegerExample> STATIC_VALUES_MAPPING = new HashMap<>();\n\n"
            sb.append("\tprivate static final Map<").append(typeClass.getName()).append(", ").append(fixName).append("> STATIC_VALUES_MAPPING = new HashMap<>();\n\n");
            for (Map.Entry<CharSequence, CharSequence> defaultValue : defaultValues.entrySet()) {
                // "	public static final FieldIntegerExample UNKNOWN_CLIENT = new FieldIntegerExample(0);\n"
                sb.append("\tpublic static final ").append(fixName).append(' ').append(defaultValue.getKey()).append(" = new ").append(fixName).append('(');
                sb.append(defaultValue.getValue());
                sb.append(");\n");
            }
            sb.append('\n');
            sb.append("\tstatic {\n");
            for (Map.Entry<CharSequence, CharSequence> defaultValue : defaultValues.entrySet()) {
                // "		STATIC_VALUES_MAPPING.put(UNKNOWN_CLIENT.getValue(), UNKNOWN_CLIENT);\n"
                sb.append("\t\tSTATIC_VALUES_MAPPING.put(").append(defaultValue.getKey()).append(".getValue(), ").append(defaultValue.getKey()).append(");\n");
            }
            sb.append("\t}\n\n");
        }
    }

    @Override
    protected void getClassTitle() {
        sb.append("public class ").append(fixName).append(" extends ")
                .append(parentClassName).append('<').append(typeClass.getName()).append("> {\n");
    }
}