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
    protected void getMethodGetInstanceType(Container container) {
        /*
		public static Currency getInstance(java.util.Currency value) {
            return new Currency(value);
        }
        */
        container.sb.append("\tpublic static ").append(container.fixName).append(" getInstance(").append(typeClass.getName()).append(" value) {\n");
        if (container.defaultValues != null) {
            container.sb.append("\t\t").append(container.fixName).append(" res = STATIC_VALUES_MAPPING.get(value);\n")
                    .append("\t\tif (res == null) {\n").append("\t\t\tres = new ").append(container.fixName).append("(value);\n")
                    .append("\t\t}\n")
                    .append("\t\treturn res;\n");
        } else {
            container.sb.append("\t\treturn new ").append(container.fixName).append("(value);\n");
        }
        container.sb.append("\t}\n");
    }

    @Override
    protected void getConstructor(Container container) {
		/*
		private Currency(java.util.Currency value) {
            this.value = value;
        }
		 */
        container.sb.append("\tprivate ").append(container.fixName).append('(').append(typeClass.getName()).append(" value) {\n")
                .append("\t\tthis.value = value;\n")
                .append("\t}\n\n");
    }

    @Override
    protected void getPredefinedStaticMembers(Container container) {
        if (container.defaultValues != null) {
            // "	private static final Map<Integer, FieldIntegerExample> STATIC_VALUES_MAPPING = new HashMap<>();\n\n"
            container.sb.append("\tprivate static final Map<").append(typeClass.getName()).append(", ").append(container.fixName).append("> STATIC_VALUES_MAPPING = new HashMap<>();\n\n");
            for (Map.Entry<CharSequence, CharSequence> defaultValue : container.defaultValues.entrySet()) {
                // "	public static final FieldIntegerExample UNKNOWN_CLIENT = new FieldIntegerExample(0);\n"
                container.sb.append("\tpublic static final ").append(container.fixName).append(' ').append(defaultValue.getKey()).append(" = new ").append(container.fixName).append('(');
                container.sb.append(defaultValue.getValue());
                container.sb.append(");\n");
            }
            container.sb.append('\n');
            container.sb.append("\tstatic {\n");
            for (Map.Entry<CharSequence, CharSequence> defaultValue : container.defaultValues.entrySet()) {
                // "		STATIC_VALUES_MAPPING.put(UNKNOWN_CLIENT.getValue(), UNKNOWN_CLIENT);\n"
                container.sb.append("\t\tSTATIC_VALUES_MAPPING.put(").append(defaultValue.getKey()).append(".getValue(), ").append(defaultValue.getKey()).append(");\n");
            }
            container.sb.append("\t}\n\n");
        }
    }

    @Override
    protected void getClassTitle(Container container) {
        container.sb.append("public class ").append(container.fixName).append(" extends ")
                .append(parentClassName).append('<').append(typeClass.getName()).append("> {\n");
    }
}