package net.kem.newtquickfix.gateway.json;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.QFComponentValidator;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFUtils;
import net.kem.newtquickfix.builders.BuilderUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeny Kurtser on 31-May-16 at 11:55 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
@SuppressWarnings("unused")
public class JSONQFComponentValidator implements QFComponentValidator {
	private List<QFUtils.UnknownTag> UNCLAIMED_TAGS;

	public enum Failures {
		// Validation
		MANDATORY_ELEMENT_MISSING,
		// Parsing
		DUPLICATED_TAG,
		DUPLICATED_COMPONENT,
		INVALID_GROUP_COUNT,
		INVALID_FIELD_VALUE,
		NOT_PREDEFINED_FIELD_VALUE,
		UNPROCESSED_TAG,
		NOTIFY
	}

	private Multimap<Failures, String> failures = LinkedListMultimap.create();

	public Map<Failures, Collection<String>> getFailures() {
		return failures.asMap();
	}

	public Collection<String> getFailure(Failures failure) {
		return failures.get(failure);
	}

	public boolean hasFailure(Failures failure) {
		return failures.containsKey(failure);
	}

	public boolean hasFailure() {
		return !failures.isEmpty();
	}

	@Override
	public Boolean mandatoryElementMissing(@SuppressWarnings("unused") QFComponent thisComponent, @SuppressWarnings("unused") Class<?> missingElement) {
		String out = "Mandatory tag " + missingElement.getSimpleName() + " is missing in its parent component " + thisComponent.getName();
		failures.put(Failures.MANDATORY_ELEMENT_MISSING, out);
		return false;
	}

	@Override
	public void duplicatedTag(QFField qfField, QFField currentValue, QFComponent component) {
		String out = "Tag \"" + qfField + "\" will not replace value \"" + currentValue + "\" in class \"" + component.getName() + "\". This tag will be used in some other component.";
		failures.put(Failures.DUPLICATED_TAG, out);
	}

	@Override
	public void duplicatedComponent(QFComponent childrenComponentInstance, QFComponent thisInstance) {
		String out = "Component \"" + childrenComponentInstance.getName() + "\" already exists in class \"" + thisInstance.getName() + "\". Please, check the incoming FIX message for data integrity.";
		failures.put(Failures.DUPLICATED_COMPONENT, out);
	}

	@Override
	public void invalidGroupCount(QFField numOfElementsField, List<? extends QFComponent> groupInstances, Class<? extends QFComponent> ownerClass) {
		String out = numOfElementsField.getName() + " declares " + numOfElementsField.getValue() + " group elements. However, actual number of group elements does not fit this number in component \"" + ownerClass.getSimpleName() + "\". Please, check the incoming FIX message for data integrity.";
		failures.put(Failures.INVALID_GROUP_COUNT, out);
	}

	@Override
	public <V> V invalidFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull CharSequence problematicValue, @Nullable Throwable t) throws UnsupportedOperationException {
		String out;
		if(t == null) {
			out = "\"" + problematicValue + "\" should not be used as value of FIX tag " + fieldClass.getSimpleName();
		} else {
			out = "\"" + problematicValue + "\" should not be used as value of FIX tag " + fieldClass.getSimpleName() + " due to error " + t.getClass().getSimpleName() + ": " + t.getMessage();
		}
		failures.put(Failures.INVALID_FIELD_VALUE, out);
		return getDefaultValue(typeClass);
	}

	@Override
	public <V> V notPredefinedFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull V problematicValue) throws UnsupportedOperationException {
		String out = "Value " + problematicValue + " is not pre-defined in field " + fieldClass.getSimpleName();
		failures.put(Failures.NOT_PREDEFINED_FIELD_VALUE, out);
		return problematicValue;
	}

	/**
	 * This implementation adds the unprocessed tag to list of unprocessed tags.
	 *
	 * @param unprocessedTag unknown tag.
	 */
	@Override
	public void unprocessedTag(@NotNull QFField unprocessedTag, @NotNull Class<? extends QFComponent> ownerClass) {
		// add this tag to list of unknown tags (this list will be used when the message will be "serialized" to a String)
		if(UNCLAIMED_TAGS == null) {
			UNCLAIMED_TAGS = new LinkedList<>();
		}
		UNCLAIMED_TAGS.add(new QFUtils.UnknownTag(unprocessedTag));
		String out;
		if(unprocessedTag.isKnown()) {
			out = "Tag \"" + unprocessedTag + "\" should not appear in message \"" + ownerClass.getSimpleName() + "\"";
		} else {
			out = "Undefined tag \"" + unprocessedTag + "\" has been detected while processing component \"" + ownerClass.getSimpleName() + "\"";
		}
		failures.put(Failures.UNPROCESSED_TAG, out);
	}

	@Override
	public List<QFUtils.UnknownTag> getUnprocessedTags() {
		return UNCLAIMED_TAGS==null? Collections.emptyList(): UNCLAIMED_TAGS;
	}


	@Override
	public void notify(@NotNull CharSequence notification, @NotNull NotificationSeverity severity) {
		// add this tag to list of unknown tags (this list will be used when the message will be "serialized" to a String)
		BuilderUtils.NOTIFICATIONS.get().add(new ImmutablePair<>(notification, severity));
		System.out.println(severity + ":\t" + notification);
	}
}
