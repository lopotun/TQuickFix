package net.kem.newtquickfix.builders;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:27 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFCharacterFieldBrick extends QFFieldBrick {
    public QFCharacterFieldBrick(String parentClassName, Class typeClass, CharSequence importLine, CharSequence typeToStringConversion) {
        super(parentClassName, typeClass, importLine, typeToStringConversion);
    }

    protected String getQuotedValue(String valueEnum) {
        return '\'' + valueEnum + '\'';
    }
}