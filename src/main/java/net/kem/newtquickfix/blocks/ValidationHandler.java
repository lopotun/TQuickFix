package net.kem.newtquickfix.blocks;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

/**
 * Created by Evgeny Kurtser on 12/23/2015 at 2:41 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public interface ValidationHandler<V> {

    interface Numbers {
        ValidationHandler VALIDATION_HANDLER_SILENT = new ValidationHandler<Number>() {
            @Override
            public Number invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException {
                return problematicValue instanceof Number ? (Number) problematicValue : 0;
            }
        };

        ValidationHandler VALIDATION_HANDLER_WARNING = new ValidationHandler<Number>() {
            @Override
            public Number invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException {
                if(t == null) {
                    System.err.println("Value " + problematicValue + " is out of predefined values list of FIX tag " + cls.getSimpleName());
                } else {
                    System.err.println("\"" + problematicValue + "\" cannot be used as value of FIX tag " + cls.getSimpleName() + " due to error " + t.getClass().getSimpleName() + ": " + t.getMessage());
                }
                return problematicValue instanceof Number ? (Number) problematicValue : 0;
            }
        };

        ValidationHandler VALIDATION_HANDLER_ERROR = new ValidationHandler<Number>() {
            @Override
            public Number invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException {
                if (t instanceof UnsupportedOperationException) {
                    throw (UnsupportedOperationException) t;
                } else {
                    throw new UnsupportedOperationException(t);
                }
            }
        };
    }

    interface Temporals<V extends Temporal> {
        ValidationHandler VALIDATION_HANDLER_SILENT = new ValidationHandler<LocalDateTime>() {
            @Override
            public LocalDateTime invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException {
                return LocalDateTime.now();
            }
        };

        ValidationHandler VALIDATION_HANDLER_WARNING = new ValidationHandler<LocalDateTime>() {
            @Override
            public LocalDateTime invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException {
                if(t == null) {
                    System.err.println("Value " + problematicValue + " is out of predefined values list of FIX tag " + cls.getSimpleName());
                } else {
                    System.err.println("\"" + problematicValue + "\" cannot be used as value of FIX tag " + cls.getSimpleName() + " due to error " + t.getClass().getSimpleName() + ": " + t.getMessage());
                }
                return LocalDateTime.now();
            }
        };

        ValidationHandler VALIDATION_HANDLER_ERROR = new ValidationHandler<LocalDateTime>() {
            @Override
            public LocalDateTime invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException {
                if (t instanceof UnsupportedOperationException) {
                    throw (UnsupportedOperationException) t;
                } else {
                    throw new UnsupportedOperationException(t);
                }
            }
        };
    }


    V invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException;
}
