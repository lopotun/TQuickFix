package net.kem.tquickfix.blocks;

import net.kem.tquickfix.QFParser;
import net.kem.tquickfix.ThreadContext;

/**
 *
 */
public abstract class QFCharacterField extends QFField<Character> {

    protected QFCharacterField(String name, int number, String rawValue, Validation validateValue) {
        super(name, number, rawValue, validateValue);
    }

    protected QFCharacterField(int number, String name, Character value, Validation validateValue) {
        super(number, name, value, validateValue);
    }

    @Override
    protected boolean calculate(CalculationDirection calculationDirection) {
        return calculate(this, calculationDirection);
    }

    protected static boolean calculate(QFCharacterField instance, CalculationDirection calculationDirection) {
        boolean res;
        switch(calculationDirection) {
            case TO_RAW:
                if(instance.value != null) {
                    instance.rawValue = Character.toString(instance.value);
                    res = true;
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Empty Character parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Empty Character parameter " + instance.rawValue);
                    res = false;
                }
                break;
            case TO_VALUE:
                if(instance.rawValue != null && instance.rawValue.length() == 1) {
                    instance.value = instance.rawValue.charAt(0);
                    res = true;
                } else {
                    instance.logger.error("Calculation error in field " + instance.name + ". Invalid Character parameter.");
                    QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.ILLEGAL_VALUE, "Calculation error in field " + instance.name + ". Invalid Character parameter " + instance.rawValue);
                    res = false;
                }
                break;
            default: res = false; // Won't happen.
        }
        return res;
    }

    protected static Character calculate(String rawValue) throws IllegalArgumentException {
        if(rawValue != null && rawValue.length() == 1) {
            return rawValue.charAt(0);
        } else {
            throw new IllegalArgumentException("Invalid Character parameter " + rawValue);
        }
    }
}