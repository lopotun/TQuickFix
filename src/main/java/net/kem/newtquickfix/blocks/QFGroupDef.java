package net.kem.newtquickfix.blocks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Evgeny Kurtser on 11/2/2015 at 11:37 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //on class level
public @interface QFGroupDef {
    Class<? extends QFField> countField();
    Class<? extends QFField> delimiterField();
    int count();
    int delimiter();
}
