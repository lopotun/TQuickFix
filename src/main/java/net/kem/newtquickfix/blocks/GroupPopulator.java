package net.kem.newtquickfix.blocks;

import com.sun.istack.internal.NotNull;
import net.kem.newtquickfix.LoggerUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Evgeny Kurtser on 01-Aug-16 at 3:25 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class GroupPopulator extends Populator {
	private Class<? extends QFField> groupCountField; // fields.NoSecurityAltID
	private Class<? extends QFField> groupDelimiterField; // fields.SecurityAltID
	private Class<? extends QFComponent> groupClass; // NoSecurityAltID
	private Method groupMemberCtor;// NoSecurityAltID.getInstance();
	private Method groupGetter; // List<NoSecurityAltID> = getNoSecurityAltID();
	private Populator groupMemberPopulator;

	GroupPopulator(@NotNull Class<? extends QFMessage> message, @NotNull Method setter, @NotNull Class<? extends QFComponent> componentClass) throws NoSuchMethodException {
		super(setter);
		String getterName = 'g' + setter.getName().substring(1);
		groupGetter = componentClass.getDeclaredMethod(getterName);

		// Here we have a kind of "public void setNoSecurityAltID(List<NoSecurityAltID> noSecurityAltID)" method.
		// We need to deduce "NoSecurityAltID" group class, its count field (fields.NoSecurityAltID) and its delimiter filed (fields.SecurityAltID)
		final Field[] componentsFields = componentClass.getDeclaredFields();
		try {
			// 'componentField' is @QFMember(type = QFMember.Type.GROUP, groupClass = NoSecurityAltID.class) private List<NoSecurityAltID> noSecurityAltID;
			final Field componentField = Arrays.stream(componentsFields).filter(field -> field.getName().equals(StringUtils.uncapitalize(setter.getName().substring(3)))).findFirst().get();
			QFMember memberAnnotation = componentField.getAnnotation(QFMember.class);
			if(memberAnnotation != null) {
				groupClass = memberAnnotation.groupClass(); // NoSecurityAltID
//					MESSAGE_FIELD_PARENTSETTER.put(message, groupClass, possibleSetterMethod);
				// E.g.:
				// @QFGroupDef(countField = net.kem.newtquickfix.v50sp2.fields.NoSecurityAltID.class, count = net.kem.newtquickfix.v50sp2.fields.NoSecurityAltID.TAG,
				//             delimiterField = net.kem.newtquickfix.v50sp2.fields.SecurityAltID, delimiter = net.kem.newtquickfix.v50sp2.fields.SecurityAltID.TAG)
				// public static class NoSecurityAltID extends QFComponent
				groupCountField = groupClass.getAnnotation(QFGroupDef.class).countField(); // countField = net.kem.newtquickfix.v50sp2.fields.NoSecurityAltID.class
				groupDelimiterField = groupClass.getAnnotation(QFGroupDef.class).delimiterField(); // countField = net.kem.newtquickfix.v50sp2.fields.NoSecurityAltID.class
				groupMemberCtor = groupClass.getDeclaredMethod("getInstance");// NoSecurityAltID.getInstance();
//					final Method putResult = MESSAGE_FIELD_PARENTSETTER.put(message, (Class<QFField>) groupCountField, possibleSetterMethod);
//					buildFieldParentSetters(message, groupClass);
			} else {
				String msg = "There is no @QFMember annotation for the " + componentField.getName() + " member in class " + componentClass.getName();
				LoggerUtil.getLogger().severe(msg);
				throw new NoSuchElementException(msg);
			}
		} catch (NoSuchElementException e) {
			LoggerUtil.getLogger().severe("There is no abc member that corresponds to setAbc(List<Abc> abc) method in class " + componentClass.getName());
			throw e;
		}
	}

//	@Override
//	Class<?> getOwnerClass() {
//		return groupMemberPopulator == null?
//				ownerClass:
//				groupMemberPopulator.ownerClass;
//	}

	@Override
	protected void populate(@NotNull QFComponent owner, @NotNull Object groupField, Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		final Class<? extends QFField> groupFieldClass = (Class<? extends QFField>) groupField.getClass();
		if(groupFieldClass.equals(groupCountField)) {
			Integer count = (Integer) ((QFField) groupField).getValue();
			setter.invoke(owner, new ArrayList<>(count));
			return;
		}
//		if(groupFieldClass.equals(groupDelimiterField)) {
//			final QFComponent groupMember = (QFComponent) groupMemberCtor.invoke(null);// NoSecurityAltID.getInstance();
//			final List<QFComponent> groupMembers = (List<QFComponent>) groupGetter.invoke(owner); // List<NoSecurityAltID> = getNoSecurityAltID();
//			groupMembers.add(groupMember);
//			if(groupMemberPopulator != null) {
//				groupMemberPopulator.populate(groupMember, groupField, COMPONENT_CLASS_TO_INSTANCE);
//			}
//			COMPONENT_CLASS_TO_INSTANCE.put(groupClass, groupMember);
//			return;
//		}
	}

	void addGroupMember(@NotNull QFComponent owner, @NotNull Map<Class, QFComponent> COMPONENT_CLASS_TO_INSTANCE) throws InvocationTargetException, IllegalAccessException {
		final QFComponent groupMember = (QFComponent) groupMemberCtor.invoke(null);// NoSecurityAltID.getInstance();
		owner = COMPONENT_CLASS_TO_INSTANCE.get(ownerClass);
		final List<QFComponent> groupMembers = (List<QFComponent>) groupGetter.invoke(owner); // List<NoSecurityAltID> = getNoSecurityAltID();
		groupMembers.add(groupMember);
		COMPONENT_CLASS_TO_INSTANCE.put(groupClass, groupMember);
	}

	public Class<? extends QFComponent> getGroupClass() {
		return groupClass;
	}

	Class<? extends QFField> getGroupCountField() {
		return groupCountField;
	}

	Class<? extends QFField> getGroupDelimiterField() {
		return groupDelimiterField;
	}

//	void belongsToGroupMember(Populator groupMemberPopulator) {
//		this.groupMemberPopulator = groupMemberPopulator;
//	}

	@Override
	public String toString() {
		return getOwnerClass().getSimpleName() + "." + setter.getName() + "(<" + groupCountField.getSimpleName() + "> | " + groupCountField.getSimpleName() + " | " + groupDelimiterField.getSimpleName() + ")";
	}
}
