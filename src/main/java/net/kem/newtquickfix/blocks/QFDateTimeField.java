package net.kem.newtquickfix.blocks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:15 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFDateTimeField extends QFField<LocalDateTime> {
    /*protected static DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyyMMdd-HH:mm:ss");//:S
    }*/
    protected static DateTimeFormatter getDateFormat() {
        return DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss.SSS");
    }

    @Override
    public void toFIXString(StringBuilder sb) {
        if ($fixString == null) {
            $fixString = getTag() + "=" + (value == null ? "" : getDateFormat().format(value)) + QFFieldUtils.FIELD_SEPARATOR;
        }
        sb.append($fixString);
    }
}
