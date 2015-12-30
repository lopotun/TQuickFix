package net.kem.newtquickfix;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFMessage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Currency;
import java.util.List;

/**
 * Created by Evgeny Kurtser on 12/23/2015 at 2:41 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFComponentValidator {
    /**
     * This method is called by a LiteFix Component validation mechanism to allow custom validation for this Component.
     * Validates the supplied LiteFix Component: {@link QFMessage} and {@link QFComponent}.
     * This implementation returns <em>null</em> meaning that it does not interfere with default validation mechanism.
     * If an implementing class provides its own validation mechanism for the supplied LiteFix Component then this method must eventually return either <em>true</em> or <em>false</em>.
     *
     * @param thisComponent LiteFix Component being validated.
     * @return <em>true</em> if this validator has performed the component custom validation and found this component <strong>valid</strong>;
     * <em>false</em> if this validator has performed the component custom validation and found this component <strong>invalid</strong>;
     * <em>null</em> if there is no custom validation. In this case the default validation of the supplied component will be used.
     * @see ValidationErrorsHandler#invalidValue(Class, Object, Throwable, ValidationErrorsHandler.ErrorType)
     */
    @Nullable
    public Boolean validateComponent(@SuppressWarnings("unused") QFComponent thisComponent) {
        return null;
    }

    /**
     * *  (a) LiteFix Component when mandatory field is missing in this Component. In this case 'errorType' is {@linkplain net.kem.newtquickfix.ValidationErrorsHandler.ErrorType#MISSING};
     *
     * @param thisComponent
     * @param missingElement
     */
    public void mandatoryElementMissing(QFComponent thisComponent, Class<?> missingElement) {
    }

    /**
     * This method is called while FIX data parsing when tag 'qfField' has been already present previously by the 'currentValue' in component 'component'
     *
     * @param qfField      duplicated tag.
     * @param currentValue current tag.
     * @param component    component that contains the tag
     */
    public void duplicatedTag(QFField qfField, QFField currentValue, QFComponent component) {
    }

    public void duplicatedComponent(QFComponent childrenComponentInstance, QFComponent thisInstance) {
    }

    public void invalidGroupCount(QFField qfField, List<? extends QFComponent> groupInstances, QFComponent thisInstance) {
    }

    /**
     * This method is called while FIX data parsing when tag 'unknownTag' is recognized neither by 'message' not by any of its components.
     *
     * @param unknownTag unknown tag.
     * @param message    message being parsed.
     */
    public void unknownTag(QFField unknownTag, QFMessage message) {
    }

    /**
     * This method is called by a:
     * (a) LiteFix Field when value of this field is not one of the its predefined values. In this case 'errorType' is {@linkplain net.kem.newtquickfix.ValidationErrorsHandler.ErrorType#NOT_PREDEFINED};
     * (b) LiteFix Field when value of this field cannot be parsed to field type (e.g. when value "abcd" is given to an Integer or a Date/Time field). In this case 'errorType' is {@linkplain net.kem.newtquickfix.ValidationErrorsHandler.ErrorType#PARSING};
     *
     * @param fieldClass       in case of
     *                         (a):
     * @param problematicValue
     * @param t
     * @return
     * @throws UnsupportedOperationException
     */
    public <V> V invalidFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull CharSequence problematicValue, @Nullable Throwable t) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Invalid value " + problematicValue + " in field " + fieldClass.getSimpleName());
    }

    public <V> V notPredefinedFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull V problematicValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Value " + problematicValue + " is not pre-defined in field " + fieldClass.getSimpleName());
    }

    protected <V> V getDefaultValue(Class<V> typeClass) {
        Object res;
        switch (typeClass.getSimpleName()) {
            case "String": {
                res = "";
            }
            break;
            case "Integer": {
                res = 0;
            }
            break;
            case "BigDecimal": {
                res = BigDecimal.ZERO;
            }
            break;
            case "Character": {
                res = '?';
            }
            break;
            case "Float": {
                res = 0.0f;
            }
            break;
            case "Currency": {
                res = Currency.getInstance("US");
            }
            break;
            case "Double": {
                res = 0;
            }
            break;
            case "Boolean": {
                res = false;
            }
            break;
            case "LocalDateTime": {
                res = LocalDateTime.now();
            }
            break;
            case "LocalDate": {
                res = LocalDate.now();
            }
            break;
            case "LocalTime": {
                res = LocalTime.now();
            }
            break;
            default:
                res = null;
        }
        return (V) res;
    }


    private static QFComponentValidator DEFAULT_COMPONENT_VALIDATOR = new QFComponentValidator() {
        @Override
        public void mandatoryElementMissing(@SuppressWarnings("unused") QFComponent thisComponent, @SuppressWarnings("unused") Class<?> missingElement) {
            System.err.println("Mandatory tag " + missingElement.getSimpleName() + " is missing in its parent component " + thisComponent.getName());
        }

        @Override
        public void duplicatedTag(QFField qfField, QFField currentValue, QFComponent component) {
            System.out.println("Tag \"" + qfField + "\" will not replace value \"" + currentValue + "\" in class \"" + component.getName() + "\". This tag will be used in some other component.");
        }

        @Override
        public void duplicatedComponent(QFComponent childrenComponentInstance, QFComponent thisInstance) {
            System.out.println("Component \"" + childrenComponentInstance.getName() + "\" already exists in class \"" + thisInstance.getName() + "\". Please, check the incoming FIX message for data integrity.");
        }

        @Override
        public void invalidGroupCount(QFField qfField, List<? extends QFComponent> groupInstances, QFComponent thisInstance) {
            System.out.println("Declared number [" + qfField.getValue() + "] of group elements does not fit the real number [" + groupInstances.size() + "] in component \"" + thisInstance.getName() + "\". Please, check the incoming FIX message for data integrity.");
        }

        /**
         * This implementation adds the unknown tag to list of unknown tags.
         * @param unknownTag    unknown tag.
         * @param message       message being parsed.
         * @see QFMessage#addUnknownTag(QFField)
         */
        @Override
        public void unknownTag(QFField unknownTag, QFMessage message) {
            // add this tag to list of unknown tags (this list will be used when the message will be "serialized" to a String)
            message.addUnknownTag(unknownTag);
        }

        @Override
        public <V> V invalidFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull CharSequence problematicValue, @Nullable Throwable t) throws UnsupportedOperationException {
            if (t == null) {
                System.err.println("\"" + problematicValue + "\" should not be used as value of FIX tag " + fieldClass.getSimpleName());
            } else {
                System.err.println("\"" + problematicValue + "\" should not be used as value of FIX tag " + fieldClass.getSimpleName() + " due to error " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
            Object res = getDefaultValue(typeClass);
            return (V) res;
        }

        @Override
        public <V> V notPredefinedFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull V problematicValue) throws UnsupportedOperationException {
            System.err.println("Value " + problematicValue + " is not pre-defined in field " + fieldClass.getSimpleName());
            return problematicValue;
        }
    };


    private static QFComponentValidator defaultComponentValidator = DEFAULT_COMPONENT_VALIDATOR;

    public static QFComponentValidator getDefaultComponentValidator() {
        return defaultComponentValidator;
    }

    public static void setDefaultComponentValidator(QFComponentValidator defaultComponentValidator) {
        QFComponentValidator.defaultComponentValidator = defaultComponentValidator;
    }

}
