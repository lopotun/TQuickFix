package net.kem.newtquickfix.builders;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFFieldElement extends QFElement {
    private int tag;
    protected Map<CharSequence, CharSequence> defaultValues;
    protected BuilderUtils.QFFieldBlockDef def;
    protected CharSequence typeClassName;

    protected QFFieldElement(Element startElement, BuilderUtils.QFFieldBlockDef def) {
        super(startElement, new StringBuilder(), "");
        this.def = def;
        this.typeClassName = def.typeClass==java.util.Currency.class? def.typeClass.getName(): def.typeClass.getSimpleName();

        String attribute;
        attribute = startElement.getAttribute("number");
        tag = Integer.parseInt(attribute);

        NodeList values = startElement.getElementsByTagName("value");
        if (values != null && values.getLength() > 0) {
            defaultValues = new HashMap<>();
            for (int j = 0; j < values.getLength(); j++) {
                Node value = values.item(j);
                if (value instanceof Element) {
                    String valueEnum = getQuotedValue(((Element) value).getAttribute("enum"));
                    CharSequence valueDescription = ((Element) value).getAttribute("description");
                    defaultValues.put(valueDescription, valueEnum);
                }
            }
        }
    }

    public static QFFieldElement getNewQFFieldBrick(Element startElement) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String attribute = startElement.getAttribute("type");
        BuilderUtils.QFFieldBlockDef def = BuilderUtils.getJavaSourceFieldBuildingBrick1(attribute);
        Constructor<QFFieldElement> constructor = def.thisClass.getDeclaredConstructor(Element.class, BuilderUtils.QFFieldBlockDef.class);
        QFFieldElement res = constructor.newInstance(startElement, def);
        return res;
    }

    protected String getQuotedValue(String valueEnum) {
        return valueEnum;
    }

    protected void getImportSection() {
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME).append("LiteFixMessageParser;\n");
        super.getImportSection();
    }

    @Override
    public void toJavaSource() {
        generatePackageSection();
        generateImportSection();
        generateCreditsSection();
        generateClassTitle();
        generatePredefinedStaticMembers();
        generateMemberTag();
        generateConstructor();
        generateMethodGetTag();
        generateMethodGetInstanceString();
        generateMethodGetInstanceType();
        generateAuxMethods();
        sb.append('}'); // end of class
    }

    /*package net.kem.newtquickfix;*/
    private void generatePackageSection() {
        sb.append("package ").append(BuilderUtils.PACKAGE_NAME_FIELDS).append(";\n\n");
    }

    //				"import net.kem.newtquickfix.blocks.QFField;\n" +
    //				"\n" +
    //				"import java.util.HashMap;\n" +
    //				"import java.util.Map;\n" +
    //				"\n" +
    private void generateImportSection() {
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append(def.parentClassName).append(";\n\n");
        // import net.kem.newtquickfix.LiteFixMessageParser;
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME).append("LiteFixMessageParser;\n\n");
        // import net.kem.newtquickfix.QFComponentValidator;
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME).append("QFComponentValidator;\n\n");
        if (def.typeToStringConversion == null) {
            sb.append("import org.apache.commons.lang3.StringUtils;\n");
        }
        if (defaultValues != null) {
            sb.append("import java.util.HashMap;\n").append("import java.util.Map;\n");
        }
        if (def.importLine != null) {
            sb.append(def.importLine).append("\n\n");
        }
    }

    /*public class FieldIntegerExample extends QFField<Integer> {*/
    protected void generateClassTitle() {
        sb.append("public class ").append(name).append(" extends ")
                .append(def.parentClassName).append('<').append(typeClassName).append("> {\n");
    }

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
    protected void generatePredefinedStaticMembers() {
        if (defaultValues != null) {
            // "	private static final Map<Integer, FieldIntegerExample> STATIC_VALUES_MAPPING = new HashMap<>();\n\n"
            sb.append("\tprivate static final Map<").append(def.typeClass.getSimpleName()).append(", ").append(name).append("> STATIC_VALUES_MAPPING = new HashMap<>();\n\n");
            for (Map.Entry<CharSequence, CharSequence> defaultValue : defaultValues.entrySet()) {
                // "	public static final FieldIntegerExample UNKNOWN_CLIENT = new FieldIntegerExample(0);\n"
                sb.append("\tpublic static final ").append(name).append(' ').append(defaultValue.getKey()).append(" = new ").append(name).append('(');
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

    /*public static final int TAG = 1497;*/
    private void generateMemberTag() {
        sb.append("\tpublic static final int TAG = ").append(tag).append(";\n\n");
    }

    /*
    private FieldIntegerExample(Integer value) {
        this.value = value;
    }
	*/
    protected void generateConstructor() {
        sb.append("\tprivate ").append(name).append('(').append(typeClassName).append(" value) {\n")
                .append("\t\tthis.value = value;\n")
                .append("\t}\n\n");
    }

    /*
    @Override
    public int getTag() {
        return TAG;
    };*/
    private void generateMethodGetTag() {
        sb.append("\t@Override\n").append("\tpublic int getTag() {\n").append("\t\treturn TAG;\n").append("\t}\n\n");
    }

    /*
    public static AccountType getInstance(String value) {
		return getInstance(value, LiteFixMessageParser.getComponentValidator());
	}

	public static AccountType getInstance(String value, QFComponentValidator componentValidator) {
		try {
			return getInstance(Integer.parseInt(value), componentValidator);
		} catch (Exception e) {
			final Integer newValue = componentValidator.invalidFieldValue(AccountType.class, Integer.class, value, e);
			return getInstance(newValue);
		}
	}
    */
    protected void generateMethodGetInstanceString() {
        if (def.typeToStringConversion != null) {
            sb.append("\tpublic static ").append(name).append(" getInstance(String value) {\n")
                    .append("\t\treturn getInstance(value, LiteFixMessageParser.getComponentValidator());\n")
                    .append("\t}\n\n");

            CharSequence typeClassName = def.typeClass==java.util.Currency.class? def.typeClass.getName(): def.typeClass.getSimpleName();
            sb.append("\tpublic static ").append(name).append(" getInstance(String value, QFComponentValidator componentValidator) {\n")
                    .append("\t\ttry {\n")
                    .append("\t\t\treturn getInstance(").append(def.typeToStringConversion).append(", componentValidator);\n") //"Integer.parseInt(value)"
                    .append("\t\t} catch (Exception e) {\n")
                    .append("\t\t\tfinal ").append(typeClassName)
                        .append(" newValue = componentValidator.invalidFieldValue(")
                        .append(name).append(".class, ").append(typeClassName).append(".class, value, e);\n")
                    .append("\t\t\treturn getInstance(newValue);\n")
                    .append("\t\t}\n")
            .append("\t}\n\n");
        }
    }

    /*
    public static FieldIntegerExample getInstance(Integer value) {
        FieldIntegerExample res = STATIC_VALUES_MAPPING.get(value);
        if (res == null) {
            final Integer newValue = validationErrorsHandler.invalidValue(ApplReportType.class, value, null, ValidationErrorsHandler.ErrorType.NOT_PREDEFINED);
			res = new ApplReportType(newValue);
        }
        return res;
        // OR
        return new FieldIntegerExample(value);
    }



    public static AccountType getInstance(Integer value) {
		return getInstance(value, LiteFixMessageParser.getComponentValidator());
	}

	public static AccountType getInstance(Integer value, QFComponentValidator componentValidator) {
		AccountType res = STATIC_VALUES_MAPPING.get(value);
		if (res == null) {
			final Integer newValue = componentValidator.notPredefinedFieldValue(AccountType.class, Integer.class, value);
			res = new AccountType(newValue);
		}
		return res;
	}
    */
    protected void generateMethodGetInstanceType() {
        sb.append("\tpublic static ").append(name).append(" getInstance(").append(typeClassName).append(" value) {\n")
                .append("\t\treturn getInstance(value, LiteFixMessageParser.getComponentValidator());\n")
                .append("\t}\n\n");

        sb.append("\tpublic static ").append(name).append(" getInstance(").append(typeClassName).append(" value, QFComponentValidator componentValidator) {\n");
        if (defaultValues != null) {
            sb.append("\t\t").append(name).append(" res = STATIC_VALUES_MAPPING.get(value);\n")
                    .append("\t\tif (res == null) {\n")
                    .append("\t\t\tfinal ")
                        .append(def.typeClass.getSimpleName()).append(" newValue = componentValidator.notPredefinedFieldValue(")
                        .append(name).append(".class, ").append(def.typeClass.getSimpleName()).append(".class, value);\n")
                    .append("\t\t\tres = new ").append(name).append("(newValue);\n")
                    .append("\t\t}\n")
                    .append("\t\treturn res;\n");
        } else {
            if (def.typeToStringConversion == null) {
                sb.append("\t\tif(StringUtils.isBlank(value)) {\n");
                sb.append("\t\t\tvalue = componentValidator.invalidFieldValue(").append(name).append(".class, ").append(typeClassName).append(".class, value, null);\n");
            } else {
                sb.append("\t\tif(value == null) {\n");
                sb.append("\t\t\tvalue = componentValidator.invalidFieldValue(").append(name).append(".class, ").append(typeClassName).append(".class, null, null);\n");
            }
            sb.append("\t\t}\n")
                    .append("\t\treturn new ").append(name).append("(value);\n");
        }
        sb.append("\t}\n");
    }

    protected void generateAuxMethods() {
    }
}