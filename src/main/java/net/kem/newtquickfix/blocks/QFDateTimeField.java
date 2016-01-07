package net.kem.newtquickfix.blocks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:15 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFDateTimeField extends QFField<LocalDateTime> {
    protected static DateTimeFormatter getDateFormat() {
        return new DateTimeFormatterBuilder().appendPattern("yyyyMMdd-HH:mm:ss").appendFraction(ChronoField.MICRO_OF_SECOND, 0, 3, true).toFormatter();//return DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss.SSS");
    }

    @Override
    public void toFIXString(StringBuilder sb) {
        if ($fixString == null) {
            $fixString = getTag() + "=" + (value == null ? "" : getDateFormat().format(value)) + QFFieldUtils.FIELD_SEPARATOR;
        }
        sb.append($fixString);
    }
}
