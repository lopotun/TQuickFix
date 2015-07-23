package com.traiana.tquickfix;

import com.traiana.tquickfix.blocks.QFCommonMessage;
import com.traiana.tquickfix.blocks.QFTag;
import com.traiana.tquickfix.builder.QFBuilderConfig;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses incoming FIX message.
 * User: EvgenyK
 * Date: 10/29/14
 * Time: 10:20 AM
 */
public class QFParser {
    private static final ThreadLocal<ThreadContext> THREAD_CONTEXT = new ThreadLocal<ThreadContext>() {
        protected ThreadContext initialValue() {
            ThreadContext.getInstance().reset();
            return ThreadContext.getInstance();
        }
    };
    private static final Logger LOGGER = Logger.getLogger(QFParser.class);
    private static QFParser INSTANCE = new QFParser();

    private static final Map<String, MessageMapper> MESSAGE_VERSION_TO_MESSAGE_MAPPER = new HashMap<>();
    static {
//        MESSAGE_VERSION_TO_MESSAGE_MAPPER.put("FIX.4.0", tqf.v40.QFMessageMapper.getInstance());
//        MESSAGE_VERSION_TO_MESSAGE_MAPPER.put("FIX.4.4", tqf.v44.QFMessageMapper.getInstance());
//        MESSAGE_VERSION_TO_MESSAGE_MAPPER.put("FIX.5.0", tqf.v50.QFMessageMapper.getInstance());
//        MESSAGE_VERSION_TO_MESSAGE_MAPPER.put("FIX.5.0.SP1", tqf.v50sp1.QFMessageMapper.getInstance());
        MESSAGE_VERSION_TO_MESSAGE_MAPPER.put("FIX.5.0.SP2", tqf.v50sp2.QFMessageMapper.getInstance());
    }


    /**
     * Returns {@linkplain QFParser} singleton.
     *
     * @return {@linkplain QFParser} singleton.
     * @throws RuntimeException if configuration could not be loaded.
     * @see com.traiana.tquickfix.builder.QFBuilderConfig#init(String)
     */
    public static QFParser getInstance() throws RuntimeException {
        return INSTANCE;
    }


    public ThreadContext getThreadContext() {
        return THREAD_CONTEXT.get();
    }


    public QFCommonMessage parseMessage(File fixFile, QFBuilderConfig config) throws IOException, ParseException {
        if(!fixFile.isFile() || !fixFile.canRead()) {
            throw new IOException("Cannot access " + fixFile.getAbsolutePath() + " as a readable file.");
        }
        QFCommonMessage res;
        CharSequence fixMessage = QFUtils.readFile(fixFile);

        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parsing " + fixFile.getName() + "... ");
        }

        res = parseMessage(fixMessage, config);

        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parsing done: " + res.toFIXString());
        }
        return res;
    }

    /**
     * Parses the given FIX message string.
     * Use {@linkplain QFParser#getThreadContext()#hasErrors()} and {@linkplain QFParser#getThreadContext()#getErrors(com.traiana.tquickfix.ThreadContext.ErrorType)}
     * after each invocation of this method to check errors status.
     *
     * @param fixString FIX message.
     * @param config
     * @return TQF Message instance.
     * @throws ParseException if Message Standard Header ({@linkplain tqf.v50sp2.component.StandardHeaderComponent}) cannot be parsed
     *                        or a runtime error occurred while message parsing.
     */
    public QFCommonMessage parseMessage(CharSequence fixString, QFBuilderConfig config) throws ParseException {
        THREAD_CONTEXT.get().reset(); // Clear thread-local errors list.
        List<QFTag> kv = QFUtils.getTagPairs(fixString);
        MutableInt index = new MutableInt(0);

        Method parseMethod = getMessageParseMethod(kv, true);
        try {
            QFCommonMessage res = (QFCommonMessage) parseMethod.invoke(null, kv, index, config);
            return res;
        } catch(IllegalAccessException e) {
            LOGGER.error("Internal parser error", e); //Should not happen.
            throw new ParseException("Internal parser error:" + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()), 0);
        } catch(InvocationTargetException e) {
            if(e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                LOGGER.error("Internal parser invocation error", e); //Should not happen.
                throw new ParseException("Internal parser invocation error:" + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()), 0);
            }
        }
    }

    private Method getMessageParseMethod(List<QFTag> kv, boolean ignoreVersion) throws ParseException {
        MessageMapper messageMapper;
        if(ignoreVersion) {
//            messageMapper = null;
            messageMapper = tqf.v50sp2.QFMessageMapper.getInstance();
        } else {
            String msgVersion = kv.get(0).getTagValue(); // 8=FIX.5.0
            messageMapper = MESSAGE_VERSION_TO_MESSAGE_MAPPER.get(msgVersion);
            if(messageMapper == null) {
                throw new ParseException("Unrecognizable message version " + msgVersion, 0);
            }
        }
        String msgType = kv.get(2).getTagValue(); // 35=J
        Method parseMethod = messageMapper.getMessageByType(msgType);
        if(parseMethod == null) {
            throw new ParseException("Unrecognizable message type " + msgType, 2);
        }
        parseMethod.setAccessible(true);
        return parseMethod;
    }
}