package net.kem.newtquickfix;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFMessage;
import net.kem.newtquickfix.builders.BuilderUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

/**
 * Created by Evgeny Kurtser on 12/23/2015 at 2:41 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFComponentValidator {

    public enum NotificationSeverity {TRACE, DEBUG, INFO, WARNING, ERROR, FATAL}

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
     */
    @Nullable
    public Boolean validateComponent(@SuppressWarnings("unused") QFComponent thisComponent) {
        return null;
    }

    /**
     * This method is called by a LiteFix Component validation mechanism when it detects missing mandatory field in this Component.
     * This implementation returns <em>false</em> meaning that this missing mandatory field should cause validation error.
     *
     * @param thisComponent     LiteFix Component being validated.
     * @param missingElement    missing mandatory field.
     * @return <em>true</em> if this component should be considered as valid although missing mandatory field;
     * <em>false</em> if this missing mandatory field should cause validation error.
     */
    @NotNull
    public Boolean mandatoryElementMissing(QFComponent thisComponent, Class<?> missingElement) {
        return false;
    }

    /**
     * This method is called while FIX data parsing when tag 'qfField' has been already present previously by the 'currentValue' in component 'component'.
     * In most cases this situation indicated about structure error in incoming FIX message.
     *
     * @param qfField      duplicated tag.
     * @param currentValue current tag.
     * @param component    component that contains the tag.
     */
    public void duplicatedTag(QFField qfField, QFField currentValue, QFComponent component) {
    }

    /**
     * Currently not in use.
     * @param childrenComponentInstance .
     * @param thisInstance              .
     */
    public void duplicatedComponent(QFComponent childrenComponentInstance, QFComponent thisInstance) {
    }

    /**
     * This method is called while FIX data parsing when the actual number of group elements does not fix the declared number of group elements.
     * @param numOfElementsField    contains declared number of group elements.
     * @param groupInstances        list of actually parsed group elements.
     * @param ownerClass            group class.
     */
    public void invalidGroupCount(QFField numOfElementsField, List<? extends QFComponent> groupInstances, Class<? extends QFComponent> ownerClass) {
    }

    /**
     * This method is called by a:
     * (a) LiteFix Field when value of this field is not one of the its predefined values.
     * (b) LiteFix Field when value of this field cannot be parsed to field type (e.g. when value "abcd" is given to an Integer or a Date/Time field).
     *
     * @param fieldClass          .
     * @param problematicValue    .
     * @param t                   .
     * @return value to use instead of invalid one. If there is no such a value, the method should throw an exception.
     * @throws UnsupportedOperationException
     */
    public <V> V invalidFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull CharSequence problematicValue, @Nullable Throwable t) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Invalid value " + problematicValue + " in field " + fieldClass.getSimpleName());
    }

    public <V> V notPredefinedFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull V problematicValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Value " + problematicValue + " is not pre-defined in field " + fieldClass.getSimpleName());
    }

    /**
     * This method is called while FIX data parsing when tag 'unprocessedTag' is recognized neither by 'message' (misplaced tag) not by any of its components (unknown tag).
     * @param unprocessedTag unprocessed tag.
     * @see #getUnprocessedTags()
     */
    public void unprocessedTag(@NotNull QFField unprocessedTag, @NotNull Class<? extends QFComponent> ownerClass) {
    }

    /**
     * Retrieves    list of unprocessed tags.
     * @return      list of unprocessed tags. If there is no unprocessed tags, then an empty list is returned.
     * @see #getUnprocessedTags()
     */
    public List<QFField<String>> getUnprocessedTags() {
        return Collections.emptyList();
    }

    /**
     * Collects auxiliary notifications that LiteFix parser/validator may produce.
     * @param notification    notification message.
     * @param severity        message severity level.
     */
    public void notify(@NotNull CharSequence notification, @NotNull NotificationSeverity severity) {
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
                res = Currency.getInstance("USD");
            }
            break;
            case "Double": {
                res = 0d;
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
        public Boolean mandatoryElementMissing(@SuppressWarnings("unused") QFComponent thisComponent, @SuppressWarnings("unused") Class<?> missingElement) {
            System.err.println("Mandatory tag " + missingElement.getSimpleName() + " is missing in its parent component " + thisComponent.getName());
            return false;
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
        public void invalidGroupCount(QFField numOfElementsField, List<? extends QFComponent> groupInstances, Class<? extends QFComponent> ownerClass) {
            System.out.println(numOfElementsField.getName() + " declares " + numOfElementsField.getValue() + " group elements. However, actual number of group elements does not fit this number in component \"" + ownerClass.getSimpleName() + "\". Please, check the incoming FIX message for data integrity.");
        }

        @Override
        public <V> V invalidFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull CharSequence problematicValue, @Nullable Throwable t) throws UnsupportedOperationException {
            if (t == null) {
                System.err.println("\"" + problematicValue + "\" should not be used as value of FIX tag " + fieldClass.getSimpleName());
            } else {
                System.err.println("\"" + problematicValue + "\" should not be used as value of FIX tag " + fieldClass.getSimpleName() + " due to error " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
            V res = getDefaultValue(typeClass);
            return res;
        }

        @Override
        public <V> V notPredefinedFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull V problematicValue) throws UnsupportedOperationException {
            System.err.println("Value " + problematicValue + " is not pre-defined in field " + fieldClass.getSimpleName());
            return problematicValue;
        }

        /**
         * This implementation adds the unprocessed tag to list of unprocessed tags.
         * @param unprocessedTag    unknown tag.
         * @see BuilderUtils#UNCLAIMED_TAGS
         */
        @Override
        public void unprocessedTag(@NotNull QFField unprocessedTag, @NotNull Class<? extends QFComponent> ownerClass) {
            // add this tag to list of unknown tags (this list will be used when the message will be "serialized" to a String)
            BuilderUtils.UNCLAIMED_TAGS.get().add(unprocessedTag);
            if(unprocessedTag.isKnown()) {
                System.out.println("Tag \"" + unprocessedTag + "\" should not appear in message \"" + ownerClass.getSimpleName() + "\"");
            } else {
                System.out.println("Undefined tag \"" + unprocessedTag + "\" has been detected while processing component \"" + ownerClass.getSimpleName() + "\"");
            }
        }

        @Override
        public List<QFField<String>> getUnprocessedTags() {
            return BuilderUtils.UNCLAIMED_TAGS.get();
        }


        @Override
        public void notify(@NotNull CharSequence notification, @NotNull NotificationSeverity severity) {
            // add this tag to list of unknown tags (this list will be used when the message will be "serialized" to a String)
            BuilderUtils.NOTIFICATIONS.get().add(new ImmutablePair<>(notification, severity));
            System.out.println(severity + ":\t" + notification);
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
