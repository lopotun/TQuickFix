package net.kem.newtquickfix.blocks;

/**
 * Created by Evgeny Kurtser on 12/22/2015 at 9:22 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public abstract class QFMessage extends QFComponent {

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
}
