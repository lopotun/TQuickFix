package com.traiana.tquickfix.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 10/30/14
 * Time: 2:43 PM
 * Generates QF Field class file.
 */
public class QFFieldClassBuilder {


    //	 <field number="5" javaType="AdvTransType" javaType="STRING">
//	     <value enum="N" description="NEW"/>
//	     <value enum="C" description="CANCEL"/>
//	     <value enum="R" description="REPLACE"/>
//	 </field>
    static class TypeMetadata {
        private boolean useByStringInstantiation;
        private String superclassName;
        private String javaType;
        private String imports;

        String getByStringInstantiation(boolean hasPredefinedValues) {
            if(useByStringInstantiation) {
                String jDoc = "\n\t/**\n" +
                        "\t * Creates instance of {@linkplain $CLASS_NAME} from the given raw String value.\n" +
                        "\t * @param rawValue the field string value.\n" +
                        "\t * @param validateValue validation policy.\n" +
                        "\t * @return instance of {@linkplain $CLASS_NAME}.\n" +
                        "\t * @throws IllegalArgumentException if the given validation policy restricts instance creation with the given value.\n" +
                        "\t * @see Validation\n" +
                        "\t */\n";
                if(hasPredefinedValues) {
                    return jDoc + "\tpublic static $CLASS_NAME getInstance(String rawValue, Validation validateValue) throws IllegalArgumentException {\n" +
                            "\t\t$CLASS_NAME res = getPredefinedInstance(rawValue, validateValue);\n" +
                            "\t\tif(res == null) {\n" +
                            "\t\t\tres = new $CLASS_NAME(rawValue, validateValue);\n" +
                            "\t\t}\n" +
                            "\t\treturn res;\n" +
                            "\t}\n";
                } else {
                    return jDoc + "\tpublic static $CLASS_NAME getInstance(String rawValue, Validation validateValue) throws IllegalArgumentException {\n" +
                            "\t\treturn new $CLASS_NAME(rawValue, validateValue);\n" +
                            "\t}\n";
                }
            } else {
                return "";
            }
        }

        String getByTypeInstantiation(boolean hasPredefinedValues) {
            String jDoc, res;
            if(hasPredefinedValues) {
                jDoc = "\t/**\n" +
                        "\t * Creates instance of {@linkplain $CLASS_NAME}.\n" +
                        "\t * @param value the field value.\n" +
                        "\t * @param validateValue validation policy.\n" +
                        "\t * @return instance of {@linkplain $CLASS_NAME}.\n" +
                        "\t * @throws IllegalArgumentException if the given validation policy restricts instance creation with the given value.\n" +
                        "\t * @see Validation\n" +
                        "\t */\n";
                res = jDoc + "\tpublic static $CLASS_NAME getInstance($TYPE value, Validation validateValue) throws IllegalArgumentException {\n" +
                        "\t\t$CLASS_NAME res = getPredefinedInstance(value, validateValue);\n" +
                        "\t\tif(res == null) {\n" +
                        "\t\t\tres = new $CLASS_NAME(value, validateValue);\n" +
                        "\t\t}\n" +
                        "\t\treturn res;\n" +
                        "\t}\n";
            } else {
                jDoc = "\t/**\n" +
                        "\t * Creates instance of {@linkplain $CLASS_NAME}.\n" +
                        "\t * @param value the field value.\n" +
                        "\t * @param validateValue validation policy. Since this field doesn't have predefined values, \n" +
                        "\t *                      the {@linkplain Validation#OMIT_PREDEFINED} will have the same meaning as {@linkplain Validation#FULL}.\n" +
                        "\t * @return instance of {@linkplain $CLASS_NAME}.\n" +
                        "\t * @throws IllegalArgumentException if the given validation policy restricts instance creation with the given value.\n" +
                        "\t * @see Validation\n" +
                        "\t */\n";
                res = jDoc + "\tpublic static $CLASS_NAME getInstance($TYPE value, Validation validateValue) throws IllegalArgumentException {\n" +
                        "\t\treturn new $CLASS_NAME(value, validateValue);\n" +
                        "\t}\n";
            }
            return res;
        }

        String getByStringCtor(boolean hasPredefinedValues) {
            return useByStringInstantiation? "\tprotected $CLASS_NAME(String rawValue, Validation validateValue) {\n" +
                    "\t\tsuper(NAME, NUMBER, rawValue, validateValue);\n" +
                    "\t}\n":
                    "";
        }

        String getByTypeCtor(boolean hasPredefinedValues) {
            return "\tprotected $CLASS_NAME($TYPE value, Validation validateValue) {\n" +
                    "\t\tsuper(NUMBER, NAME, value, validateValue);\n" +
                    "\t}\n";
        }

        String getByObjectObjectInstantiation(boolean hasPredefinedValues) {
            return hasPredefinedValues?
                    "\t/**\n" +
                    "\t * Tries to retrieve predefined $CLASS_NAME instance based on the given value.\n" +
                    "\t * @param value         field value.\n" +
                    "\t * @param validateValue validation rule.\n" +
                    "\t * @return $CLASS_NAME instance if the instance with the given value is defined. Otherwise the method either returns <em>null</em> or throws exception (see below).\n" +
                    "\t * @throws IllegalArgumentException if $CLASS_NAME instance with the given value is not defined and validateValue is {@linkplain Validation#FULL}.\n" +
                    "\t */\n" +
                    "\tprivate static $CLASS_NAME getPredefinedInstance(Object value, Validation validateValue) throws IllegalArgumentException {\n" +
                    "\t\t$CLASS_NAME res = FIELD_VALUES.get(value);\n" +
                    "\t\tif(res == null && validateValue == Validation.FULL) {\n" +
//                    "\t\t\tQFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.OUT_OF_PREDEFINED_SCOPE, \"Value \" + value + \" is out of predefined values set of tag $CLASS_NAME (\" + $CLASS_NAME.NUMBER + \")\");\n" +
                    "\t\t\tthrow new IllegalArgumentException(\"Value \" + value + \" is out of predefined values set of tag $CLASS_NAME (\" + $CLASS_NAME.NUMBER + \")\");\n" +
                    "\t\t}\n" +
                    "\t\treturn res;\n" +
                    "\t}\n":

                    "";
        }

        TypeMetadata(String superclassName, String javaType, String imports, boolean useByStringInstantiation) {
            this.superclassName = superclassName;
            this.javaType = javaType;
            this.imports = imports;
            this.useByStringInstantiation = useByStringInstantiation;
        }
    }
    private static final TypeMetadata TMD_String = new TypeMetadata("QFStringField", "String", null, false);
    private static final TypeMetadata TMD_Character = new TypeMetadata("QFCharacterField", "Character", null, true);
    private static final TypeMetadata TMD_BigDecimal = new TypeMetadata("QFBigDecimalField", "BigDecimal", "import java.math.BigDecimal;", true);
    private static final TypeMetadata TMD_Integer = new TypeMetadata("QFIntegerField", "Integer", null, true);
    private static final TypeMetadata TMD_Double = new TypeMetadata("QFDoubleField", "Double", null, true);
    private static final TypeMetadata TMD_Boolean = new TypeMetadata("QFBooleanField", "Boolean", null, true);
    private static final TypeMetadata TMD_Float = new TypeMetadata("QFFloatField", "Float", null, true);
    private static final TypeMetadata TMD_Currency = new TypeMetadata("QFCurrencyField", "Currency", "import java.util.Currency;", true);

    private static final TypeMetadata TMD_DateTime = new TypeMetadata("QFDateTimeField", "Date", "import java.util.Date;", true);
    private static final TypeMetadata TMD_Date = new TypeMetadata("QFDateField", "Date", "import java.util.Date;", true);
    private static final TypeMetadata TMD_MonthYear = new TypeMetadata("QFMonthYearField", "Date", "import java.util.Date;", true);
    private static final TypeMetadata TMD_Time = new TypeMetadata("QFTimeField", "Date", "import java.util.Date;", true);

    private static final Map<String, TypeMetadata> FIX_TYPE_TO_JAVA_TYPE = new HashMap<String, TypeMetadata>();
    static {
        FIX_TYPE_TO_JAVA_TYPE.put("STRING", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("MULTIPLECHARVALUE", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("EXCHANGE", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("DATA", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("LOCALMKTDATE", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("MONTHYEAR", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("MULTIPLESTRINGVALUE", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("MULTIPLEVALUESTRING", TMD_String); // In FIX v4.4 only.
        FIX_TYPE_TO_JAVA_TYPE.put("COUNTRY", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("TZTIMEONLY", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("TZTIMESTAMP", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("XMLDATA", TMD_String);
        FIX_TYPE_TO_JAVA_TYPE.put("LANGUAGE", TMD_String);

        FIX_TYPE_TO_JAVA_TYPE.put("CHAR", TMD_Character);
        FIX_TYPE_TO_JAVA_TYPE.put("PRICE", TMD_BigDecimal);
        FIX_TYPE_TO_JAVA_TYPE.put("CURRENCY", TMD_Currency);

        FIX_TYPE_TO_JAVA_TYPE.put("LENGTH", TMD_Integer);
        FIX_TYPE_TO_JAVA_TYPE.put("NUMINGROUP", TMD_Integer);
        FIX_TYPE_TO_JAVA_TYPE.put("SEQNUM", TMD_Integer);
        FIX_TYPE_TO_JAVA_TYPE.put("INT", TMD_Integer);
        FIX_TYPE_TO_JAVA_TYPE.put("DAY-OF-MONTH", TMD_Integer); // In FIX v5.0 only.

        FIX_TYPE_TO_JAVA_TYPE.put("AMT", TMD_Double);
        FIX_TYPE_TO_JAVA_TYPE.put("QTY", TMD_Double);
        FIX_TYPE_TO_JAVA_TYPE.put("PERCENTAGE", TMD_Double);
        FIX_TYPE_TO_JAVA_TYPE.put("PRICEOFFSET", TMD_Double);
        FIX_TYPE_TO_JAVA_TYPE.put("BOOLEAN", TMD_Boolean);
        FIX_TYPE_TO_JAVA_TYPE.put("FLOAT", TMD_Float);

        FIX_TYPE_TO_JAVA_TYPE.put("UTCTIMESTAMP", TMD_DateTime);
        FIX_TYPE_TO_JAVA_TYPE.put("UTCDATEONLY", TMD_Date); // In FIX v4.0 only.
        FIX_TYPE_TO_JAVA_TYPE.put("DATE", TMD_Date);
        FIX_TYPE_TO_JAVA_TYPE.put("UTCTIMEONLY", TMD_Time);
        FIX_TYPE_TO_JAVA_TYPE.put("TIME", TMD_Time); // In FIX v4.0 only.
        FIX_TYPE_TO_JAVA_TYPE.put("MONTH-YEAR", TMD_MonthYear); // In FIX v5.0 only.
    }


    public static String generateClassFile(String fNumber, String fName, String fType, List<String[]> fEnumDescr) {
        TypeMetadata tmd = FIX_TYPE_TO_JAVA_TYPE.get(fType);
        boolean hasPredefinedValues = !fEnumDescr.isEmpty();
        String res = TEMPLATE_CLASS
                .replace("$PACKAGE", getPackage())// $PACKAGE package com.traiana.tquickfix.qf.field;
                .replace("$IMPORT", getImport(tmd.superclassName, tmd.imports, fEnumDescr))// $IMPORT    ...
                .replace("$SUPERCLASS", tmd.superclassName)// $SUPERCLASS  QFIntegerField
                .replace("$NUMBER", fNumber)// $NUMBER  1144
                .replace("$PREDEFINED_SECTION", getFieldValuesSection(fName, tmd.javaType, tmd.useByStringInstantiation, fEnumDescr).toString())

                .replace("$INSTANCE_BY_STRING", tmd.getByStringInstantiation(hasPredefinedValues))
                .replace("$INSTANCE_BY_TYPE", tmd.getByTypeInstantiation(hasPredefinedValues))
                .replace("$INSTANCE_NO_VALIDATION", getByTypeInstantiationNoValidation(hasPredefinedValues))

                .replace("$CTOR_BY_STRING", tmd.getByStringCtor(hasPredefinedValues))
                .replace("$CTOR_BY_TYPE", tmd.getByTypeCtor(hasPredefinedValues))
                .replace("$INSTANCE_BY_OBJECT", tmd.getByObjectObjectInstantiation(hasPredefinedValues))

                .replace("$TYPE", tmd.javaType)// $CTOR_METHOD_BODY    ...
                .replace("$CLASS_NAME", fName);// $CLASS_NAME  ImpliedMarketIndicator
        return res;
    }

    private static CharSequence getPackage() {
        return "package com.traiana.tquickfix.qf." + QFBuilder.qfVersion + ".field;\n";
    }

    private static CharSequence getImport(CharSequence superclassName, CharSequence imports, List<String[]> fEnumDescr) {
        StringBuilder sb = new StringBuilder();
        sb.append("import com.traiana.tquickfix.QFParser;\n");
        sb.append("import com.traiana.tquickfix.ThreadContext;\n");
        sb.append("import com.traiana.tquickfix.blocks.").append(superclassName).append(";\n");
        if(imports != null) {
            sb.append(imports).append("\n");
        }
        if(!fEnumDescr.isEmpty()) {
            sb.append("\nimport java.util.HashMap;\nimport java.util.Map;\n");
        }
        return sb;
    }

    private static StringBuilder getFieldValuesSection(CharSequence className, CharSequence fType, boolean useByStringInstantiation, List<String[]> fEnumDescr) {
        StringBuilder sb = new StringBuilder();
        if(fEnumDescr.isEmpty()) {
            return sb;
        }

        //protected static final Map<Integer, ModelType> M_FIELD_VALUES = new HashMap<>(116);
        sb.append("\tprivate static final Map<Object, $CLASS_NAME> FIELD_VALUES = new HashMap<Object, $CLASS_NAME>($SIZE);\n".replace("$TYPE", fType).replace("$CLASS_NAME", className).replace("$SIZE", String.valueOf(fEnumDescr.size())));

        //public static final ModelType M_UTILITY_PROVIDED_STANDARD_MODEL = new ModelType(0, true);
        //public static final ModelType M_PROPRIETARY_MODEL = new ModelType(1, true);
        String member = "\tpublic static final $CLASS_NAME $CONST_NAME = new $CLASS_NAME($CONST_VALUE, Validation.OMIT_PREDEFINED);\n";
        char fEnumBoundares;
        if(fType.equals("String")) {
            fEnumBoundares = '"';
        } else {
            if(fType.equals("Character")) {
                fEnumBoundares = '\'';
            } else {
                if(fType.equals("Boolean")) {
                    fEnumBoundares = 1;
                } else {
                    fEnumBoundares = 0;
                }
            }
        }
        for(String[] ed : fEnumDescr) {
            String fEnum = ed[0], fDescr = ed[1];
            if(fEnumBoundares != 0) {
                if(fEnumBoundares == 1) {
                    fEnum = fEnum.charAt(0) == 'Y'? "true": "false";
                } else {
                    fEnum = fEnumBoundares + fEnum + fEnumBoundares;
                }
            }
            sb.append(member.replace("$CLASS_NAME", className).replace("$CONST_NAME", fDescr).replace("$CONST_VALUE", fEnum));
        }

        sb.append("\tstatic {\n");
        for(String[] ed : fEnumDescr) {
            String fDescr = ed[1];
            sb.append(String.format("\t\tFIELD_VALUES.put(%s.value, %s);%n", fDescr, fDescr));
            if(useByStringInstantiation) {
                sb.append(String.format("\t\tFIELD_VALUES.put(%s.rawValue, %s);%n", fDescr, fDescr));
            }
        }
        sb.append("\t}\n");

        sb.append("\t@Override\n" +
                "\tprotected boolean isPredefined() {\n" +
                "\t\treturn FIELD_VALUES.containsKey(value)");
        if(useByStringInstantiation) {
            sb.append(" || FIELD_VALUES.containsKey(rawValue);\n\t}");
        } else {
            sb.append(";\n\t}");
        }
        return sb;
    }

    private static CharSequence getByTypeInstantiationNoValidation(boolean hasPredefinedValues) {
        if(hasPredefinedValues) {
            return "";
        }
        return "\t/**\n" +
                "\t * Creates non-validated instance of {@linkplain $CLASS_NAME}.\n" +
                "\t * Since this field doesn't have predefined values, the validation is not necessary.\n" +
                "\t * @param value the field value. In general, the <em>null</em> value is allowed.\n" +
                "\t *              In this case method {@linkplain #getValue()} will return <em>null</em>,\n" +
                "\t *              method {@linkplain #getRawValue()} will return <em>null</em> and\n" +
                "\t * @return non-validated instance of {@linkplain $CLASS_NAME}.\n" +
                "\t */\n" +
                "\tpublic static $CLASS_NAME getInstance($TYPE value) {\n" +
                "\t\treturn new $CLASS_NAME(value, Validation.NONE);\n" +
                "\t}\n\n";
    }

    private static final String TEMPLATE_CLASS =
            "$PACKAGE\n" +
            "$IMPORT" +
            "\n" +
            "/**\n" +
            " * Field $CLASS_NAME\n" +
            " */\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "public class $CLASS_NAME extends $SUPERCLASS {\n" +
            "\tpublic static final String NAME = \"$CLASS_NAME\";\n" +
            "\tpublic static final int NUMBER = $NUMBER;\n" +
            "\n" +

            "$PREDEFINED_SECTION\n" +

            "$INSTANCE_BY_STRING\n" +
            "$INSTANCE_BY_TYPE\n" +
            "$INSTANCE_NO_VALIDATION" +
            "$CTOR_BY_STRING\n" +
            "$CTOR_BY_TYPE\n" +
            "$INSTANCE_BY_OBJECT\n" +
            "}";
}