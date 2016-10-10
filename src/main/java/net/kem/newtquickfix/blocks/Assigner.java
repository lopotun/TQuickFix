package net.kem.newtquickfix.blocks;

import com.sun.istack.internal.NotNull;
import net.kem.newtquickfix.QFComponentValidator;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Evgeny Kurtser on 01-Sep-16 at 8:25 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class Assigner {
	final Method mySetter;
	Class<?> myClass;
	private final ComponentAssigner ownerComponentAssigner;

	Assigner(Method setter, ComponentAssigner ownerComponentAssigner) throws NoSuchMethodException {
		mySetter = setter;
		myClass();
		this.ownerComponentAssigner = ownerComponentAssigner;
	}
	void myClass() {
		myClass = mySetter.getParameterTypes()[0];
	}

	void assignMeToParent(@NotNull Object fieldOrComponent, @NotNull Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE, @NotNull QFComponentValidator componentValidator) throws InvocationTargetException, IllegalAccessException {
		QFComponent owner = getOwner(fieldOrComponent, COMPONENT_CLASS_TO_INSTANCE, componentValidator);
		if(owner != null) {
			mySetter.invoke(owner, fieldOrComponent);
			setParent(fieldOrComponent, owner);
		}
	}

	QFComponent getOwner(@NotNull Object whoAsks, @NotNull Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE, @NotNull QFComponentValidator componentValidator) throws InvocationTargetException, IllegalAccessException {
		return ownerComponentAssigner == null ?
				null:
				ownerComponentAssigner.getInstance(whoAsks, COMPONENT_CLASS_TO_INSTANCE, componentValidator);
	}

	protected void setParent(@NotNull Object fieldOrComponent, QFComponent owner) {
		if(fieldOrComponent instanceof QFComponent) {
			QFComponent qfComponent = (QFComponent) fieldOrComponent;
			qfComponent._parent = new WeakReference<>(owner);
		}
	}

	@Override
	public String toString() {
		return "F: " + myClass.getSimpleName() + " son of " + ownerComponentAssigner.myClass.getSimpleName();
	}
}