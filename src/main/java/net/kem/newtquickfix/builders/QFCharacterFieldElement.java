package net.kem.newtquickfix.builders;

import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:27 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFCharacterFieldElement extends QFFieldElement {
    public QFCharacterFieldElement(Element startElement, BuilderUtils.QFFieldBlockDef def) {
        super(startElement, def);
    }

    protected String getQuotedValue(String valueEnum) {
        return '\'' + valueEnum + '\'';
    }
}