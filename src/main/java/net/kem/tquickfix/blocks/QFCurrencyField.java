package net.kem.tquickfix.blocks;

import net.kem.tquickfix.QFParser;
import net.kem.tquickfix.ThreadContext;

import java.util.Currency;

/**
 *
 */
public abstract class QFCurrencyField extends QFField<Currency> {

    protected QFCurrencyField(String name, int number, String rawValue, Validation validateValue) {
        super(name, number, rawValue, validateValue);
    }

    protected QFCurrencyField(int number, String name, Currency value, Validation validateValue) {
        super(number, name, value, validateValue);
    }

    @Override
    protected boolean calculate(CalculationDirection calculationDirection) {
        return calculate(this, calculationDirection);
    }

    protected static boolean calculate(QFCurrencyField instance, CalculationDirection calculationDirection) {
        boolean res;
        switch(calculationDirection) {
            case TO_RAW:
                if(instance.value != null) {
                    instance.rawValue = instance.value.toString();
                    res = true;
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty Currency parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty Currency parameter.");
                    res = false;
                }
                break;
            case TO_VALUE:
                if(instance.rawValue != null && !instance.rawValue.isEmpty()) {
                    try {
                        instance.value = Currency.getInstance(instance.rawValue);
                        res = true;
                    } catch(IllegalArgumentException e) {
                        instance.logger.error("Calculation error in field " + instance.name + ". Invalid Currency parameter " + instance.rawValue, e);
                        QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Invalid Currency parameter " + instance.rawValue);
                        res = false;
                    }
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty Currency parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty Currency parameter.");
                    res = false;
                }
                break;
            default: res = false; // Won't happen.
        }
        return res;
    }

    protected static Currency calculate(String rawValue) throws IllegalArgumentException {
        if(rawValue != null && !rawValue.isEmpty()) {
            try {
                return Currency.getInstance(rawValue);
            } catch(IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid Currency value " + rawValue, e);
            }
        } else {
            throw new IllegalArgumentException("Empty value");
        }
    }
}

