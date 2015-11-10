package net.kem.newtquickfix.blocks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Evgeny Kurtser on 11/1/2015 at 10:11 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.TYPE) //on class level
//@Target({ElementType.METHOD, ElementType.PARAMETER}) //on method and parameter level
@Target({ElementType.FIELD}) //on field level
public @interface QFMember {
    enum Type {
        FIELD, COMPONENT, GROUP
    }

    Type type() default Type.FIELD;

    Class<? extends QFComponent> groupClass() default QFComponent.class;
}
