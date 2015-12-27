package net.kem.newtquickfix;

import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFMessage;

/**
 * Created by Evgeny Kurtser on 12/23/2015 at 2:41 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public interface QFComponentValidator {
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
    default Boolean validateComponent(@SuppressWarnings("unused") QFComponent thisComponent) {
        return null;
    }

    /**
     * *  (a) LiteFix Component when mandatory field is missing in this Component. In this case 'errorType' is {@linkplain ErrorType#MISSING};
     * @param thisComponent
     * @param missingElement
     */
    default void mandatoryElementMissing(@SuppressWarnings("unused") QFComponent thisComponent, @SuppressWarnings("unused") Class<?> missingElement) {
    }

    QFComponentValidator DEFAULT_COMPONENT_VALIDATOR = new QFComponentValidator() {
        @Override
        public void mandatoryElementMissing(@SuppressWarnings("unused") QFComponent thisComponent, @SuppressWarnings("unused") Class<?> missingElement) {
            System.err.println("Mandatory tag " + missingElement.getSimpleName() + " is missing in its parent component " + thisComponent.getName());
        }
    };
}
