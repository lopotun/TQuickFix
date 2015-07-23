package com.traiana.tquickfix.blocks;

import com.traiana.tquickfix.QFParser;
import com.traiana.tquickfix.ThreadContext;

/**
 *
 */
public abstract class QFBooleanField extends QFField<Boolean> {

    protected QFBooleanField(String name, int number, String rawValue, Validation validateValue) {
        super(name, number, rawValue, validateValue);
    }

    protected QFBooleanField(int number, String name, Boolean value, Validation validateValue) {
        super(number, name, value, validateValue);
    }

    @Override
    protected boolean calculate(CalculationDirection calculationDirection) {
        return calculate(this, calculationDirection);
    }

    protected static boolean calculate(QFBooleanField instance, CalculationDirection calculationDirection) {
        boolean res;
        switch(calculationDirection) {
            case TO_RAW:
                if(instance.value != null) {
                    instance.rawValue = Boolean.toString(instance.value);
                    res = true;
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty Boolean parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty Boolean parameter.");
                    res = false;
                }
                break;

            case TO_VALUE:
                if(instance.rawValue != null && !instance.rawValue.isEmpty()) {
                    instance.value = Boolean.parseBoolean(instance.rawValue);
                    res = true;
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty Boolean parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty Boolean parameter.");
                    res = false;
                }
                break;
            default: res = false; // Won't happen.
        }
        return res;
    }

    protected static Boolean calculate(String rawValue) throws IllegalArgumentException {
        if(rawValue != null && !rawValue.isEmpty()) {
            return Boolean.parseBoolean(rawValue);
        } else {
            throw new IllegalArgumentException("Empty Boolean value");
        }
    }
}