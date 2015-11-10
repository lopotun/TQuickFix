package net.kem.newtquickfix.blocks;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeny Kurtser
 * Date: 19-Oct-15
 * Time: 8:46 AM
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public interface ComponentMetadata<QFComp extends QFComponent> {
    Method[] getFieldSetters();
    Method[] getComponentSetters();
    SetterStructure[] getSetterStructure();
}