package net.kem.newtquickfix;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

/**
 * Created by Evgeny Kurtser on 12/23/2015 at 2:41 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public interface ValidationErrorsHandler<V> {

    enum ErrorType {PARSING, NOT_PREDEFINED}

    interface Numbers {
        ValidationErrorsHandler VALIDATION_HANDLER_SILENT = new ValidationErrorsHandler<Number>() {
            @Override
            public Number invalidValue(Class<?> cls, Object problematicValue, Throwable t, ErrorType errorType) throws UnsupportedOperationException {
                return problematicValue instanceof Number ? (Number) problematicValue : 0;
            }
        };

        ValidationErrorsHandler VALIDATION_HANDLER_WARNING = new ValidationErrorsHandler<Number>() {
            @Override
            public Number invalidValue(Class<?> cls, Object problematicValue, Throwable t, ErrorType errorType) throws UnsupportedOperationException {
                switch (errorType) {
                    case NOT_PREDEFINED: {
                        System.err.println("Value " + problematicValue + " is out of predefined values list of FIX tag " + cls.getSimpleName());
                    } break;
                    case PARSING: {
                        if(t == null) {
                            System.err.println("Empty value shouldn't be used as value of FIX tag \"" + cls.getSimpleName() + "\"");
                        } else {
                            System.err.println("\"" + problematicValue + "\" cannot be used as value of FIX tag " + cls.getSimpleName() + " due to error " + t.getClass().getSimpleName() + ": " + t.getMessage());
                        }
                    }
                }
                return problematicValue instanceof Number ? (Number) problematicValue : 0;
            }
        };

        ValidationErrorsHandler VALIDATION_HANDLER_ERROR = new ValidationErrorsHandler<Number>() {
            @Override
            public Number invalidValue(Class<?> cls, Object problematicValue, Throwable t, ErrorType errorType) throws UnsupportedOperationException {
                if (t instanceof UnsupportedOperationException) {
                    throw (UnsupportedOperationException) t;
                } else {
                    throw new UnsupportedOperationException(t);
                }
            }
        };
    }

    interface Temporals<V extends Temporal> {
        ValidationErrorsHandler VALIDATION_HANDLER_SILENT = (cls, problematicValue, t, errorType) -> LocalDateTime.now();

        ValidationErrorsHandler VALIDATION_HANDLER_WARNING = (cls, problematicValue, t, errorType) -> {
            switch (errorType) {
                case NOT_PREDEFINED: {
                    System.err.println("Value " + problematicValue + " is out of predefined values list of FIX tag " + cls.getSimpleName());
                } break;
                case PARSING: {
                    System.err.println("\"" + problematicValue + "\" cannot be used as value of FIX tag " + cls.getSimpleName() + " due to error " + t.getClass().getSimpleName() + ": " + t.getMessage());
                }
            }
            return LocalDateTime.now();
        };

        ValidationErrorsHandler VALIDATION_HANDLER_ERROR = (cls, problematicValue, t, errorType) -> {
            if (t instanceof UnsupportedOperationException) {
                throw (UnsupportedOperationException) t;
            } else {
                throw new UnsupportedOperationException(t);
            }
        };
    }

    /**
     * This method is called by a:
     *  (a) LiteFix Field when value of this field is not one of the its predefined values. In this case 'errorType' is {@linkplain ErrorType#NOT_PREDEFINED};
     *  (b) LiteFix Field when value of this field cannot be parsed to field type (e.g. when value "abcd" is given to an Integer or a Date/Time field). In this case 'errorType' is {@linkplain ErrorType#PARSING};
     * @param cls                 in case of
     *  (a):
     * @param problematicValue
     * @param t
     * @param errorType
     * @return
     * @throws UnsupportedOperationException
     */
    V invalidValue(Class<?> cls, Object problematicValue, Throwable t, ErrorType errorType) throws UnsupportedOperationException;//TODO rename to invalidFieldValue
}
