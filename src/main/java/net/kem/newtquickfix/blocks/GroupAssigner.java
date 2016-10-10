package net.kem.newtquickfix.blocks;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.LoggerUtil;
import net.kem.newtquickfix.QFComponentValidator;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Evgeny Kurtser on 01-Sep-16 at 8:25 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class GroupAssigner extends ComponentAssigner {
	private Method groupGetter; // List<NoSecurityAltID> = getNoSecurityAltID();
	private Class<? extends QFField> groupCountField; // fields.NoSecurityAltID
	private Class<? extends QFField> groupDelimiterField; // fields.SecurityAltID
	private Assigner groupDelimiterFieldAssigner;

	GroupAssigner(@NotNull Method setter, @Nullable ComponentAssigner ownerAssigner) throws NoSuchMethodException {
		super(setter, ownerAssigner);

		String getterName = 'g' + setter.getName().substring(1);
		groupGetter = ownerAssigner.myClass.getDeclaredMethod(getterName);
		// Here we have a kind of "public void setNoSecurityAltID(List<NoSecurityAltID> noSecurityAltID)" method.
		// We need to deduce "NoSecurityAltID" group class, its count field (fields.NoSecurityAltID) and its delimiter filed (fields.SecurityAltID)
		final Field[] componentsFields = ownerAssigner.myClass.getDeclaredFields();
		try {
			// 'componentField' is @QFMember(type = QFMember.Type.GROUP, groupClass = NoSecurityAltID.class) private List<NoSecurityAltID> noSecurityAltID;
			final Field componentField = Arrays.stream(componentsFields).filter(field -> field.getName().equals(StringUtils.uncapitalize(setter.getName().substring(3)))).findFirst().get();
			QFMember memberAnnotation = componentField.getAnnotation(QFMember.class);
			if(memberAnnotation != null) {
				myClass = memberAnnotation.groupClass(); // NoSecurityAltID
				// E.g.:
				// @QFGroupDef(countField = net.kem.newtquickfix.v50sp2.fields.NoSecurityAltID.class, count = net.kem.newtquickfix.v50sp2.fields.NoSecurityAltID.TAG,
				//             delimiterField = net.kem.newtquickfix.v50sp2.fields.SecurityAltID, delimiter = net.kem.newtquickfix.v50sp2.fields.SecurityAltID.TAG
				//             delimiterContainer = ...)
				// public static class NoSecurityAltID extends QFComponent
				groupCountField = myClass.getAnnotation(QFGroupDef.class).countField(); // countField = net.kem.newtquickfix.v50sp2.fields.NoSecurityAltID.class
				groupDelimiterField = myClass.getAnnotation(QFGroupDef.class).delimiterField(); // countField = net.kem.newtquickfix.v50sp2.fields.NoSecurityAltID.class
				myGetInstanceMethod = myClass.getDeclaredMethod("getInstance");// NoSecurityAltID.getInstance();

				// Create assigner for Group Delimiter Field.

			} else {
				String msg = "There is no @QFMember annotation for the " + componentField.getName() + " member in class " + myClass.getName();
				LoggerUtil.getLogger().severe(msg);
				throw new NoSuchElementException(msg);
			}
		} catch (NoSuchElementException e) {
			LoggerUtil.getLogger().severe("There is no abc member that corresponds to setAbc(List<Abc> abc) method in class " + myClass.getName());
			throw e;
		}
	}

	void myClass() {
	}


	@NotNull
	QFComponent getInstance(@NotNull Object whoAsks, @NotNull Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE, QFComponentValidator componentValidator) throws InvocationTargetException, IllegalAccessException {
		QFComponent myInstance;
		final Class groupFieldClass = whoAsks.getClass();
		// Group delimiter field
		if(groupFieldClass.equals(groupDelimiterField)) {
			// Remove all references to previous group member. That should be done to avoid situation when newly created group field could be attached to previous group.
			Iterator<Map.Entry<Class, QFComponent>> it = COMPONENT_CLASS_TO_INSTANCE.entrySet().iterator();
			while (it.hasNext()) {
				final Map.Entry<Class, QFComponent> qfComponentEntry = it.next();
				QFComponent qfComponent = qfComponentEntry.getValue();
				if(del(qfComponent)) {
					it.remove();
				}
			}

			QFComponent owner = getOwner(whoAsks, COMPONENT_CLASS_TO_INSTANCE, componentValidator);
			if(owner != null) {
				myInstance = createInstance(COMPONENT_CLASS_TO_INSTANCE, owner);// NoSecurityAltID.getInstance();
				List<QFComponent> groupMembers = buildGroupList(owner, -1, (QFField) whoAsks, componentValidator);
				groupMembers.add(myInstance);
			} else {
				myInstance = null; //TODO Is it possible?
			}
		} else {
			// Some other field that belongs to a group
			myInstance = super.getInstance(whoAsks, COMPONENT_CLASS_TO_INSTANCE, componentValidator);
		}
		return myInstance;
	}

	private boolean del(@NotNull QFComponent qfComponent) {
		//COMPONENT_CLASS_TO_INSTANCE.keySet().removeIf(aClass -> aClass.getName().contains("$"));
		if(qfComponent == null) {
			return false;
		}
		if(qfComponent.getClass().equals(myClass)) {
			return true;
		}
		if(qfComponent._parent == null) {
			return false;
		}
		return del(qfComponent._parent.get());
	}

	@NotNull
	private QFComponent createInstance(@NotNull Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE, @NotNull QFComponent owner) throws InvocationTargetException, IllegalAccessException {
		QFComponent myInstance = (QFComponent) myGetInstanceMethod.invoke(null);
		setParent(myInstance, owner);
		COMPONENT_CLASS_TO_INSTANCE.put(myClass, myInstance);
		return myInstance;
	}


	@Override
	void assignMeToParent(@NotNull Object fieldOrComponent, @NotNull Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE, @NotNull QFComponentValidator componentValidator) throws InvocationTargetException, IllegalAccessException {
		List<QFComponent> groupMembers;
		QFComponent owner = getOwner(fieldOrComponent, COMPONENT_CLASS_TO_INSTANCE, componentValidator);
		setParent(fieldOrComponent, owner);
		final Class groupFieldClass = fieldOrComponent.getClass();
		// Group count field
		if(groupFieldClass.equals(groupCountField)) {
			Integer count = (Integer) ((QFField) fieldOrComponent).getValue();
			buildGroupList(owner, count, fieldOrComponent, componentValidator);
		} else {
			groupMembers = buildGroupList(owner, -2, fieldOrComponent, componentValidator);
			groupMembers.add((QFComponent) fieldOrComponent);
		}
	}


	@NotNull
	private List<QFComponent> buildGroupList(@NotNull QFComponent owner, int count, @NotNull Object whoAsks, @NotNull QFComponentValidator componentValidator) throws InvocationTargetException, IllegalAccessException {
		List<QFComponent> groupMembers = (List<QFComponent>) groupGetter.invoke(owner);
		if(groupMembers == null) {
			if(count < 0) {
				//There is group member WITHOUT group delimiter! OR There is group delimiter WITHOUT group counter!
				componentValidator.brokenGroup(owner, whoAsks, QFComponentValidator.BrokenGroupReason.NO_DELIMITER);
				count = 3;
			}
			groupMembers = new ArrayList<>(count);
			mySetter.invoke(owner, groupMembers);
		} else {
			if(count > 0) {
				//This group has been already defined.
				componentValidator.brokenGroup(owner, whoAsks, QFComponentValidator.BrokenGroupReason.ALREADY_EXIST);
			}
		}
		return groupMembers;
	}

	Class<? extends QFField> getGroupCountField() {
		return groupCountField;
	}

	Class<? extends QFField> getGroupDelimiterField() {
		return groupDelimiterField;
	}
}