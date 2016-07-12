package net.kem.newtquickfix;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFUtils;
import net.kem.newtquickfix.builders.BuilderUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Evgeny Kurtser on 31-May-16 at 11:55 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class DefaultQFComponentValidator implements QFComponentValidator {
    private static final ThreadLocal<List<QFUtils.UnknownTag>> UNCLAIMED_TAGS = ThreadLocal.withInitial(LinkedList::new);

	/**
	 * Since DefaultQFComponentValidator is stateless, there is nothing to cleanup.
	 */
	public void cleanup() {}

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
        return getDefaultValue(typeClass);
    }

    @Override
    public <V> V notPredefinedFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull V problematicValue) throws UnsupportedOperationException {
        System.err.println("Value " + problematicValue + " is not pre-defined in field " + fieldClass.getSimpleName());
        return problematicValue;
    }

    /**
     * This implementation adds the unprocessed tag to list of unprocessed tags.
     * @param unprocessedTag    unknown tag.
     * @see DefaultQFComponentValidator#UNCLAIMED_TAGS
     */
    @Override
    public void unprocessedTag(@NotNull QFField unprocessedTag, @NotNull Class<? extends QFComponent> ownerClass) {
        // add this tag to list of unknown tags (this list will be used when the message will be "serialized" to a String)
        UNCLAIMED_TAGS.get().add(new QFUtils.UnknownTag(unprocessedTag));
        if(unprocessedTag.isKnown()) {
            System.out.println("Tag \"" + unprocessedTag + "\" should not appear in message \"" + ownerClass.getSimpleName() + "\"");
        } else {
            System.out.println("Undefined tag \"" + unprocessedTag + "\" has been detected while processing component \"" + ownerClass.getSimpleName() + "\"");
        }
    }

    @Override
    public List<QFUtils.UnknownTag> getUnprocessedTags() {
        return UNCLAIMED_TAGS.get();
    }


    @Override
    public void notify(@NotNull CharSequence notification, @NotNull NotificationSeverity severity) {
        // add this tag to list of unknown tags (this list will be used when the message will be "serialized" to a String)
        BuilderUtils.NOTIFICATIONS.get().add(new ImmutablePair<>(notification, severity));
        System.out.println(severity + ":\t" + notification);
    }
}
