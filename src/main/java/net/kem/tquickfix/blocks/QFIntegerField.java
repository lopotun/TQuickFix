package net.kem.tquickfix.blocks;

import net.kem.tquickfix.QFParser;
import net.kem.tquickfix.ThreadContext;

/**
 *
 */
public abstract class QFIntegerField extends QFField<Integer> {

    protected QFIntegerField(String name, int number, String rawValue, Validation validateValue) throws IllegalArgumentException {
        super(name, number, rawValue, validateValue);
    }

    protected QFIntegerField(int number, String name, Integer value, Validation validateValue) throws IllegalArgumentException {
        super(number, name, value, validateValue);
    }

    @Override
    protected boolean calculate(CalculationDirection calculationDirection) {
        return calculate(this, calculationDirection);
    }

    protected static boolean calculate(QFIntegerField instance, CalculationDirection calculationDirection) {
        boolean res;
        switch(calculationDirection) {
            case TO_RAW:
                if(instance.value != null) {
                    instance.rawValue = Integer.toString(instance.value);
                    res = true;
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty integer parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty integer parameter.");
                    res = false;
                }
                break;
            case TO_VALUE:
                if(instance.rawValue != null && !instance.rawValue.isEmpty()) {
                    try {
                        instance.value = Integer.parseInt(instance.rawValue);
                        res = true;
                    } catch(NumberFormatException e) {
                        instance.logger.error("Calculation error in field " + instance.name + ". Invalid integer parameter " + instance.rawValue, e);
                        QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Invalid integer parameter " + instance.rawValue);
                        res = false;
                    }
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty integer parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty integer parameter.");
                    res = false;
                }
                break;
            default: res = false; // Won't happen.
        }
        return res;
    }

    protected static Integer calculate(String rawValue) throws IllegalArgumentException {
        if(rawValue != null && !rawValue.isEmpty()) {
            try {
                return Integer.parseInt(rawValue);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("Invalid integer value " + rawValue, e);
            }
        } else {
            throw new IllegalArgumentException("Empty integer value");
        }
    }
}