package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.Assigner;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFMessage;
import net.kem.newtquickfix.blocks.QFUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Evgeny Kurtser on 01-Jun-16 at 12:53 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class LiteFixMessageParser {
	private static final Pattern PATTERN = Pattern.compile("(\\d+)=(.*)");

	private static ThreadLocal<QFComponentValidator> componentValidator = new ThreadLocal<>();

	public static LiteFixMessageParser of(QFComponentValidator componentValidator) throws InvocationTargetException, NoSuchMethodException, IOException, IllegalAccessException, NoSuchFieldException {
		LiteFixMessageParser res = create();
		setComponentValidator(componentValidator);
		return res;
	}

	public static LiteFixMessageParser create() throws NoSuchMethodException, NoSuchFieldException, IOException, IllegalAccessException, InvocationTargetException {
		LiteFixMessageParser res = new LiteFixMessageParser();
		QFUtils.initMaps();
		return res;
	}

	public static QFComponentValidator getComponentValidator() {
		if(componentValidator.get() == null) {
			setComponentValidator(new DefaultQFComponentValidator());
		}
		return componentValidator.get();
	}

	public static void setComponentValidator(QFComponentValidator defaultComponentValidator) {
		componentValidator.set(defaultComponentValidator);
	}

	public QFMessage parse(CharSequence src) {
		return parse(src, getComponentValidator());
	}

	public QFMessage parse(CharSequence src, QFComponentValidator componentValidator) {
		componentValidator.cleanup();
		Deque<QFField> tags = toFieldsStack(src, componentValidator);
		if(tags.size() < 4) {
			throw new UnsupportedOperationException("Too few recognized tags in message " + src);
		}
		try {
			final Class<QFMessage> messageClass = QFUtils.getMessageClass(tags);

			QFUtils.insureMessageStructureCache(messageClass);

			List<QFUtils.UnknownTag> unknownTags = null;
			final Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE = new HashMap<>();

			for (QFField tag : tags) {
				Optional<Assigner> parent = QFUtils.getParentSetter(messageClass, tag);
//		        parent.ifPresent(System.out::println);
				Assigner populator = parent.orElse(null);
				if(populator == null) {
					if(unknownTags == null) {
						unknownTags = new LinkedList<>();
					}
					unknownTags.add(new QFUtils.UnknownTag(tag));
				} else {
					QFUtils.assignToComponent(tag, populator, COMPONENT_CLASS_TO_INSTANCE, componentValidator);
				}
			}
			final QFMessage message = (QFMessage) COMPONENT_CLASS_TO_INSTANCE.get(messageClass);

			if(unknownTags != null) {
				message.setUnknownTag(unknownTags);
			}
			return message;
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new UnsupportedOperationException("Internal error", e);
		}
	}

	public boolean validate(QFMessage message, QFComponentValidator componentValidator) {
		return message.validate(componentValidator);
	}

	private static Deque<QFField> toFieldsStack(CharSequence src, QFComponentValidator componentValidator) {
		Deque<QFField> tags = new LinkedList<>();
		final String[] rawTags = src.toString().split("\u0001");// \u0001|\n
		// Check basic message validity
		if(rawTags.length < 5 || rawTags[0].length() < 4 || rawTags[1].length() < 2 || rawTags[2].length() < 4) {
			return tags;
		}
		if(componentValidator.getDefaultFIXVersion() != null) {
			rawTags[0] = "8=" + componentValidator.getDefaultFIXVersion();
		}
		CharSequence fixVersion = rawTags[0].substring(2); // 8=FIX.5.0 -> FIX.5.0
		CharSequence messageType = rawTags[2].substring(3); // 35=AS -> AS
		for (String rawTag : rawTags) {
			final Matcher matcher = PATTERN.matcher(rawTag);
			if(matcher.matches()) {
				QFField qfField = QFUtils.lookupField(fixVersion, messageType, matcher.group(1), matcher.group(2), componentValidator);
				tags.add(qfField);
			}
		}// 0 = {8=FIX.4.4}, 2 = {35=8}
		return tags;
	}
}
