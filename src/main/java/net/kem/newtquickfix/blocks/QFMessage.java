package net.kem.newtquickfix.blocks;

import net.kem.newtquickfix.QFComponentValidator;

import java.util.Deque;
import java.util.List;

/**
 * Created by Evgeny Kurtser on 12/22/2015 at 9:22 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFMessage extends QFComponent {

    protected List<QFUtils.UnknownTag> unknownTags;

    public abstract QFField<String> getMessageType();
    public abstract String getMessageCategory();

//    public abstract QFComponent getStandardHeader();
//    public abstract void setStandardHeader(QFComponent standardHeader);

//    public abstract QFComponent getStandardTrailer();
//    public abstract void setStandardTrailer(QFComponent standardTrailer);

    protected static <QFComp extends QFMessage> QFComp getInstance(Deque<QFField> tags, QFComp thisInstance, Class<? extends QFMessage> compClass, QFComponentValidator componentValidator) {
        CharSequence fixVersion = (CharSequence) tags.peek().getValue();
        while(true) {
            thisInstance = QFComponent.getInstance(fixVersion, tags, thisInstance, compClass, componentValidator);
            if (tags.isEmpty()) {
                // All the tags were consumed. Stop the loop.
                break;
            } else {
                // One or more tags were not recognized by any of message components.
                // Remove this tag from the Deque,
                final QFField qfField = tags.pop();
                // [most probably] add it to list of unknown tags (this list will be used when the message will be "serialized" to a String)
                componentValidator.unprocessedTag(qfField, compClass);
                // and proceed to next field.
            }
        }
        thisInstance.unknownTags = componentValidator.getUnprocessedTags();
        return thisInstance;
    }

    public void setUnknownTag(List<QFUtils.UnknownTag> unknownTags) {
        this.unknownTags = unknownTags;
    }

    public StringBuilder toFIXString() {
        StringBuilder sb = new StringBuilder(2048);
        toFIXString(sb);
        return sb;
    }

    public void toFIXString(StringBuilder sb) {
        if (unknownTags != null) {
            for (QFField<String> unknownTag : unknownTags) {
                unknownTag.toFIXString(sb);
            }
        }
    }
}
