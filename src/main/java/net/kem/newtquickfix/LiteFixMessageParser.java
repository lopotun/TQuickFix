package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFMessage;
import net.kem.newtquickfix.blocks.QFTag;
import net.kem.newtquickfix.blocks.QFUtils;
import net.kem.newtquickfix.builders.BuilderUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Evgeny Kurtser on 01-Jun-16 at 12:53 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class LiteFixMessageParser {
    private static final Pattern PATTERN = Pattern.compile("(\\d+)=(.*)");

    private LiteFixMessageParser() {
        try {
            QFUtils.fillMaps();
        } catch (NoSuchFieldException | InvocationTargetException | IOException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        try {
            QFUtils.fillMaps();
        } catch (NoSuchFieldException | InvocationTargetException | IOException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static ThreadLocal<QFComponentValidator> componentValidator = new ThreadLocal<QFComponentValidator>() {
        @Override
        protected QFComponentValidator initialValue() {
            return new DefaultQFComponentValidator();
        }
    };

    public static QFComponentValidator getComponentValidator() {
        return componentValidator.get();
    }

    public static void setComponentValidator(QFComponentValidator defaultComponentValidator) {
        componentValidator.set(defaultComponentValidator);
    }


    private static final LiteFixMessageParser INSTANCE = new LiteFixMessageParser();

    public static LiteFixMessageParser getInstance() {
        if(INSTANCE == null) {
            throw new NullPointerException("LiteFixMessageParser hasn't been initialized successfully. See application log for details.");
        }
        return INSTANCE;
    }


    public LiteFixMessageParser with(QFComponentValidator componentValidator) {
        setComponentValidator(componentValidator);
        return this;
    }

    public QFMessage parse(CharSequence src) {
        return parse(src, getComponentValidator());
    }

    public QFMessage parse(CharSequence src, QFComponentValidator componentValidator) {
        BuilderUtils.UNCLAIMED_TAGS.get().clear();
        Stack<QFField> tags = toFieldsStack(src, componentValidator);
        if(tags.size() < 4) {
            throw new UnsupportedOperationException("Too few recognized tags in message " + src);
        }
        final Class<? extends QFMessage> messageClass = QFUtils.getMessageClass(tags);
        if(messageClass == null) {
            throw new UnsupportedOperationException("Unrecognized message");
        }
        try {
            final Method getInstanceMethod = messageClass.getMethod("getInstance", Stack.class, QFComponentValidator.class);
            return (QFMessage) getInstanceMethod.invoke(null, tags, componentValidator);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new UnsupportedOperationException("Internal error", e);
        }
    }

    public boolean validate(QFMessage message) {
        return message.validate(componentValidator.get());
    }

    private Stack<QFField> toFieldsStack(CharSequence src, QFComponentValidator componentValidator) {
        Stack<QFTag> origTags = new Stack<>();
        final String[] rawTags = src.toString().split("\u0001");// \u0001|\n
        if(componentValidator.getDefaultFIXVersion() != null) {
            rawTags[0] = "8=" + componentValidator.getDefaultFIXVersion().getFixVersion();
        }
        for (String rawTag : rawTags) {
            final Matcher matcher = PATTERN.matcher(rawTag);
            if (matcher.matches()) {
                origTags.add(new QFTag(Integer.parseInt(matcher.group(1)), matcher.group(2)));
            }
        }// 0 = {8=FIX.4.4}, 2 = {35=8}
        CharSequence fixVersion = origTags.get(0).getTagValue();
        //reverse
        Stack<QFField> tags = new Stack<>();
        while (!origTags.empty()) {
            QFTag kv = origTags.pop();
            QFField qfField = QFUtils.lookupField(fixVersion, kv, componentValidator);
            tags.push(qfField);
        }
        return tags;
    }
}
