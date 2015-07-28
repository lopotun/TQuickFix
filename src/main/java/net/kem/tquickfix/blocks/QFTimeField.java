package net.kem.tquickfix.blocks;

import net.kem.tquickfix.QFParser;

import java.text.DateFormat;
import java.util.Date;

/**
 *
 */
public abstract class QFTimeField extends QFDateTimeField {

    protected QFTimeField(String name, int number, String rawValue, Validation validateValue) {
        super(name, number, rawValue, validateValue);
    }

    protected QFTimeField(int number, String name, Date value, Validation validateValue) {
        super(number, name, value, validateValue);
    }

    protected DateFormat getDateFormat() {
        return QFParser.getInstance().getThreadContext().getTimeFormatter();//df = new SimpleDateFormat("HH:mm:ss:S");
    }
}