package net.kem.newtquickfix;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFUtils;
import net.kem.newtquickfix.builders.BuilderUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeny Kurtser on 31-May-16 at 11:55 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class DefaultQFComponentValidator implements QFComponentValidator {
	private static final ThreadLocal<List<QFUtils.UnknownTag>> UNCLAIMED_TAGS = ThreadLocal.withInitial(LinkedList::new);

	// These Table and Map are used by public QFField beforeFieldParse(CharSequence, CharSequence, int, String) just as example.
	// Feel free to remove it once this method has different implementation.
	private static final Table<CharSequence, Integer, QFField> EMPTY_VERSION = HashBasedTable.create(0, 0);
	private static final Map<CharSequence, Table<CharSequence, Integer, QFField>> PREPARSED_FIELDS = new HashMap<>(5);
	static {
		Table<CharSequence, Integer, QFField> V50 = HashBasedTable.create(1, 3);
		V50.put("AS", net.kem.newtquickfix.v50.fields.SignatureLength.TAG, net.kem.newtquickfix.v50.fields.SignatureLength.of(0));
		V50.put("AS", net.kem.newtquickfix.v50.fields.Signature.TAG, net.kem.newtquickfix.v50.fields.Signature.of(""));
		V50.put("AS", net.kem.newtquickfix.v50.fields.CheckSum.TAG, net.kem.newtquickfix.v50.fields.CheckSum.of("0"));
		V50.put("J", net.kem.newtquickfix.v50.fields.SignatureLength.TAG, net.kem.newtquickfix.v50.fields.SignatureLength.of(0));
		V50.put("J", net.kem.newtquickfix.v50.fields.Signature.TAG, net.kem.newtquickfix.v50.fields.Signature.of(""));
		V50.put("J", net.kem.newtquickfix.v50.fields.CheckSum.TAG, net.kem.newtquickfix.v50.fields.CheckSum.of("0"));
		PREPARSED_FIELDS.put("FIX50", V50);
		Table<CharSequence, Integer, QFField> V50SP1 = HashBasedTable.create(1, 3);
		V50SP1.put("AS", net.kem.newtquickfix.v50sp1.fields.SignatureLength.TAG, net.kem.newtquickfix.v50sp1.fields.SignatureLength.of(0));
		V50SP1.put("AS", net.kem.newtquickfix.v50sp1.fields.Signature.TAG, net.kem.newtquickfix.v50sp1.fields.Signature.of(""));
		V50SP1.put("AS", net.kem.newtquickfix.v50sp1.fields.CheckSum.TAG, net.kem.newtquickfix.v50sp1.fields.CheckSum.of("0"));
		PREPARSED_FIELDS.put("FIX50SP1", V50SP1);
		Table<CharSequence, Integer, QFField> V50SP2 = HashBasedTable.create(1, 3);
		V50SP2.put("AS", net.kem.newtquickfix.v50sp2.fields.SignatureLength.TAG, net.kem.newtquickfix.v50sp2.fields.SignatureLength.of(0));
		V50SP2.put("AS", net.kem.newtquickfix.v50sp2.fields.Signature.TAG, net.kem.newtquickfix.v50sp2.fields.Signature.of(""));
		V50SP2.put("AS", net.kem.newtquickfix.v50sp2.fields.CheckSum.TAG, net.kem.newtquickfix.v50sp2.fields.CheckSum.of("0"));
		PREPARSED_FIELDS.put("FIX50SP2", V50SP2);
	}

	/**
	 * Since DefaultQFComponentValidator is stateless, there is nothing to cleanup.
	 */
	public void cleanup() {
	}

	@Override
	public Boolean mandatoryElementMissing(@SuppressWarnings("unused") QFComponent thisComponent, @SuppressWarnings("unused") Class<?> missingElement) {
		System.err.println("Mandatory tag " + missingElement.getSimpleName() + " is missing in its parent component " + thisComponent.getName());
		return false;
	}


	/**
	 * !! EXAMPLE !! DO NOT USE IN PRODUCTION ENVIRONMENT !!
	 * @param fixVersion    FIX version.
	 * @param messageType   FIX message type.
	 * @param tagKey   tag number
	 * @param tagValue tag value
	 * @return
	 */
	@Nullable
	public QFField beforeFieldParse(@NotNull CharSequence fixVersion, @NotNull CharSequence messageType, int tagKey, @NotNull String tagValue) {
		final Table<CharSequence, Integer, QFField> preParsedFields = PREPARSED_FIELDS.getOrDefault(fixVersion, EMPTY_VERSION);
		final QFField field = preParsedFields.get(messageType, tagKey);
		return field;
	}


	/**
	 * !! EXAMPLE !! DO NOT USE IN PRODUCTION ENVIRONMENT !!
	 * @param fixVersion    FIX version.
	 * @param messageType   FIX message type.
	 * @param field   parsed {@link QFField} instance
	 * @return
	 */
	@NotNull
	public QFField afterFieldParse(@NotNull CharSequence fixVersion, @NotNull CharSequence messageType, @NotNull QFField field) {
		if(field.getTag() == 89) {
			final String signature = (String) field.getValue();
			if(signature != null) {
				System.out.println("****\t" + signature + "\t****");
			}
		}
		return field;
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
	public void brokenGroup(QFComponent groupOwner, Object groupField, BrokenGroupReason brokenGroupReason) {
		switch (brokenGroupReason) {
			case NO_DELIMITER:
				System.err.println("Error while processing field \"" + groupField + "\". Its parent group \"" + groupOwner.getName() + "\" is either without group delimiter OR there is group delimiter without group counter.");
				break;
			case ALREADY_EXIST:
				System.err.println("Error while processing field \"" + groupField + "\". Its parent group \"" + groupOwner.getName() + "\" has been already defined.");
				break;
		}
	}

	@Override
	public <V> V invalidFieldValue(@NotNull Class<?> fieldClass, @NotNull Class<V> typeClass, @NotNull CharSequence problematicValue, @Nullable Throwable t) throws UnsupportedOperationException {
		if(t == null) {
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
	 *
	 * @param unprocessedTag unknown tag.
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
