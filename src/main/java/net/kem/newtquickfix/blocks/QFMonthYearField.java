package net.kem.newtquickfix.blocks;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:15 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFMonthYearField extends QFField<YearMonth> {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
			"[yyyyMM]"
			+ "[yyyyMMdd]"
			+ "[yyyyMMw]"
			);
    protected static DateTimeFormatter getDateFormat() {
	    return FORMATTER;
    }
}