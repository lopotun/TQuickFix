package net.kem.newtquickfix;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFMessage;
import net.kem.newtquickfix.blocks.QFUtils;

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
public interface QFComponentValidator {

    enum NotificationSeverity {TRACE, DEBUG, INFO, WARNING, ERROR, FATAL}

    //  ------------------------------------
    //  ----    Validation callbacks    ----
    //  ------------------------------------

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
    default Boolean validateComponent(@SuppressWarnings("unused") QFComponent thisComponent) {
        return null;
    }

    /**
     * This method is called by a LiteFix Component validation mechanism when it detects missing mandatory field in this Component.
     * This implementation returns <em>false</em> meaning that this missing mandatory field should cause validation error.
     *
     * @param thisComponent  LiteFix Component being validated.
     * @param missingElement missing mandatory field.
     * @return <em>true</em> if this component should be considered as valid although missing mandatory field;
     * <em>false</em> if this missing mandatory field should cause validation error.
     */
    @NotNull
    default Boolean mandatoryElementMissing(QFComponent thisComponent, Class<?> missingElement) {
        return false;
    }


    //  ---------------------------------
    //  ----    Parsing callbacks    ----
    //  ---------------------------------

    /**
     * This method is called while FIX data parsing when tag 'qfField' has been already presented previously by the 'currentValue' in component 'component'.
     * In most cases this situation indicates structure error in incoming FIX message (e.g. misplaced delimiter field (such like PartyID) in a repeating group).
     *
     * @param qfField      duplicated tag.
     * @param currentValue current tag.
     * @param component    component that contains the tag.
     */
    default void duplicatedTag(QFField qfField, QFField currentValue, QFComponent component) {
    }

    /**
     * Currently not in use.
     *
     * @param childrenComponentInstance .
     * @param thisInstance              .
     */
    default void duplicatedComponent(QFComponent childrenComponentInstance, QFComponent thisInstance) {
    }

    /**
     * This method is called while FIX data parsing when the actual number of group elements does not fit the declared number of group elements.
     *
     * @param numOfElementsField contains declared number of group elements.
     * @param groupInstances     list of actually parsed group elements.
     * @param ownerClass         group class.
     */
    default void invalidGroupCount(QFField numOfElementsField, List<? extends QFComponent> groupInstances, Class<? extends QFComponent> ownerClass) {
    }

    /**
     * This method is called while FIX data parsing (or while new LiteFix Message creation) when value of LiteFix Field field cannot be parsed to field type (e.g. when value "abcd" is given to an Integer or a Date/Time field).
     *
     * @param fieldClass       LiteFix Field's Class
     * @param typeClass        Class type of expected value
     * @param problematicValue .
     * @param t                .
     * @return value to use instead of invalid one. If there is no such a value, the method should throw an exception.
     * @throws UnsupportedOperationException
     */
    default <V> V invalidFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull CharSequence problematicValue, @Nullable Throwable t) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Invalid value " + problematicValue + " in field " + fieldClass.getSimpleName());
    }

    /**
     * This method is called while FIX data parsing (or while new LiteFix Message creation) when the value of LiteFix Field field is not one of the its predefined values (when applicable).
     *
     * @param fieldClass       LiteFix Field's Class
     * @param typeClass        Class type of expected value
     * @param problematicValue actual value that causes the problem
     * @param <V>              .
     * @return value to use instead of invalid one. If there is no such a value, the method should throw an exception.
     * @throws UnsupportedOperationException
     */
    default <V> V notPredefinedFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull V problematicValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Value " + problematicValue + " is not pre-defined in field " + fieldClass.getSimpleName());
    }

    /**
     * This method is called while FIX data parsing when the given tag is recognized neither by LiteFix Message (misplaced tag) nor by any of its components (unknown tag).
     *
     * @param unprocessedTag unprocessed tag.
     * @param ownerClass     LiteFix Component that reports about unknown tag.
     * @see #getUnprocessedTags()
     */
    default void unprocessedTag(@NotNull QFField unprocessedTag, @NotNull Class<? extends QFComponent> ownerClass) {
    }

    /**
     * Retrieves list of unprocessed tags.
     *
     * @return list of unprocessed tags. If there is no unprocessed tags, then an empty list is returned.
     * @see #getUnprocessedTags()
     */
    default List<QFUtils.UnknownTag> getUnprocessedTags() {
        return Collections.emptyList();
    }

    /**
     * Collects auxiliary notifications that LiteFix parser/validator may produce.
     *
     * @param notification notification message.
     * @param severity     message severity level.
     */
    default void notify(@NotNull CharSequence notification, @NotNull NotificationSeverity severity) {
    }

	/**
     * This method is called while FIX data parser initialization when it's needed to "narrow" any FIX version of incoming messages to a single parser FIX version.
     * the given tag is recognized neither by LiteFix Message (misplaced tag) nor by any of its components (unknown tag).
     * This implementation returns <em>null</em>.
     * @return FIX version of the parser that will be user to parse any incoming message.
     * If the method returns <em>null</em>, then the parser version will be picked automatically according to FIX version of incoming message.
     */
    default String getDefaultFIXVersion() {
        return null;
    }

    default <V> V getDefaultValue(Class<V> typeClass) {
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
}