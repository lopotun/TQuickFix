package com.traiana.tquickfix.blocks;

import com.traiana.tquickfix.QFParser;
import com.traiana.tquickfix.ThreadContext;

/**
 *
 */
public abstract class QFStringField extends QFField<String> {
    protected QFStringField(int number, String name, String value, Validation validateValue) {
        super(number, name, value, validateValue);
    }

    @Override
    protected boolean calculate(CalculationDirection calculationDirection) {
        boolean res;
        switch(calculationDirection) {
            case TO_RAW:
                if(value != null) {
                    rawValue = value;
                    res = true;
                } else {
                    logger.error("Calculation error in field " + name + ". Empty string parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + name + ". Empty string parameter.");
                    res = true;
                }
                break;
            case TO_VALUE:
                if(rawValue != null) {
                    value = rawValue;
                    res = true;
                } else {
                    logger.error("Calculation error in field " + name + ". Empty string parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + name + ". Empty string parameter.");
                    res = false;
                }
                break;
            default: res = false; // Won't happen.
        }
        return res;
    }
}