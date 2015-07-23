package com.traiana.tquickfix.blocks;

import com.traiana.tquickfix.builder.QFBuilderConfig;

import java.util.List;

/**
 * Abstract presentation of FIX message element.
 * User: EvgenyK
 * Date: 10/28/14
 * Time: 11:59 AM
 */
public abstract class QFCommonMessage extends QFComponent {
    protected String type;
    protected List<QFTag> unrecognizedFields;

    protected QFCommonMessage(String name, String type, QFBuilderConfig config) {
        super(name, config);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<QFTag> getUnrecognizedFields() {
        return unrecognizedFields;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(obj instanceof QFCommonMessage) {
            QFCommonMessage qfGroup = (QFCommonMessage) obj;
            return name.equals(qfGroup.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}