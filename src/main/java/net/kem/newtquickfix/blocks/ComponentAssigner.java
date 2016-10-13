package net.kem.newtquickfix.blocks;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.QFComponentValidator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Evgeny Kurtser on 01-Sep-16 at 8:25 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
class ComponentAssigner extends Assigner {
	Method myGetInstanceMethod;

	ComponentAssigner(@NotNull Method setter, @Nullable ComponentAssigner ownerAssigner) throws NoSuchMethodException {
		super(setter, ownerAssigner);
		myGetInstanceMethod = myClass==null? null: myClass.getDeclaredMethod("of");
	}

	@NotNull
	QFComponent getInstance(@Nullable Object whoAsks, @NotNull Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE, @NotNull QFComponentValidator componentValidator) throws InvocationTargetException, IllegalAccessException {
		QFComponent myInstance = COMPONENT_CLASS_TO_INSTANCE.get(myClass);
		if(myInstance == null) {
			myInstance = (QFComponent) myGetInstanceMethod.invoke(null);
			COMPONENT_CLASS_TO_INSTANCE.put(myClass, myInstance);
//			 Attach the newly created instance to its parent here?
			assignMeToParent(myInstance, COMPONENT_CLASS_TO_INSTANCE, componentValidator);
		}
		return myInstance;
	}
}