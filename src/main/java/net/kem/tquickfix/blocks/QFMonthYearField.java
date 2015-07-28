package net.kem.tquickfix.blocks;

import net.kem.tquickfix.QFParser;

import java.text.DateFormat;
import java.util.Date;

/**
 *
 */
public abstract class QFMonthYearField extends QFDateTimeField {

    protected QFMonthYearField(String name, int number, String rawValue, Validation validateValue) {
        super(name, number, rawValue, validateValue);
    }

    protected QFMonthYearField(int number, String name, Date value, Validation validateValue) {
        super(number, name, value, validateValue);
    }

    protected DateFormat getDateFormat() {
        return QFParser.getInstance().getThreadContext().getMonthYearFormatter();//df = new SimpleDateFormat("yyyyMM");
    }
}