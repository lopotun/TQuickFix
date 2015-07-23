package com.traiana.tquickfix.blocks;

import com.traiana.tquickfix.QFParser;
import com.traiana.tquickfix.ThreadContext;
import org.apache.log4j.Logger;

/**
 * Immutable presentation of FIX tag "key=value".
 *
 * @param <T> tag value type.
 */
public abstract class QFField<T> {
    /**
     * Validation policy.
     */
    public static enum Validation {
        /**
         * No validation is applied: mandatory fields may be missing or may contain illegal values.
         */
        NONE,
        /**
         * Validation is particularly applied: mandatory fields existence is checked, values illegality is checked.
         * The value, however, can be out of the predefined values scope (if any).
         */
        OMIT_PREDEFINED,
        /**
         * Validation is applied. : mandatory fields existence is checked, values illegality is checked.
         * The value must be in scope of the predefined values (if any).
         */
        FULL
    }

//    public static enum FieldValidity {VALID, NOT_PREDEFINED, INVALID}

    protected Logger logger;

    protected static enum CalculationDirection {TO_RAW, TO_VALUE}

    protected int number;
    protected String name;
    protected T value;
    protected String rawValue;
    protected String toFIXString;

    // Validation cache.
    // isValidated[X][]     indicates whether Validation.NONE, Validation.OMIT_PREDEFINED and Validation.FULL validation has been already done.
    // isValidated[X][Y]    contains result of Validation.X validation.
    protected boolean[][] isValidated;

    protected QFField(String name, int number, String rawValue, Validation validateValue) throws IllegalArgumentException {
        this(name, number, null, rawValue, validateValue);
    }

    protected QFField(int number, String name, T value, Validation validateValue) throws IllegalArgumentException {
        this(name, number, value, null, validateValue);
    }

    /**
     * Ctor
     *
     * @param name
     * @param number
     * @param value
     * @param rawValue
     * @param validateValue //     * @throws IllegalArgumentException if the supplied argument is illegal for this field type and validation is allowed.
     * @see Validation
     */
    private QFField(String name, int number, T value, String rawValue, Validation validateValue) throws IllegalArgumentException {
        logger = Logger.getLogger(this.getClass().getName());
        this.name = name;
        this.number = number;
        this.value = value;
        this.rawValue = rawValue;
        isValidated = new boolean[Validation.values().length][2];

		if(!isValid(validateValue)) {
			throw new IllegalArgumentException("The value " + (rawValue==null? value: rawValue) + " cannot be set to field " + name + " (tag " + number + ")");
		}
    }

    protected abstract boolean calculate(CalculationDirection calculationDirection);

    protected boolean isPredefined() {
        return true;
    }


    /**
     * Checks whether value of this QField is valid according to supplied validation rule.
     * @param validationRule    Validation rule. If <em>null</em> then the method returns <em>false</em> regardless the supplied validation rule.
     * @return <em>true</em> value of this QField is valid according to supplied validation rule.
     */
    public boolean isValid(Validation validationRule) {
        if(validationRule == null) {
            return false;
        }
        if(isValidated[validationRule.ordinal()][0]) {
            return isValidated[validationRule.ordinal()][1];
        }
        isValidated[validationRule.ordinal()][0] = true;
        switch(validationRule) {
            case FULL:
            case OMIT_PREDEFINED:
                boolean isPredefined = isPredefined();
                if(isPredefined) {
                    //isValidated[validateValue.ordinal()][1] = true;
                    fillValues(validationRule);
                    break;
                } else {
                    if(validationRule == Validation.FULL) {
                        QFParser.getInstance().getThreadContext().addError(ThreadContext.ErrorType.OUT_OF_PREDEFINED_SCOPE, "Value " + value + " is out of predefined values set of tag " + name + " (" + number + ")");
                        isValidated[validationRule.ordinal()][1] = false;
                        break;
                    }
                    fillValues(validationRule);
                }
                break;
            case NONE:
                isValidated[validationRule.ordinal()][1] = true;
                break;
        }
        return isValidated[validationRule.ordinal()][1];
    }
    private void fillValues(Validation validationRule) {
        if(value == null) {
            isValidated[validationRule.ordinal()][1] = calculate(CalculationDirection.TO_VALUE);
        } else {
            if(rawValue == null) {
                isValidated[validationRule.ordinal()][1] = calculate(CalculationDirection.TO_RAW);
            } else {
                isValidated[validationRule.ordinal()][1] = true;
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public T getValue() {
        if(value == null) {
            calculate(CalculationDirection.TO_VALUE);
        }
        return value;
    }

    public String getRawValue() {
        if(rawValue == null) {
            calculate(CalculationDirection.TO_RAW);
        }
        return rawValue;
    }

    public String toFIXString() {
        if(toFIXString == null) {
            toFIXString = number + "=" + getRawValue() + '\u0001';
        }
        return toFIXString;
    }

    public void toFIXString(StringBuilder sb) {
        sb.append(toFIXString());
    }

    @Override
    public String toString() {
        return name + " {" + number + "=" + rawValue + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(obj instanceof QFField) {
            QFField qfField = (QFField) obj;
            return number == qfField.number && // Same tag numbers AND
                    (
//                        (getValue() == null && qfField.getValue() == null) || // either both values are null OR
//                        (getValue() != null && qfField.getValue() != null && getValue().equals(qfField.getValue())) // both values are same.

                          // All the values are null OR
                          ((value == null && qfField.value == null) && // either both values are null OR
                          (rawValue == null && qfField.rawValue == null)) || // either both values are null OR
                          // Values are equal OR
                          (value != null && qfField.value != null && value.equals(qfField.value)) || // both values are same.
                          // Raw values are equal
                          (rawValue != null && qfField.rawValue != null && rawValue.equals(qfField.rawValue)) // both values are same.
                    );
        }
        return false;
    }

    @Override
    public int hashCode() {
        return number;
    }
}