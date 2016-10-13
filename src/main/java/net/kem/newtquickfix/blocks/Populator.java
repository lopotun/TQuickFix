package net.kem.newtquickfix.blocks;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class Populator {
	final Method setter;
	protected final Class<?> ownerClass;
	private final Method ownerGetInstanceMethod;

	Populator(@NotNull Method setter) throws NoSuchMethodException {
		this(setter, null);
	}

	Populator(@NotNull Method setter, @Nullable Class<?> ownerClass) throws NoSuchMethodException {
		this.setter = setter;
		this.ownerClass = ownerClass ==null? setter.getDeclaringClass(): ownerClass;
		this.ownerGetInstanceMethod = this.ownerClass.getDeclaredMethod("of");
	}

	Class<?> getOwnerClass() {
		return ownerClass;
	}

	Method getOwnerGetInstanceMethod() {
		return ownerGetInstanceMethod;
	}
	protected void populate(@NotNull QFComponent owner, @NotNull Object child, Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		setter.invoke(owner, child);
	}

	@Override
	public String toString() {
		return ownerClass.getSimpleName() + "." + setter.getName() + "(" + setter.getParameterTypes()[0].getSimpleName() + ")";
	}
}
