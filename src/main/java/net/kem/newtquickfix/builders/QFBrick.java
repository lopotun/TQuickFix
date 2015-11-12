package net.kem.newtquickfix.builders;


import net.kem.newtquickfix.blocks.QFMember;

/**
 * Created by Evgeny Kurtser on 11/11/2015 at 9:32 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public interface QFBrick {
    default QFMember.Type getFIXType() {
        return QFMember.Type.FIELD;
    }

    String getName();

    default boolean isRequired() {
        return false;
    }

    String getFirstFiledName();

    void getMemberAnnotation();
}
