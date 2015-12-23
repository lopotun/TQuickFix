package net.kem.newtquickfix.blocks;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Evgeny Kurtser on 12/22/2015 at 9:22 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFMessage extends QFComponent {

    protected List<QFField<String>> unknownTags;
    protected QFComponent standardHeader;
    protected QFComponent standardTrailer;

    protected String messageType;
    public String getMessageType() {
        return messageType;
    }
    public void setMessageType(CharSequence messageType) {
        this.messageType = messageType.toString();
    }


    protected String messageCategory;
    public String getMessageCategory() {
        return messageCategory;
    }
    public void setMessageCategory(CharSequence messageCategory) {
        this.messageCategory = messageCategory.toString();
    }


    protected static <QFComp extends QFMessage> QFComp getInstance(Stack<QFField> tags, QFComp instance, Class<? extends QFMessage> compClass) {
        while(true) {
            instance = QFComponent.getInstance(tags, instance, compClass);
            if (tags.isEmpty()) {
                // All the tags were consumed. Stop the loop.
                break;
            } else {
                // One or more tags were not recognized by any of message components.
                // Remove this tag from the stack,
                final QFField qfField = tags.pop();
                // add it to list of unknown tags (this list will be used when the message will be "serialized" to a String)
                instance.addUnknownTag(qfField);
                // an proceed to next field.
            }
        }
        return instance;
    }

    protected void addUnknownTag(QFField<String> unknownTag) {
        if(unknownTags == null) {
            unknownTags = new LinkedList<>();
        }
        unknownTags.add(unknownTag);
    }

    public void toFIXString(StringBuilder sb) {
        if (unknownTags != null) {
            for (QFField<String> unknownTag : unknownTags) {
                unknownTag.toFIXString(sb);
            }
        }
    }
}
