package net.kem.newtquickfix.builders;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFFieldBrick {
    protected final String parentClassName;
    protected final Class typeClass;
    private final CharSequence importLine;
    protected final CharSequence typeToStringConversion;

    public class Container {
        protected String fixName;
        private int tag;
        protected Map<CharSequence, CharSequence> defaultValues;
        protected StringBuilder sb = new StringBuilder();
        public String getJavaSourceFileName() {
            return fixName;
        }
        public StringBuilder getJavaSource() {
            return sb;
        }
    }

    public QFFieldBrick(String parentClassName, Class typeClass, CharSequence importLine, CharSequence typeToStringConversion) {
        this.parentClassName = parentClassName;
        this.typeClass = typeClass;
        this.importLine = importLine;
        this.typeToStringConversion = typeToStringConversion;
    }

    protected String getQuotedValue(String valueEnum) {
        return valueEnum;
    }

    public Container toJavaSource(Element startElement) {
        Container container = new Container();
        String attribute;
        attribute = startElement.getAttribute("number");
        container.tag = Integer.parseInt(attribute);

        attribute = startElement.getAttribute("name");
        container.fixName = attribute;

//        attribute = startElement.getAttribute("type");

        NodeList values = startElement.getElementsByTagName("value");
        if (values != null && values.getLength() > 0) {
            container.defaultValues = new HashMap<>();
            for (int j = 0; j < values.getLength(); j++) {
                Node value = values.item(j);
                if (value instanceof Element) {
                    String valueEnum = getQuotedValue(((Element) value).getAttribute("enum"));
                    CharSequence valueDescription = ((Element) value).getAttribute("description");
                    container.defaultValues.put(valueDescription, valueEnum);
                }
            }
        }

//				"package net.kem.newtquickfix;\n" + "\n" +
        getPackageSection(container);

//				"import net.kem.newtquickfix.blocks.QFField;\n" +
//				"\n" +
//				"import java.util.HashMap;\n" +
//				"import java.util.Map;\n" +
//				"\n" +
        getImportSection(container);

//				"/**\n" +
//				" * Eugene Kurtzer\n" +
//				" * Date: 19-Oct-15\n" +
//				" * Time: 8:48 AM\n" +
//				" * <a href=mailto:Lopotun@gmail.com>Eugene Kurtzer</a>\n" +
//				" */\n" +
        getCreditsSection(container);

//				"public class FieldIntegerExample extends QFField<Integer> {\n" +
        getClassTitle(container);

//				"    private static final Map<Integer, FieldIntegerExample> STATIC_VALUES_MAPPING = new HashMap<>();\n" +
//				"\n" +
//				"    public static final FieldIntegerExample UNKNOWN_CLIENT = new FieldIntegerExample(0);\n" +
//				"    public static final FieldIntegerExample EXCEEDS_MAXIMUM_SIZE = new FieldIntegerExample(1);\n" +
//				"    public static final FieldIntegerExample UNKNOWN_OR_INVALID_CURRENCY_PAIR = new FieldIntegerExample(2);\n" +
//				"    public static final FieldIntegerExample NO_AVAILABLE_STREAM = new FieldIntegerExample(3);\n" +
//				"    public static final FieldIntegerExample OTHER = new FieldIntegerExample(99);\n" +
//				"\n" +
//				"    static {\n" +
//				"        STATIC_VALUES_MAPPING.put(UNKNOWN_CLIENT.getValue(), UNKNOWN_CLIENT);\n" +
//				"        STATIC_VALUES_MAPPING.put(EXCEEDS_MAXIMUM_SIZE.getValue(), EXCEEDS_MAXIMUM_SIZE);\n" +
//				"        STATIC_VALUES_MAPPING.put(UNKNOWN_OR_INVALID_CURRENCY_PAIR.getValue(), UNKNOWN_OR_INVALID_CURRENCY_PAIR);\n" +
//				"        STATIC_VALUES_MAPPING.put(NO_AVAILABLE_STREAM.getValue(), NO_AVAILABLE_STREAM);\n" +
//				"        STATIC_VALUES_MAPPING.put(OTHER.getValue(), OTHER);\n" +
//				"    }\n" +
        getPredefinedStaticMembers(container);

//				"    public static final int TAG = 1497;\n" +
        getMemberTag(container);

//				"    private FieldIntegerExample(Integer value) {\n" +
//				"        this.value = value;\n" +
//				"    }\n" +
        getConstructor(container);

//				"    @Override\n" +
//				"    public int getTag() {\n" +
//				"        return TAG;\n" +
//				"    }\n" +
        getMethodGetTag(container);

//				"    public static FieldIntegerExample getInstance(String value) {\n" +
//				"        return getInstance(Integer.parseInt(value));\n" +
//				"    }\n" +
        getMethodGetInstanceString(container);

//				"    public static FieldIntegerExample getInstance(Integer value) {\n" +
//				"        FieldIntegerExample res = STATIC_VALUES_MAPPING.get(value);\n" +
//				"        if (res == null) {\n" +
//				"            res = new FieldIntegerExample(value);\n" +
//				"        }\n" +
//				"        return res;\n" +
//				"    }\n" +
        getMethodGetInstanceType(container);
        container.sb.append('}'); // end of class
        return container;
    }

    protected void getMethodGetInstanceType(Container container) {
        /*
		public static FieldIntegerExample getInstance(Integer value) {
			FieldIntegerExample res = STATIC_VALUES_MAPPING.get(value);
			if (res == null) {
				res = new FieldIntegerExample(value);
			}
			return res;
			// OR
			return new FieldIntegerExample(value);
		}
        */
        container.sb.append("\tpublic static ").append(container.fixName).append(" getInstance(").append(typeClass.getSimpleName()).append(" value) {\n");
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

    protected void getMethodGetInstanceString(Container container) {
		/*
		public static FieldIntegerExample getInstance(String value) {
			return getInstance(Integer.parseInt(value));
		};
        */
        if (typeToStringConversion != null) {
            container.sb.append("\tpublic static ").append(container.fixName).append(" getInstance(String value) {\n")
                    .append("\t\treturn getInstance(")
                    .append(typeToStringConversion)//"Integer.parseInt(value)"
                    .append(");\n\t}\n\n");
        }
    }

    private void getMethodGetTag(Container container) {
		/*
		@Override
		public int getTag() {
			return TAG;
		};*/
        container.sb.append("\t@Override\n").append("\tpublic int getTag() {\n").append("\t\treturn TAG;\n").append("\t}\n\n");
    }

    protected void getConstructor(Container container) {
		/*
		private FieldIntegerExample(Integer value) {
			this.value = value;
		}
		 */
        container.sb.append("\tprivate ").append(container.fixName).append('(').append(typeClass.getSimpleName()).append(" value) {\n")
                .append("\t\tthis.value = value;\n")
                .append("\t}\n\n");
    }

    private void getMemberTag(Container container) {
        container.sb.append("\tpublic static final int TAG = ").append(container.tag).append(";\n\n");
    }

    protected void getPredefinedStaticMembers(Container container) {
        if (container.defaultValues != null) {
            // "	private static final Map<Integer, FieldIntegerExample> STATIC_VALUES_MAPPING = new HashMap<>();\n\n"
            container.sb.append("\tprivate static final Map<").append(typeClass.getSimpleName()).append(", ").append(container.fixName).append("> STATIC_VALUES_MAPPING = new HashMap<>();\n\n");
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

    protected void getClassTitle(Container container) {
        container.sb.append("public class ").append(container.fixName).append(" extends ")
                .append(parentClassName).append('<').append(typeClass.getSimpleName()).append("> {\n");
    }

    private void getCreditsSection(Container container) {
        LocalDateTime now = LocalDateTime.now();
        container.sb.append("/**\n")
                .append(" * Eugene Kurtzer\n")
                .append(" * Date: ").append(now.format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n")
                .append(" * Time: ").append(now.format(DateTimeFormatter.ISO_TIME)).append("\n")
                .append(" * <a href=mailto:Lopotun@gmail.com>Eugene Kurtzer</a>\n")
                .append(" */\n");
    }

    private void getImportSection(Container container) {
        container.sb.append("import net.kem.newtquickfix.blocks.").append(parentClassName).append(";\n\n");
        if (container.defaultValues != null) {
            container.sb.append("import java.util.HashMap;\n").append("import java.util.Map;\n");
        }
        if (importLine != null) {
            container.sb.append(importLine).append("\n\n");
        }
    }

    private void getPackageSection(Container container) {
        container.sb.append("package net.kem.newtquickfix.fields;\n\n");
    }


    public static QFFieldBrick getNewQFFieldBrick(Element startElement) throws ClassNotFoundException {
        String attribute = startElement.getAttribute("type");
        return BuilderUtils.getJavaSourceFieldBuildingBrick(attribute);
    }
}