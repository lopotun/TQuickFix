package net.kem.newtquickfix.blocks;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:15 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFTimeField extends QFField<LocalTime> {
    /*protected static DateFormat getDateFormat() {
        return new SimpleDateFormat("HH:mm:ss:S");
    }*/
    protected static DateTimeFormatter getDateFormat() {
        return DateTimeFormatter.ofPattern("HH:mm:ss:S");
    }
}
