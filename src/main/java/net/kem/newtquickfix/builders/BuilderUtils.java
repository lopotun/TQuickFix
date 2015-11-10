package net.kem.newtquickfix.builders;

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
    private static final Map<CharSequence, QFFieldBrick> FIX_TYPE_TO_JAVA_BUILDING_BRICK = new HashMap<>();

    static {
        final QFFieldBrick BrickString = new QFStringFieldBrick("QFField", String.class, null, null);
        final QFFieldBrick BrickCharacter = new QFCharacterFieldBrick("QFField", Character.class, null, "value.charAt(0)");
        final QFFieldBrick BrickBigDecimal = new QFFieldBrick("QFField", BigDecimal.class, "import java.math.BigDecimal;", "new BigDecimal(value)");
        final QFFieldBrick BrickInteger = new QFFieldBrick("QFField", Integer.class, null, "Integer.parseInt(value)");
        final QFFieldBrick BrickDouble = new QFFieldBrick("QFField", Double.class, null, "Double.parseDouble(value)");
        final QFFieldBrick BrickBoolean = new QFBooleanFieldBrick("QFField", Boolean.class, null, "Boolean.parseBoolean(value)");
        final QFFieldBrick BrickFloat = new QFFieldBrick("QFField", Float.class, null, "Float.parseFloat(value)");
        final QFFieldBrick BrickCurrency = new QFCurrencyFieldBrick("QFField", Currency.class, null, "java.util.Currency.getInstance(value)");

        final QFFieldBrick BrickDateTime = new QFDateFieldBrick("QFDateTimeField", LocalDateTime.class, "import java.time.LocalDateTime;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");
        final QFFieldBrick BrickDate = new QFDateFieldBrick("QFDateField", LocalDate.class, "import java.time.LocalDate;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");
        final QFFieldBrick BrickMonthYear = new QFDateFieldBrick("QFMonthYearField", LocalDate.class, "import java.time.LocalDate;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");
        final QFFieldBrick BrickTime = new QFDateFieldBrick("QFTimeField", LocalTime.class, "import java.time.LocalTime;\nimport java.time.format.DateTimeParseException;", "getDateFormat().parse(value)");

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
        FIX_TYPE_TO_JAVA_BUILDING_BRICK.put("MONTH-YEAR", BrickMonthYear); // In FIX v5.0 only.
    }

    public static QFFieldBrick getJavaSourceFieldBuildingBrick(CharSequence fieldType) {
        return FIX_TYPE_TO_JAVA_BUILDING_BRICK.get(fieldType);
    }
}
