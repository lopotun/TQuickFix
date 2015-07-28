package net.kem.tquickfix.blocks;

import net.kem.tquickfix.QFParser;
import net.kem.tquickfix.ThreadContext;

/**
 *
 */
public abstract class QFDoubleField extends QFField<Double> {

    protected QFDoubleField(String name, int number, String rawValue, Validation validateValue) {
        super(name, number, rawValue, validateValue);
    }

    protected QFDoubleField(int number, String name, Double value, Validation validateValue) {
        super(number, name, value, validateValue);
    }

    @Override
    protected boolean calculate(CalculationDirection calculationDirection) {
        return calculate(this, calculationDirection);
    }

    protected static boolean calculate(QFDoubleField instance, CalculationDirection calculationDirection) {
        boolean res;
        switch(calculationDirection) {
            case TO_RAW:
                if(instance.value != null) {
                    instance.rawValue = Double.toString(instance.value);
                    res = true;
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty double parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty double parameter.");
                    res = false;
                }
                break;
            case TO_VALUE:
                if(instance.rawValue != null && !instance.rawValue.isEmpty()) {
                    try {
                        instance.value = Double.parseDouble(instance.rawValue);
                        res = true;
                    } catch(NumberFormatException e) {
                        instance.logger.error("Calculation error in field " + instance.name + ". Invalid double parameter " + instance.rawValue, e);
                        QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Invalid double parameter " + instance.rawValue);
                        res = false;
                    }
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty double parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty double parameter.");
                    res = false;
                }
                break;
            default: res = false; // Won't happen.
        }
        return res;
    }

    protected static Double calculate(String rawValue) throws IllegalArgumentException {
        if(rawValue != null && !rawValue.isEmpty()) {
            try {
                return Double.parseDouble(rawValue);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("Invalid double value " + rawValue, e);
            }
        } else {
            throw new IllegalArgumentException("Empty double value");
        }
    }
}

