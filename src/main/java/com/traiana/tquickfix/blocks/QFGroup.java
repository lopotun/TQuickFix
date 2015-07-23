package com.traiana.tquickfix.blocks;

import com.traiana.tquickfix.builder.QFBuilderConfig;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 10/27/14
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class QFGroup extends QFComponent {
    protected int number;
    protected int firstMemberNumber;

    protected QFGroup(String name, int number, int firstMemberNumber) {
        this(name, number, firstMemberNumber, null);
    }

    protected QFGroup(String name, int number, int firstMemberNumber, QFBuilderConfig config) {
        super(name, config);
        this.number = number;
        this.firstMemberNumber = firstMemberNumber;
    }

    public int getNumber() {
        return number;
    }

    int getFirstMemberNumber() {
        return firstMemberNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(obj instanceof QFGroup) {
            QFGroup qfGroup = (QFGroup) obj;
            return number == qfGroup.number;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return number;
    }
}