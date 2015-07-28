package net.kem.tquickfix.blocks;

import net.kem.tquickfix.QFParser;
import net.kem.tquickfix.ThreadContext;

/**
 *
 */
public abstract class QFFloatField extends QFField<Float> {

    protected QFFloatField(String name, int number, String rawValue, Validation validateValue) {
        super(name, number, rawValue, validateValue);
    }

    protected QFFloatField(int number, String name, Float value, Validation validateValue) {
        super(number, name, value, validateValue);
    }

    @Override
    protected boolean calculate(CalculationDirection calculationDirection) {
        return calculate(this, calculationDirection);
    }

    protected static boolean calculate(QFFloatField instance, CalculationDirection calculationDirection) {
        boolean res;
        switch(calculationDirection) {
            case TO_RAW:
                if(instance.value != null) {
                    instance.rawValue = Float.toString(instance.value);
                    res = true;
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty Float parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty Float parameter.");
                    res = false;
                }
                break;
            case TO_VALUE:
                if(instance.rawValue != null && !instance.rawValue.isEmpty()) {
                    try {
                        instance.value = Float.parseFloat(instance.rawValue);
                        res = true;
                    } catch(NumberFormatException e) {
                        instance.logger.error("Calculation error in field " + instance.name + ". Invalid Float parameter " + instance.rawValue, e);
                        QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Invalid Float parameter " + instance.rawValue);
                        res = false;
                    }
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty Float parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty Float parameter.");
                    res = false;
                }
                break;
            default: res = false; // Won't happen.
        }
        return res;
    }

    protected static Float calculate(String rawValue) throws IllegalArgumentException {
        if(rawValue != null && !rawValue.isEmpty()) {
            try {
                return Float.parseFloat(rawValue);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Float value " + rawValue, e);
            }
        } else {
            throw new IllegalArgumentException("Empty value");
        }
    }
}

