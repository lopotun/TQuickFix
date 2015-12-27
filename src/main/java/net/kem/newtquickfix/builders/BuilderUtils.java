package net.kem.newtquickfix.builders;

import net.kem.newtquickfix.blocks.QFMember;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:44 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class BuilderUtils {
    public static final CharSequence PACKAGE_NAME = "net.kem.newtquickfix.";
    public static final CharSequence PACKAGE_NAME_BLOCKS = PACKAGE_NAME + "blocks.";

    public static CharSequence FIX_VERSION = "";
    public static CharSequence PACKAGE_NAME_COMPONENTS = PACKAGE_NAME + "components";
    public static CharSequence PACKAGE_NAME_FIELDS = PACKAGE_NAME + "fields";
    public static CharSequence PACKAGE_NAME_MESSAGES = PACKAGE_NAME + "messages";

    public static final Map<CharSequence, CharSequence> COMPONENTS_FIRST_FIELD = new HashMap<>(64);

    public static class QFFieldBlockDef {
        protected final Class thisClass;
        protected final String parentClassName;
        protected final Class typeClass;
        protected final CharSequence importLine;
        protected final CharSequence typeToStringConversion;

        public QFFieldBlockDef(Class thisClass, String parentClassName, Class typeClass, CharSequence importLine, CharSequence typeToStringConversion) {
            this.thisClass = thisClass;
            this.parentClassName = parentClassName;
            this.typeClass = typeClass;
            this.importLine = importLine;
            this.typeToStringConversion = typeToStringConversion;
        }
    }

    //    private static final Map<CharSequence, QFFieldElement> FIX_TYPE_TO_JAVA_BUILDING_BRICK = new HashMap<>();
    private static final Map<CharSequence, QFFieldBlockDef> FIX_TYPE_TO_JAVA_BUILDING_BRICK1 = new HashMap<>();

    static {
        final QFFieldBlockDef BrickString1 = new QFFieldBlockDef(QFStringFieldElement.class, "QFField", String.class, null, null);
        final QFFieldBlockDef BrickCharacter1 = new QFFieldBlockDef(QFCharacterFieldElement.class, "QFField", Character.class, null, "value.charAt(0)");
        final QFFieldBlockDef BrickBigDecimal1 = new QFFieldBlockDef(QFFieldElement.class, "QFField", BigDecimal.class, "import java.math.BigDecimal;", "new BigDecimal(value)");
        final QFFieldBlockDef BrickInteger1 = new QFFieldBlockDef(QFFieldElement.class, "QFField", Integer.class, null, "Integer.parseInt(value)");
        final QFFieldBlockDef BrickDouble1 = new QFFieldBlockDef(QFFieldElement.class, "QFField", Double.class, null, "Double.parseDouble(value)");
        final QFFieldBlockDef BrickBoolean1 = new QFFieldBlockDef(QFBooleanFieldElement.class, "QFField", Boolean.class, null, "Boolean.parseBoolean(value)");
        final QFFieldBlockDef BrickFloat1 = new QFFieldBlockDef(QFFieldElement.class, "QFField", Float.class, null, "Float.parseFloat(value)");
        final QFFieldBlockDef BrickCurrency1 = new QFFieldBlockDef(QFCurrencyFieldElement.class, "QFField", Currency.class, null, "java.util.Currency.getInstance(value)");

        final QFFieldBlockDef BrickDateTime1 = new QFFieldBlockDef(QFDateFieldElement.class, "QFDateTimeField", LocalDateTime.class, "import java.time.LocalDateTime;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");
        final QFFieldBlockDef BrickDate1 = new QFFieldBlockDef(QFDateFieldElement.class, "QFDateField", LocalDate.class, "import java.time.LocalDate;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");
        final QFFieldBlockDef BrickMonthYear1 = new QFFieldBlockDef(QFDateFieldElement.class, "QFMonthYearField", LocalDate.class, "import java.time.LocalDate;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");
        final QFFieldBlockDef BrickTime1 = new QFFieldBlockDef(QFDateFieldElement.class, "QFTimeField", LocalTime.class, "import java.time.LocalTime;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");

        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("STRING", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("MULTIPLECHARVALUE", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("EXCHANGE", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("DATA", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("LOCALMKTDATE", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("MONTHYEAR", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("MULTIPLESTRINGVALUE", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("MULTIPLEVALUESTRING", BrickString1); // In FIX v4.4 only.
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("COUNTRY", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("TZTIMEONLY", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("TZTIMESTAMP", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("XMLDATA", BrickString1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("LANGUAGE", BrickString1);

        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("CHAR", BrickCharacter1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("PRICE", BrickBigDecimal1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("CURRENCY", BrickCurrency1);

        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("LENGTH", BrickInteger1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("NUMINGROUP", BrickInteger1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("SEQNUM", BrickInteger1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("INT", BrickInteger1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("DAY-OF-MONTH", BrickInteger1); // In FIX v5.0 only.

        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("AMT", BrickDouble1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("QTY", BrickDouble1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("PERCENTAGE", BrickDouble1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("PRICEOFFSET", BrickDouble1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("BOOLEAN", BrickBoolean1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("FLOAT", BrickFloat1);

        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("UTCTIMESTAMP", BrickDateTime1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("UTCDATEONLY", BrickDate1); // In FIX v4.0 only.
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("DATE", BrickDate1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("UTCTIMEONLY", BrickTime1);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("TIME", BrickTime1); // In FIX v4.0 only.
        FIX_TYPE_TO_JAVA_BUILDING_BRICK1.put("MONTH-YEAR", BrickMonthYear1); // In FIX v5.0 only.
        
        

        /*final QFFieldElement BrickString = new QFStringFieldElement("QFField", String.class, null, null);
        final QFFieldElement BrickCharacter = new QFCharacterFieldElement("QFField", Character.class, null, "value.charAt(0)");
        final QFFieldElement BrickBigDecimal = new QFFieldElement("QFField", BigDecimal.class, "import java.math.BigDecimal;", "new BigDecimal(value)");
        final QFFieldElement BrickInteger = new QFFieldElement("QFField", Integer.class, null, "Integer.parseInt(value)");
        final QFFieldElement BrickDouble = new QFFieldElement("QFField", Double.class, null, "Double.parseDouble(value)");
        final QFFieldElement BrickBoolean = new QFBooleanFieldElement("QFField", Boolean.class, null, "Boolean.parseBoolean(value)");
        final QFFieldElement BrickFloat = new QFFieldElement("QFField", Float.class, null, "Float.parseFloat(value)");
        final QFFieldElement BrickCurrency = new QFCurrencyFieldElement("QFField", Currency.class, null, "java.util.Currency.getInstance(value)");

        final QFFieldElement BrickDateTime = new QFDateFieldElement("QFDateTimeField", LocalDateTime.class, "import java.time.LocalDateTime;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");
        final QFFieldElement BrickDate = new QFDateFieldElement("QFDateField", LocalDate.class, "import java.time.LocalDate;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");
        final QFFieldElement BrickMonthYear = new QFDateFieldElement("QFMonthYearField", LocalDate.class, "import java.time.LocalDate;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");
        final QFFieldElement BrickTime = new QFDateFieldElement("QFTimeField", LocalTime.class, "import java.time.LocalTime;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");

        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("STRING", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("MULTIPLECHARVALUE", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("EXCHANGE", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("DATA", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("LOCALMKTDATE", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("MONTHYEAR", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("MULTIPLESTRINGVALUE", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("MULTIPLEVALUESTRING", BrickString); // In FIX v4.4 only.
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("COUNTRY", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("TZTIMEONLY", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("TZTIMESTAMP", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("XMLDATA", BrickString);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("LANGUAGE", BrickString);

        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("CHAR", BrickCharacter);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("PRICE", BrickBigDecimal);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("CURRENCY", BrickCurrency);

        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("LENGTH", BrickInteger);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("NUMINGROUP", BrickInteger);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("SEQNUM", BrickInteger);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("INT", BrickInteger);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("DAY-OF-MONTH", BrickInteger); // In FIX v5.0 only.

        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("AMT", BrickDouble);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("QTY", BrickDouble);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("PERCENTAGE", BrickDouble);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("PRICEOFFSET", BrickDouble);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("BOOLEAN", BrickBoolean);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("FLOAT", BrickFloat);

        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("UTCTIMESTAMP", BrickDateTime);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("UTCDATEONLY", BrickDate); // In FIX v4.0 only.
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("DATE", BrickDate);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("UTCTIMEONLY", BrickTime);
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("TIME", BrickTime); // In FIX v4.0 only.
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("MONTH-YEAR", BrickMonthYear); // In FIX v5.0 only.*/
    }

    /*public static QFFieldElement getJavaSourceFieldBuildingBrick(CharSequence fieldType) {
        return FIX_TYPE_TO_JAVA_BUILDING_BRICK.get(fieldType);
    }*/

    public static QFFieldBlockDef getJavaSourceFieldBuildingBrick1(CharSequence fieldType) {
        return FIX_TYPE_TO_JAVA_BUILDING_BRICK1.get(fieldType);
    }

    /*public static QFElement getQFMemberBrick(Element startElement, StringBuilder sb) {
        switch (QFMember.Type.valueOf(startElement.getTagName().toUpperCase())) {
            case FIELD:
                return new QFFieldElement();
            case GROUP:
                return new QFGroupBrick(startElement, sb);
            case COMPONENT:
                return new QFComponentElement(startElement, sb);
            default: return null;
        }
    }*/

    public static QFRequirable getQFRequirable(Element startElement, StringBuilder sb, CharSequence ident, QFElement parent) {
        QFMember.Type type = QFMember.Type.valueOf(startElement.getTagName().toUpperCase());
        switch (type) {
            case FIELD:
                return new QFFieldBrick(startElement, sb, ident);
            case COMPONENT:
                return new QFComponentBrick(startElement, sb, ident);
            case GROUP:
                return new QFGroupBrick(startElement, sb, ident, parent);
            case HEADER:
                return new QFComponentBrick(startElement, sb, ident);
            case TRAILER:
                return new QFComponentBrick(startElement, sb, ident);
            default: return null;
        }
    }

}
