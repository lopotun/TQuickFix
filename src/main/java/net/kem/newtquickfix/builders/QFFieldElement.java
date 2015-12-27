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

    protected QFFieldElement(Element startElement, BuilderUtils.QFFieldBlockDef def) {
        super(startElement, new StringBuilder(), "");
        this.def = def;

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
        // import net.kem.newtquickfix.blocks.QFUtils;
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFUtils;\n\n");
        // import net.kem.newtquickfix.ValidationErrorsHandler;
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME).append("ValidationErrorsHandler;\n\n");
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
                .append(def.parentClassName).append('<').append(def.typeClass.getSimpleName()).append("> {\n");
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
        // private static final ValidationErrorsHandler<Integer> validationErrorsHandler = QFUtils.getValidationErrorsHandler(AllocTransType.class);
        sb.append("\tprivate static ValidationErrorsHandler<").append(def.typeClass.getSimpleName()).append("> validationErrorsHandler = QFUtils.getValidationErrorsHandler(").append(name).append(".class);\n");
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
        sb.append("\tprivate ").append(name).append('(').append(def.typeClass.getSimpleName()).append(" value) {\n")
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
    public static ApplReportType getInstance(String value) {
		try {
			return getInstance(Integer.parseInt(value));
		} catch (Exception e) {//NumberFormatException
			final Integer newValue = validationErrorsHandler.invalidValue(ApplReportType.class, value, e, ValidationErrorsHandler.ErrorType.PARSING);
			return getInstance(newValue);
		}
	}
    */
    protected void generateMethodGetInstanceString() {
        if (def.typeToStringConversion != null) {
            sb.append("\tpublic static ").append(name).append(" getInstance(String value) {\n")
                    .append("\t\ttry {\n")
                    .append("\t\t\treturn getInstance(").append(def.typeToStringConversion).append(");\n") //"Integer.parseInt(value)"
                    .append("\t\t} catch (Exception e) {\n")
                    .append("\t\t\tfinal ")
                        .append(def.typeClass==java.util.Currency.class? def.typeClass.getName(): def.typeClass.getSimpleName())
                        .append(" newValue = validationErrorsHandler.invalidValue(")
                        .append(name).append(".class, value, e, ValidationErrorsHandler.ErrorType.PARSING);\n")
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
    */
    protected void generateMethodGetInstanceType() {
        sb.append("\tpublic static ").append(name).append(" getInstance(").append(def.typeClass.getSimpleName()).append(" value) {\n");
        if (defaultValues != null) {
            sb.append("\t\t").append(name).append(" res = STATIC_VALUES_MAPPING.get(value);\n")
                    .append("\t\tif (res == null) {\n")
                    .append("\t\t\tfinal ")
                        .append(def.typeClass.getSimpleName()).append(" newValue = validationErrorsHandler.invalidValue(")
                        .append(name).append(".class, value, null, ValidationErrorsHandler.ErrorType.NOT_PREDEFINED);\n")
                    .append("\t\t\tres = new ").append(name).append("(newValue);\n")
                    .append("\t\t}\n")
                    .append("\t\treturn res;\n");
        } else {
            sb.append("\t\treturn new ").append(name).append("(value);\n");
        }
        sb.append("\t}\n");
    }

    protected void generateAuxMethods() {
        // public static ValidationErrorsHandler<String> getValidationErrorsHandler() {
        //    return validationErrorsHandler;
        // }
        // public static void setValidationHandler(ValidationErrorsHandler<LocalDateTime> newValidationHandler) {
        //    validationErrorsHandler = newValidationHandler;
        // }
        CharSequence diamondValue = def.typeClass==java.util.Currency.class ? def.typeClass.getName(): def.typeClass.getSimpleName();
        sb.append("\n\n");
        sb.append("\tpublic static ValidationErrorsHandler<").append(diamondValue).append("> getValidationErrorsHandler() {\n")
                .append("\t\treturn validationErrorsHandler;\n")
                .append("\t}\n");
        sb.append("\tpublic static void setValidationHandler(ValidationErrorsHandler<").append(diamondValue).append("> newValidationErrorsHandler) {\n")
                .append("\t\tvalidationErrorsHandler = newValidationErrorsHandler;\n")
                .append("\t}\n");
    }


//
//    @Override
//    public void toBrickJavaSource() {
//        addBrickAnnotation();
//        addBrickDeclaration();
//        addBrickGetter();
//        addBrickSetter();
//        sb.append('\n'); // end of member definition
//    }
//
//    // @QFMember(type = QFMember.Type.FIELD)
//    @Override
//    public void addBrickAnnotation() {
//        sb.append("\t@QFMember(type = QFMember.Type.FIELD)");
//    }
//
//    // private ComplexEventEndDate complexEventEndDate;
//    @Override
//    public void addBrickDeclaration() {
//        sb.append("\tprivate ").append(name).append(' ').append(StringUtils.uncapitalize(name)).append(";\n");
//    }
//
//    // public ComplexEventEndDate getComplexEventEndDate() {return complexEventEndDate;}
//    @Override
//    public void addBrickGetter() {
//        sb.append("\tpublic ").append(name).append(" get").append(name).append("() {\n")
//                .append("\t\treturn ").append(StringUtils.uncapitalize(name)).append(";\n\t}\n");
//    }
//
//    // public void setComplexEventEndDate(ComplexEventEndDate complexEventEndDate) {this.complexEventEndDate = complexEventEndDate;}
//    @Override
//    public void addBrickSetter() {
//        sb.append("\tpublic void set").append(name).append("(").append(name).append(' ').append(StringUtils.uncapitalize(name)).append(") {\n")
//                .append("\t\tthis.").append(StringUtils.uncapitalize(name)).append(" = ").append(StringUtils.uncapitalize(name)).append(";\n\t}\n");
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public QFMember.Type getTagType() {
//        return QFMember.Type.FIELD;
//    }
//
//    default boolean isRequired() {
//        return false;
//    }
}