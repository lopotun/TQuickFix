package net.kem.newtquickfix.blocks;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Evgeny Kurtser on 10/26/2015 at 12:06 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class SetterStructure<T> {
	private Class<T> groupClazz;
    private Method groupOwnerSetter;

	private int groupCounter;
	private int groupDelimiter;

	public SetterStructure(Class<T> groupOwnerClazz, Class<T> groupClazz, int groupCounter, int groupDelimiter) {
		try {
			this.groupOwnerSetter = groupOwnerClazz.getDeclaredMethod("set" + groupClazz.getSimpleName(), List.class);
			this.groupOwnerSetter.setAccessible(true);

			this.groupClazz = groupClazz;
			this.groupCounter = groupCounter;
			this.groupDelimiter = groupDelimiter;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	public Class<T> getGroupClazz() {
		return groupClazz;
	}

	public Method getGroupOwnerSetter() {
		return groupOwnerSetter;
	}

	public int getGroupCounter() {
		return groupCounter;
	}

	public int getGroupDelimiter() {
		return groupDelimiter;
	}
}