package net.kem.newtquickfix.blocks;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 10/27/14
 * Time: 10:09 AM
 */

public class QFTag implements Comparable<QFTag> {
    int tagKey;
    String tagValue;

    public QFTag(String[] kv) {
        this(Integer.parseInt(kv[0]), kv[1]);
    }

    public QFTag(int tagKey, String tagValue) {
        this.tagKey = tagKey;
        this.tagValue = tagValue;
    }

    public int getTagKey() {
        return tagKey;
    }

    public String getTagValue() {
        return tagValue;
    }

    public String toFIXString() {
        return tagKey + "=" + tagValue + '\u0001';
    }

    @Override
    public String toString() {
        return "{" + tagKey + "=" + tagValue + '}';
    }

    @Override
    public int compareTo(QFTag o) {
        return tagKey - o.tagKey;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(obj instanceof QFTag) {
            QFTag qTag = (QFTag) obj;
            if(tagKey == qTag.tagKey) {
                if((tagValue == null && qTag.tagValue == null) || // either both values are null OR
                        (tagValue != null && qTag.tagValue != null && tagValue.equals(qTag.tagValue))) { // both values are same.
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return tagKey;
    }
}
