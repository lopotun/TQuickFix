package net.kem.newtquickfix.blocks;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.reflect.ClassPath;
import com.sun.istack.internal.NotNull;
import net.kem.newtquickfix.LoggerUtil;
import net.kem.newtquickfix.QFComponentValidator;
import net.kem.newtquickfix.builders.BuilderUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//import net.kem.newtquickfix.components.StandardHeader;
//import net.kem.newtquickfix.components.StandardTrailer;
//import net.kem.newtquickfix.fields.BeginString;
//import net.kem.newtquickfix.fields.MsgType;

/**
 * Created by Evgeny Kurtser on 12/27/2015 at 9:25 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFUtils {
	//    private static final Map<Class<? extends QFComponent>, QFComponentValidator> COMPONENT_VALIDATORS = new HashMap<>();
	private static ClassPath classPath;

	private static Table<CharSequence, Integer, Method> FIX_VERSION_AND_TAG_TO_GETINSTANCE = HashBasedTable.create(5, 200);
	private static Table<CharSequence, Class<? extends QFField>, Map<Class<? extends QFComponent>, ChildGetterSetter>> FIELD_OWNERS = HashBasedTable.create(5, 1350);
	private static Table<CharSequence, Class<? extends QFComponent>, List<ChildGetterSetter<? extends QFComponent>>> COMPONENT_CHILDREN = HashBasedTable.create(5, 240);
	private static Table<CharSequence, Class<? extends QFComponent>, List<ChildGetterSetterGroup<? extends QFComponent>>> GROUP_CHILDREN = HashBasedTable.create(5, 150);
	private static Table<CharSequence, QFField<String>, Class<? extends QFMessage>> MESSAGE_TYPES = HashBasedTable.create(5, 120);

	static void init() throws IOException {
		if(classPath == null) {
			classPath = ClassPath.from(ClassPath.class.getClassLoader());
		}
	}

	public static ChildGetterSetter getFieldGetterSetter(CharSequence fixVersion, Class<? extends QFField> fieldClass, Class<? extends QFComponent> compClass) {
		ChildGetterSetter getterSetter = null;
		Map<Class<? extends QFComponent>, ChildGetterSetter> componentClasses = FIELD_OWNERS.get(fixVersion, fieldClass);
		if(componentClasses != null) {
			getterSetter = componentClasses.get(compClass);
		}
		return getterSetter;
	}

	public static List<ChildGetterSetter<? extends QFComponent>> getChildrenComponentClasses(CharSequence fixVersion, Class<? extends QFComponent> compClass) {
		return COMPONENT_CHILDREN.get(fixVersion, compClass);
	}

	public static List<ChildGetterSetterGroup<? extends QFComponent>> getChildrenGroupClasses(CharSequence fixVersion, Class<? extends QFComponent> compClass) {
		List<ChildGetterSetterGroup<? extends QFComponent>> groupChildrenClasses = GROUP_CHILDREN.get(fixVersion, compClass);
		return groupChildrenClasses;
	}


	public static void initMaps() throws InvocationTargetException, NoSuchMethodException, IOException, IllegalAccessException, NoSuchFieldException {
		fillMaps();
		storeMaps();
	}

	public static void storeMaps() {
		try {
			ObjectOutputStream oos;
//		    oos = new ObjectOutputStream(new FileOutputStream(new File("FIX_VERSION_AND_TAG_TO_GETINSTANCE.dat")));
//		    oos.writeObject(FIX_VERSION_AND_TAG_TO_GETINSTANCE);
//		    oos.close();
//
//			oos = new ObjectOutputStream(new FileOutputStream(new File("FIELD_OWNERS.dat")));
//			oos.writeObject(FIELD_OWNERS);
//			oos.close();
//
//			oos = new ObjectOutputStream(new FileOutputStream(new File("COMPONENT_CHILDREN.dat")));
//			oos.writeObject(COMPONENT_CHILDREN);
//			oos.close();
//
//			oos = new ObjectOutputStream(new FileOutputStream(new File("GROUP_CHILDREN.dat")));
//			oos.writeObject(GROUP_CHILDREN);
//			oos.close();

			oos = new ObjectOutputStream(new FileOutputStream(new File("MESSAGE_TYPES.dat")));
			oos.writeObject(MESSAGE_TYPES);
			oos.close();
		} catch (IOException e) {
			LoggerUtil.getLogger().warning("Could not store LiteFix static data due to " + e.getMessage());
		}
	}

	private static Object loadStaticResource(String pathname) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(pathname)));) {
			return ois.readObject();
		}
	}

	private static void fillMaps() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
		// Try to load maps.
		boolean isFIX_VERSION_AND_TAG_TO_GETINSTANCELoaded = false;
//		try {
//			FIX_VERSION_AND_TAG_TO_GETINSTANCE = (Table<CharSequence, Integer, Method>) loadStaticResource("FIX_VERSION_AND_TAG_TO_GETINSTANCE.dat");
//			isFIX_VERSION_AND_TAG_TO_GETINSTANCELoaded = true;
// 		    LoggerUtil.getLogger().fine("FIX_VERSION_AND_TAG_TO_GETINSTANCE has been restored.");
//		} catch (IOException | ClassNotFoundException e) {
//			LoggerUtil.getLogger().info("FIELD_OWNERS.dat cannot be used as resource. Will be recalculated.");
//		}
//
		boolean isFIELD_OWNERSLoaded = false;
//		try {
//			FIELD_OWNERS = (Table<CharSequence, Class<? extends QFField>, Map<Class<? extends QFComponent>, ChildGetterSetter>>) loadStaticResource("FIELD_OWNERS.dat");
//			isFIELD_OWNERSLoaded = true;
//		} catch (IOException | ClassNotFoundException e) {
//			LoggerUtil.getLogger().info("FIELD_OWNERS.dat cannot be used as resource. Will be recalculated.");
//		}
//
		boolean isCOMPONENT_CHILDRENLoaded = false;
//		try {
//			COMPONENT_CHILDREN = (Table<CharSequence, Class<? extends QFComponent>, List<ChildGetterSetter<? extends QFComponent>>>) loadStaticResource("COMPONENT_CHILDREN.dat");
//			isCOMPONENT_CHILDRENLoaded = true;
// 		    LoggerUtil.getLogger().fine("COMPONENT_CHILDREN has been restored.");
//		} catch (IOException | ClassNotFoundException e) {
//			LoggerUtil.getLogger().info("COMPONENT_CHILDREN.dat cannot be used as resource. Will be recalculated.");
//		}
//
		boolean isGROUP_CHILDRENLoaded = false;
//		try {
//			GROUP_CHILDREN = (Table<CharSequence, Class<? extends QFComponent>, List<ChildGetterSetterGroup<? extends QFComponent>>>) loadStaticResource("GROUP_CHILDREN.dat");
//			isGROUP_CHILDRENLoaded = true;
//		    LoggerUtil.getLogger().fine("GROUP_CHILDREN has been restored.");
//		} catch (IOException | ClassNotFoundException e) {
//			LoggerUtil.getLogger().info("GROUP_CHILDREN.dat cannot be used as resource. Will be recalculated.");
//		}

		boolean isMESSAGE_TYPESLoaded = false;
		try {
			MESSAGE_TYPES = (Table<CharSequence, QFField<String>, Class<? extends QFMessage>>) loadStaticResource("MESSAGE_TYPES.dat");
			isMESSAGE_TYPESLoaded = true;
			LoggerUtil.getLogger().fine("MESSAGE_TYPES has been restored.");
		} catch (IOException | ClassNotFoundException e) {
			LoggerUtil.getLogger().info("MESSAGE_TYPES.dat cannot be used as resource. Will be recalculated.");
		}

		init();

		// Look at classes in classpath to find supported LiteFix versions.
		Set<String> packageLFVersions = classPath.getTopLevelClasses().parallelStream().filter(classInfo -> classInfo.getPackageName().startsWith("net.kem.newtquickfix.v"))
				.map(ClassPath.ClassInfo::getPackageName)
				.map(s -> s.substring("net.kem.newtquickfix.v".length() - 1))
				.map(s -> s.substring(0, s.indexOf('.')))
				.collect(Collectors.toSet());

		for (String packageVersion : packageLFVersions) {
			CharSequence liteFixVersion = BuilderUtils.FIXVersion.getFixVersionByPackageVersion(packageVersion);
			if(liteFixVersion == null) {
				liteFixVersion = packageVersion;
			}
			LoggerUtil.getLogger().fine("Calculating static data for version " + liteFixVersion + " [" + packageVersion + "]...");
			BuilderUtils.updatePackagePath(packageVersion);

			if(!isFIX_VERSION_AND_TAG_TO_GETINSTANCELoaded) {
				LoggerUtil.getLogger().fine("Calculating FIX_VERSION_AND_TAG_TO_GETINSTANCE");
				ImmutableSet<ClassPath.ClassInfo> fieldClasses = classPath.getTopLevelClasses(String.valueOf(BuilderUtils.PACKAGE_NAME_FIELDS));
				for (ClassPath.ClassInfo fieldClass : fieldClasses) {
					Class<?> qfFieldClass = fieldClass.load();
					int tagValue = qfFieldClass.getField("TAG").getInt(null);
					Method instantiatorByString = qfFieldClass.getDeclaredMethod("getInstance", String.class, QFComponentValidator.class);
					FIX_VERSION_AND_TAG_TO_GETINSTANCE.put(liteFixVersion, tagValue, instantiatorByString);
				}
			}

			Set<ClassPath.ClassInfo> messagesClasses = null;
			if(!(isFIELD_OWNERSLoaded && isCOMPONENT_CHILDRENLoaded && isGROUP_CHILDRENLoaded)) {
				LoggerUtil.getLogger().fine("Calculating FIELD_OWNERS, COMPONENT_CHILDREN, GROUP_CHILDREN");
				Set<ClassPath.ClassInfo> componentsClasses = QFUtils.classPath.getAllClasses().parallelStream().filter(classInfo -> classInfo.getPackageName().equals(BuilderUtils.PACKAGE_NAME_COMPONENTS)).collect(Collectors.toSet());
				messagesClasses = QFUtils.classPath.getAllClasses().parallelStream().filter(classInfo -> classInfo.getPackageName().equals(BuilderUtils.PACKAGE_NAME_MESSAGES)).collect(Collectors.toSet());
				final Sets.SetView<ClassPath.ClassInfo> annotatedClasses = Sets.union(componentsClasses, messagesClasses);
				for (ClassPath.ClassInfo annotatedClass : annotatedClasses) {
					Class<? extends QFComponent> newQFComponentClass = (Class<? extends QFComponent>) annotatedClass.load();
					mapFieldOwners(liteFixVersion, newQFComponentClass);
				}
			}

			if(!isMESSAGE_TYPESLoaded) {
				LoggerUtil.getLogger().fine("Calculating MESSAGE_TYPES");
				if(messagesClasses == null) {
					messagesClasses = QFUtils.classPath.getAllClasses().parallelStream().filter(classInfo -> classInfo.getPackageName().equals(BuilderUtils.PACKAGE_NAME_MESSAGES)).collect(Collectors.toSet());
				}
				// Create Message type mapping.
				for (ClassPath.ClassInfo messageClass : messagesClasses) {
					final Class<? extends QFMessage> load = (Class<? extends QFMessage>) messageClass.load();
					if(!load.isInterface() && !load.getSimpleName().equals("AMessage") && QFMessage.class.isAssignableFrom(load)) {
						final Method getMsgType = load.getDeclaredMethod("getMsgType");
						final QFField<String> msgType = (QFField<String>) getMsgType.invoke(null);
						MESSAGE_TYPES.put(liteFixVersion, msgType, load);
					}
				}
			}
			LoggerUtil.getLogger().fine("Calculating static data for version " + liteFixVersion + " [" + packageVersion + "] done.");
		}
	}

	private static void mapFieldOwners(CharSequence fixVersion, Class<? extends QFComponent> newQFComponentClass) throws NoSuchMethodException {
//		Field[] declaredFields = newQFComponentClass.getDeclaredFields();
		Set<Field> declaredFields = getAllFields(newQFComponentClass);

		for (Field declaredField : declaredFields) {
			QFMember annotation = declaredField.getAnnotation(QFMember.class);
			if(annotation != null) {
				switch (annotation.type()) {
					case FIELD: {
						Class<? extends QFField> fieldClass = (Class<? extends QFField>) declaredField.getType();
						Map<Class<? extends QFComponent>, ChildGetterSetter> componentClasses = FIELD_OWNERS.get(fixVersion, fieldClass);
						if(componentClasses == null) {
							componentClasses = new HashMap<>();
							FIELD_OWNERS.put(fixVersion, fieldClass, componentClasses);
						}
						Method getter = newQFComponentClass.getDeclaredMethod("get" + fieldClass.getSimpleName());
						Method setter = newQFComponentClass.getDeclaredMethod("set" + fieldClass.getSimpleName(), fieldClass);
						ChildGetterSetter<? extends QFComponent> childGetterSetter = new ChildGetterSetter(fieldClass, getter, setter);
						componentClasses.put(newQFComponentClass, childGetterSetter);
					}
					break;
					// @annotation(type = annotation.Type.COMPONENT) public void setComponentC(ComponentC componentC)
					case COMPONENT:
					case MESSAGE: {
						Class<? extends QFComponent> componentChildClass = (Class<? extends QFComponent>) declaredField.getType();
						Method getter = newQFComponentClass.getMethod("get" + componentChildClass.getSimpleName());
						Method setter = newQFComponentClass.getMethod("set" + componentChildClass.getSimpleName(), componentChildClass);
						ChildGetterSetter<? extends QFComponent> childGetterSetter = new ChildGetterSetter(componentChildClass, getter, setter);
						List<ChildGetterSetter<? extends QFComponent>> componentChildrenSetters = COMPONENT_CHILDREN.get(fixVersion, newQFComponentClass);
						if(componentChildrenSetters == null) {
							componentChildrenSetters = new LinkedList<>();
							COMPONENT_CHILDREN.put(fixVersion, newQFComponentClass, componentChildrenSetters);
						}
						if(!componentChildrenSetters.contains(childGetterSetter)) {
							componentChildrenSetters.add(childGetterSetter);
						}
					}
					break;
					// @annotation(type = annotation.Type.GROUP, groupClass = ComponentMain.GroupA.class) public void setGroupA(List<GroupA> groupA)
					case GROUP: {
						Class<? extends QFComponent> groupChildClass = annotation.groupClass();
						mapFieldOwners(fixVersion, groupChildClass);
						Method getter = newQFComponentClass.getDeclaredMethod("get" + groupChildClass.getSimpleName());
						Method setter = newQFComponentClass.getDeclaredMethod("set" + groupChildClass.getSimpleName(), List.class);
						// @QFGroupDef(count = FieldIntegerGroupCount.TAG, delimiter = FieldStringGroupDelimiter.TAG) public static class GroupA extends QFComponent
						QFGroupDef groupAnnotation = groupChildClass.getAnnotation(QFGroupDef.class);
						ChildGetterSetterGroup<? extends QFComponent> childGroupSetter = new ChildGetterSetterGroup(groupChildClass, getter, setter, groupAnnotation.count(), groupAnnotation.delimiter());
						List<ChildGetterSetterGroup<? extends QFComponent>> groupChildrenSetters = GROUP_CHILDREN.get(fixVersion, newQFComponentClass);
						if(groupChildrenSetters == null) {
							groupChildrenSetters = new LinkedList<>();
							GROUP_CHILDREN.put(fixVersion, newQFComponentClass, groupChildrenSetters);
						}
						if(!groupChildrenSetters.contains(childGroupSetter)) {
							groupChildrenSetters.add(childGroupSetter);
						}
					}
					break;
				}
			}
		}
	}

	private static Set<Field> getAllFields(Class<?> type) {
		Set<Field> fields = new HashSet<>(Arrays.asList(type.getDeclaredFields()));
		final Class<?> superclass = type.getSuperclass();
		if (superclass != null && QFMessage.class.isAssignableFrom(superclass)) {
			final Set<Field> headerTrailer =
					Sets.newHashSet(superclass.getDeclaredFields()).parallelStream().
					filter(field -> (field.getName().equals("standardHeader") || field.getName().equals("standardTrailer")))
					.collect(Collectors.toSet());
			fields.addAll(headerTrailer);
		}
		return fields;
	}

	public static Class<? extends QFMessage> getMessageClass(Deque<QFField> tags) {
		final QFField beginString = tags.peek();
		final QFField msgType = ((LinkedList<QFField>)tags).get(2);
		Class<? extends QFMessage> res = MESSAGE_TYPES.get(beginString.getValue(), msgType);
		if(res == null) {
			throw new UnsupportedOperationException("Message type " + msgType.toString() + " is not defined in FIX version " + beginString.getValue());
		}
		return res;
	}

//	public static QFField lookupField(CharSequence fixVersion, QFTag tag, QFComponentValidator componentValidator) {
//		QFField res = null;
//		Method methodGetInstance = FIX_VERSION_AND_TAG_TO_GETINSTANCE.get(fixVersion, tag.getTagKey());
//		if(methodGetInstance != null) {
//			try {
//				res = (QFField) methodGetInstance.invoke(null, tag.getTagValue(), componentValidator);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
//			// Unknown tag.
//			res = new UnknownTag(tag);
//		}
//		return res;
//	}

	public static QFField lookupField(CharSequence fixVersion, String tagKey, String tagValue, QFComponentValidator componentValidator) {
		QFField res = null;
		int iTagKey = Integer.parseInt(tagKey);
		Method methodGetInstance = FIX_VERSION_AND_TAG_TO_GETINSTANCE.get(fixVersion, iTagKey);
		if(methodGetInstance != null) {
			try {
				res = (QFField) methodGetInstance.invoke(null, tagValue, componentValidator);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// Unknown tag.
			res = new UnknownTag(iTagKey, tagValue);
		}
		return res;
	}


	static void merge(QFComponent from, QFComponent to) throws InvocationTargetException, IllegalAccessException {
		Map<CharSequence, MutablePair<Method, Method>> methodMap = new HashMap<>();
		List<Method> methodsFrom = new ArrayList<>(Arrays.asList(from.getClass().getMethods()));
		List<Method> superClassMethodsFrom = new ArrayList<>(Arrays.asList(from.getClass().getSuperclass().getMethods()));
		methodsFrom.removeAll(superClassMethodsFrom);
		for (Method method : methodsFrom) {
			final String methodName = method.getName();
			if(methodName.length() > 3) {
				final String startsWith = methodName.substring(0, 3);
				final String varName = methodName.substring(3);
				if(!varName.equals("Instance")) {
					if(startsWith.equals("get")) {
						fillMethodsMap(methodMap, varName, method, true);
					} else {
						if(startsWith.equals("set") && method.getParameterCount() == 1) {
							fillMethodsMap(methodMap, varName, method, false);
						}
					}
				}
			}
		}

		for (Map.Entry<CharSequence, MutablePair<Method, Method>> methods : methodMap.entrySet()) {
			Method getter = methods.getValue().getLeft();
			Method setter = methods.getValue().getRight();

			final Object newValue = getter.invoke(from);
			if(newValue != null) {
				final Object currentValue = getter.invoke(to);
				if(currentValue == null) {
					setter.invoke(to, newValue);
				} else {
					final Class<?> returnType = getter.getReturnType();
					if(QFComponent.class.isAssignableFrom(returnType)) {
						merge((QFComponent) newValue, (QFComponent) currentValue);
					}
				}
			}
		}
	}

	private static void fillMethodsMap(@NotNull final Map<CharSequence, MutablePair<Method, Method>> methodMap, @NotNull final String varName, @NotNull final Method method, boolean isGetter) {
		MutablePair<Method, Method> getterSetterPair = methodMap.get(varName);
		if(getterSetterPair == null) {
			getterSetterPair = new MutablePair<>();
			methodMap.put(varName, getterSetterPair);
		}
		if(isGetter) {
			getterSetterPair.setLeft(method);
		} else {
			getterSetterPair.setRight(method);
		}
	}

//    public static QFComponentValidator getComponentValidator(Class<? extends QFComponent> componentClass) {
//        QFComponentValidator validationErrorsHandler = COMPONENT_VALIDATORS.get(componentClass);
//        return validationErrorsHandler ==null? LiteFixMessageParser.getComponentValidator(): validationErrorsHandler;
//    }

	public static class ChildGetterSetter<T> {
		enum ElementType {FIELD, COMPONENT, GROUP}

		private Class<T> childClass;
		private Method getter;
		private Method setter;

		public ChildGetterSetter(Class<T> childClass, Method getter, Method setter) {
			this.childClass = childClass;
			this.getter = getter;
			this.setter = setter;
		}

		public Class<T> getChildClass() {
			return childClass;
		}

		public Method getGetter() {
			return getter;
		}

		public Method getSetter() {
			return setter;
		}

		public ElementType getElementType() {
			return ElementType.FIELD;
		}

		@Override
		public String toString() {
			return "{" + "childClass=" + childClass.getSimpleName() + ", setter=" + setter + '}';
		}
	}

	public static class ChildGetterSetterGroup<T> extends ChildGetterSetter<T> {
		private int groupCount;
		private int groupDelimiter;

		public ChildGetterSetterGroup(Class<T> childClass, Method getter, Method setter, int groupCount, int groupDelimiter) {
			super(childClass, getter, setter);
			this.groupCount = groupCount;
			this.groupDelimiter = groupDelimiter;
		}

		@Override
		public ElementType getElementType() {
			return ElementType.GROUP;
		}

		public int getGroupCount() {
			return groupCount;
		}

		public int getGroupDelimiter() {
			return groupDelimiter;
		}

		@Override
		public String toString() {
			return super.toString() + ", {" + "groupCount=" + groupCount + ", groupDelimiter=" + groupDelimiter + '}';
		}
	}


	public static class UnknownTag extends QFField<String> {
		private int number;

		public UnknownTag(int tagKey, String tagValue) {
			this.value = tagValue;
			this.number = tagKey;
		}

		public UnknownTag(QFTag tag) {
			this.value = tag.getTagValue();
			this.number = tag.getTagKey();
		}

		public UnknownTag(QFField field) {
			this.value = field.getValue()==null? null: field.getValue().toString();
			this.number = field.getTag();
		}

		@SuppressWarnings("unused")
		private UnknownTag() {
			// This default constructor is added solely to conform the most of JSON frameworks' requirement.
		}

		@Override
		public int getTag() {
			return number;
		}

		@Override
		public boolean isKnown() {
			return false;
		}
	}


	public static int calculateCheckSum(CharSequence sb) {
		int total = 0;
		for(int i=0; i<sb.length(); i++) {
			total += sb.charAt(i);
		}
		return total % 256;
	}
}
