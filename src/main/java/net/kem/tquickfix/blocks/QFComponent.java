package net.kem.tquickfix.blocks;

import net.kem.tquickfix.QFParser;
import net.kem.tquickfix.ThreadContext;
import net.kem.tquickfix.builder.QFBuilderConfig;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Abstract presentation of FIX element (component, group, message).
 * User: EvgenyK
 * Date: 10/27/14
 * Time: 11:18 AM
 */


public abstract class QFComponent {
    public enum ContainResult {HAS_THIS, HAS_DESCENDANT, DOESNT_HAVE}

    protected Logger logger;
    protected String name;

    public abstract boolean isValid(QFField.Validation validation);

    public abstract String toFIXString();
//	public abstract boolean containsField(int tag);

    protected QFComponent(String name) {
        this(name, null);
    }
    protected QFComponent(String name, QFBuilderConfig config) {
        logger = Logger.getLogger(this.getClass().getName());
        this.name = name;

        if(config == null) {
            config = QFBuilderConfig.getDefaultInstance();
        }
        parseValidations = config.getParseValidations(getClass().getName());
        fieldValidations = config.getFieldValidations(getClass().getName());
    }

    protected Map<CharSequence, QFField.Validation> parseValidations;
    protected QFField.Validation getParseFieldValidation(CharSequence fieldName) {
        return getFieldValidation(fieldName, parseValidations, QFField.Validation.NONE);
    }
    protected Map<CharSequence, QFField.Validation> fieldValidations;
    protected QFField.Validation getFieldValidation(CharSequence fieldName, QFField.Validation defaultValue) {
        return getFieldValidation(fieldName, fieldValidations, defaultValue);
    }
    private QFField.Validation getFieldValidation(CharSequence fieldName, Map<CharSequence, QFField.Validation> validations, QFField.Validation defaultValue) {
        if(validations == null) {
            return defaultValue;
        }
        QFField.Validation v = validations.get(fieldName);
        return v==null? defaultValue: v;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(obj instanceof QFComponent) {
            QFComponent qfComponent = (QFComponent) obj;
            return name.equals(qfComponent.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }




    /**
     * Adds an error of the given type to list of errors. This method is called internally by the framework
     * and usually should not be called by an end user.
     * @param errorType
     * @param error
     */
    public static void addError(ThreadContext.ErrorType errorType, CharSequence error) {
        QFParser.getInstance().getThreadContext().addError(errorType, error);
    }

    /**
     * Returns error list of the given error type.
     * @param errorType
     * @return
     */
    public List<CharSequence> getErrors(ThreadContext.ErrorType errorType) {
        return QFParser.getInstance().getThreadContext().getErrors(errorType);
    }

    /**
     * Returns all detected errors as list.
     * @return
     */
    public List<CharSequence> getErrors() {
        return QFParser.getInstance().getThreadContext().getErrors();
    }

    /**
     * Returns all detected errors separated by error type.
     * @return
     */
    public Map<ThreadContext.ErrorType, List<CharSequence>> getAllErrors() {
        return QFParser.getInstance().getThreadContext().getAllErrors();
    }

    /**
     * Checks whether an error had occurred while message parsing/validation.
     * @return <em>true</em> if an error had occurred while message parsing/validation.
     */
    public boolean hasErrors() {
        return QFParser.getInstance().getThreadContext().hasErrors();
    }

    /**
     * Checks whether an error of the given error type had occurred while message parsing/validation.
     * @param errorType
     * @return
     */
    public boolean hasErrors(ThreadContext.ErrorType errorType) {
        return QFParser.getInstance().getThreadContext().hasErrors(errorType);
    }
}