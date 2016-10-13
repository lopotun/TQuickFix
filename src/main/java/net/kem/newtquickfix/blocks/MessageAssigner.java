package net.kem.newtquickfix.blocks;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.QFComponentValidator;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by Evgeny Kurtser on 01-Sep-16 at 8:25 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
class MessageAssigner extends ComponentAssigner {

	MessageAssigner(Class<QFMessage> myClass) throws NoSuchMethodException {
		super(null, null);
		this.myClass = myClass;
		myGetInstanceMethod = myClass.getDeclaredMethod("of");
	}
	void myClass() {}


	@Override
	void assignMeToParent(@Nullable Object fieldOrComponent, @Nullable Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE, @Nullable QFComponentValidator componentValidator) throws InvocationTargetException, IllegalAccessException {
		//throw new UnsupportedOperationException("This method should not be called on " + MessageAssigner.class.getSimpleName() + " instance.");
	}

	@Override
	QFComponent getOwner(@NotNull Object whoAsks, @NotNull Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE, QFComponentValidator componentValidator) throws InvocationTargetException, IllegalAccessException {
		return null;
	}
}