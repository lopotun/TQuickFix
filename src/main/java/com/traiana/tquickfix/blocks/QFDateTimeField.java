package com.traiana.tquickfix.blocks;

import com.traiana.tquickfix.QFParser;
import com.traiana.tquickfix.ThreadContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 *
 */
public abstract class QFDateTimeField extends QFField<Date> {

    protected QFDateTimeField(String name, int number, String rawValue, Validation validateValue) {
        super(name, number, rawValue, validateValue);
    }

    protected QFDateTimeField(int number, String name, Date value, Validation validateValue) {
        super(number, name, value, validateValue);
    }

    protected static boolean calculate(QFDateTimeField instance, CalculationDirection calculationDirection) {
        boolean res;
        switch(calculationDirection) {
            case TO_RAW:
                if(instance.value != null) {
                    instance.rawValue = instance.getDateFormat().format(instance.value);
                    res = true;
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty date/time parameter " + instance.rawValue);
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty date/time parameter " + instance.rawValue);
                    res = false;
                }
                break;
            case TO_VALUE:
                if(instance.rawValue != null) {
                    try {
                        instance.value = instance.getDateFormat().parse(instance.rawValue);
                        res = true;
                    } catch(ParseException e) {
                        instance.logger.error("Calculation error in field " + instance.name + ". Invalid date/time parameter " + instance.rawValue, e);
                        QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Invalid date/time parameter " + instance.rawValue);
                        res = false;
                    }
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty date/time parameter " + instance.rawValue);
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty date/time parameter " + instance.rawValue);
                    res = false;
                }
                break;
            default: res = false; // Won't happen.
        }
        return res;
    }

    protected static Date calculate(String rawValue, DateFormat df) throws IllegalArgumentException {
        if(rawValue != null && !rawValue.isEmpty()) {
            try {
                return df.parse(rawValue);
            } catch(ParseException e) {
                throw new IllegalArgumentException("Invalid date/time value " + rawValue, e);
            }
        } else {
            throw new IllegalArgumentException("Empty value");
        }
    }

    protected DateFormat getDateFormat() {
        return QFParser.getInstance().getThreadContext().getDateTimeFormatter();//df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");//:S
    }

    @Override
    protected boolean calculate(CalculationDirection calculationDirection) {
        calculate(this, calculationDirection);
        return true;
    }
}