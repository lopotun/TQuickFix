package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFMessage;
import net.kem.newtquickfix.blocks.QFUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.LinkedList;
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
            final Class<? extends QFMessage> messageClass = QFUtils.getMessageClass(tags);
            final Method getInstanceMethod = messageClass.getMethod("getInstance", Deque.class, QFComponentValidator.class);
            return (QFMessage) getInstanceMethod.invoke(null, tags, componentValidator);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new UnsupportedOperationException("Internal error", e);
        }
    }

    public boolean validate(QFMessage message, QFComponentValidator componentValidator) {
        return message.validate(componentValidator);
    }

//    private static Stack<QFField> toFieldsStack(CharSequence src, QFComponentValidator componentValidator) {
//        Stack<QFTag> origTags = new Stack<>();
//        final String[] rawTags = src.toString().split("\u0001");// \u0001|\n
//        if(componentValidator.getDefaultFIXVersion() != null) {
//            rawTags[0] = "8=" + componentValidator.getDefaultFIXVersion();
//        }
//        for (String rawTag : rawTags) {
//            final Matcher matcher = PATTERN.matcher(rawTag);
//            if (matcher.matches()) {
//                origTags.add(new QFTag(Integer.parseInt(matcher.group(1)), matcher.group(2)));
//            }
//        }// 0 = {8=FIX.4.4}, 2 = {35=8}
//        CharSequence fixVersion = origTags.get(0).getTagValue();
//        //reverse
//        Stack<QFField> tags = new Stack<>();
//        while (!origTags.empty()) {
//            QFTag kv = origTags.pop();
//            QFField qfField = QFUtils.lookupField(fixVersion, kv, componentValidator);
//            tags.push(qfField);
//        }
//        return tags;
//    }

    private static Deque<QFField> toFieldsStack(CharSequence src, QFComponentValidator componentValidator) {
        final String[] rawTags = src.toString().split("\u0001");// \u0001|\n
        if(componentValidator.getDefaultFIXVersion() != null) {
            rawTags[0] = "8=" + componentValidator.getDefaultFIXVersion();
        }
        CharSequence fixVersion = rawTags[0].substring(2); // 8=AS -> AS
	    Deque<QFField> tags = new LinkedList<>();
        for (String rawTag : rawTags) {
            final Matcher matcher = PATTERN.matcher(rawTag);
            if (matcher.matches()) {
                QFField qfField = QFUtils.lookupField(fixVersion, matcher.group(1), matcher.group(2), componentValidator);
                tags.add(qfField);
            }
        }// 0 = {8=FIX.4.4}, 2 = {35=8}
        return tags;
    }
}
