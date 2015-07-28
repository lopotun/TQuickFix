package net.kem.tquickfix.blocks;

import net.kem.tquickfix.QFParser;
import net.kem.tquickfix.ThreadContext;

import java.math.BigDecimal;

/**
 *
 */
public abstract class QFBigDecimalField extends QFField<BigDecimal> {

    protected QFBigDecimalField(String name, int number, String rawValue, Validation validateValue) {
        super(name, number, rawValue, validateValue);
    }

    protected QFBigDecimalField(int number, String name, BigDecimal value, Validation validateValue) {
        super(number, name, value, validateValue);
    }

    @Override
    protected boolean calculate(CalculationDirection calculationDirection) {
        return calculate(this, calculationDirection);
    }

    protected static boolean calculate(QFBigDecimalField instance, CalculationDirection calculationDirection) {
        boolean res;
        switch(calculationDirection) {
            case TO_RAW:
                if(instance.value != null) {
                    instance.rawValue = instance.value.toString();
                    res = true;
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty BigDecimal parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty BigDecimal parameter");
                    res = false;
                }
                break;

            case TO_VALUE:
                if(instance.rawValue != null && !instance.rawValue.isEmpty()) {
                    try {
                        instance.value = new BigDecimal(instance.rawValue);
                        res = true;
                    } catch(NumberFormatException e) {
                        instance.logger.error("Calculation error in field " + instance.name + ". Invalid BigDecimal parameter " + instance.rawValue, e);
                        QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Invalid BigDecimal parameter " + instance.rawValue);
                        res = false;
                    }
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty BigDecimal parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty BigDecimal parameter.");
                    res = false;
                }
                break;
            default: res = false; // Won't happen.
        }
        return res;
    }

    protected static BigDecimal calculate(String rawValue) throws IllegalArgumentException {
        if(rawValue != null && !rawValue.isEmpty()) {
            try {
                return new BigDecimal(rawValue);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("Invalid BigDecimal value " + rawValue, e);
            }
        } else {
            throw new IllegalArgumentException("Empty BigDecimal value");
        }
    }
}

