package net.kem.newtquickfix.blocks;

/**
 * Created by Evgeny Kurtser on 12/23/2015 at 2:41 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public interface ValidationHandler<V> {

    ValidationHandler VALIDATION_HANDLER_SILENT_NUMBER = new ValidationHandler<Number>() {
        @Override
        public Number invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException {
            return problematicValue instanceof Number ? (Number) problematicValue : 0;
        }
    };

    ValidationHandler VALIDATION_HANDLER_WARNING_NUMBER = new ValidationHandler<Number>() {
        @Override
        public Number invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException {
            System.err.println("Value " + problematicValue + " is out of predefined values list of FIX tag " + cls.getSimpleName());
            return problematicValue instanceof Number ? (Number) problematicValue : 0;
        }
    };

    ValidationHandler VALIDATION_HANDLER_ERROR_NUMBER = new ValidationHandler<Number>() {
        @Override
        public Number invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException {
            if (t instanceof UnsupportedOperationException) {
                throw (UnsupportedOperationException) t;
            } else {
                throw new UnsupportedOperationException(t);
            }
        }
    };


    V invalidValue(Class<?> cls, Object problematicValue, Throwable t) throws UnsupportedOperationException;
}
