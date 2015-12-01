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
        getPackageSection();
        getImportSection();
        getCreditsSection();
        getClassTitle();
        getPredefinedStaticMembers();
        getMemberTag();
        getConstructor();
        getMethodGetTag();
        getMethodGetInstanceString();
        getMethodGetInstanceType();
        sb.append('}'); // end of class
    }

    /*package net.kem.newtquickfix;*/
    private void getPackageSection() {
        sb.append("package net.kem.newtquickfix.fields;\n\n");
    }

    //				"import net.kem.newtquickfix.blocks.QFField;\n" +
    //				"\n" +
    //				"import java.util.HashMap;\n" +
    //				"import java.util.Map;\n" +
    //				"\n" +
    private void getImportSection() {
        sb.append("import net.kem.newtquickfix.blocks.").append(def.parentClassName).append(";\n\n");
        if (defaultValues != null) {
            sb.append("import java.util.HashMap;\n").append("import java.util.Map;\n");
        }
        if (def.importLine != null) {
            sb.append(def.importLine).append("\n\n");
        }
    }

    /*public class FieldIntegerExample extends QFField<Integer> {*/
    protected void getClassTitle() {
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
    protected void getPredefinedStaticMembers() {
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
    private void getMemberTag() {
        sb.append("\tpublic static final int TAG = ").append(tag).append(";\n\n");
    }

    /*
    private FieldIntegerExample(Integer value) {
        this.value = value;
    }
	*/
    protected void getConstructor() {
        sb.append("\tprivate ").append(name).append('(').append(def.typeClass.getSimpleName()).append(" value) {\n")
                .append("\t\tthis.value = value;\n")
                .append("\t}\n\n");
    }

    /*
    @Override
    public int getTag() {
        return TAG;
    };*/
    private void getMethodGetTag() {
        sb.append("\t@Override\n").append("\tpublic int getTag() {\n").append("\t\treturn TAG;\n").append("\t}\n\n");
    }

    /*
    public static FieldIntegerExample getInstance(String value) {
        return getInstance(Integer.parseInt(value));
    };
    */
    protected void getMethodGetInstanceString() {
        if (def.typeToStringConversion != null) {
            sb.append("\tpublic static ").append(name).append(" getInstance(String value) {\n")
                    .append("\t\treturn getInstance(")
                    .append(def.typeToStringConversion)//"Integer.parseInt(value)"
                    .append(");\n\t}\n\n");
        }
    }

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
    protected void getMethodGetInstanceType() {
        sb.append("\tpublic static ").append(name).append(" getInstance(").append(def.typeClass.getSimpleName()).append(" value) {\n");
        if (defaultValues != null) {
            sb.append("\t\t").append(name).append(" res = STATIC_VALUES_MAPPING.get(value);\n")
                    .append("\t\tif (res == null) {\n").append("\t\t\tres = new ").append(name).append("(value);\n")
                    .append("\t\t}\n")
                    .append("\t\treturn res;\n");
        } else {
            sb.append("\t\treturn new ").append(name).append("(value);\n");
        }
        sb.append("\t}\n");
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