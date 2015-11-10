package net.kem.newtquickfix.blocks;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:15 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFDateField extends QFField<LocalDate> {
    protected static DateTimeFormatter getDateFormat() {
        return DateTimeFormatter.ofPattern("yyyyMMdd");
    }
}
