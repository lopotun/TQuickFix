package net.kem.newtquickfix.blocks;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.reflect.ClassPath;
import com.sun.istack.internal.NotNull;
import net.kem.newtquickfix.LiteFixMessageParser;
import net.kem.newtquickfix.QFComponentValidator;
import net.kem.newtquickfix.builders.BuilderUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
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
    private static final Map<Class<? extends QFComponent>, QFComponentValidator> COMPONENT_VALIDATORS = new HashMap<>();
    private static ClassPath classPath;

    private static final Table<CharSequence, Integer, Method> FIX_VERSION_AND_TAG_TO_GETINSTANCE = HashBasedTable.create(5, 200);
    private static final Table<CharSequence, Class<? extends QFField>, Map<Class<? extends QFComponent>, ChildGetterSetter>> FIELD_OWNERS = HashBasedTable.create(5, 1350);
    private static final Table<CharSequence, Class<? extends QFComponent>, List<ChildGetterSetter<? extends QFComponent>>> COMPONENT_CHILDREN = HashBasedTable.create(5, 240);
    private static final Table<CharSequence, Class<? extends QFComponent>, List<ChildGetterSetterGroup<? extends QFComponent>>> GROUP_CHILDREN = HashBasedTable.create(5, 150);
    private static final Table<CharSequence, QFField<String>, Class<? extends QFMessage>> MESSAGE_TYPES = HashBasedTable.create(5, 120);

    static void init() throws IOException {
        if(classPath == null) {
            classPath = ClassPath.from(ClassPath.class.getClassLoader());
        }
    }

    public static ChildGetterSetter getFieldGetterSetter(CharSequence fixVersion, Class<? extends QFField> fieldClass, Class<? extends QFComponent> compClass) {
        ChildGetterSetter getterSetter = null;
        Map<Class<? extends QFComponent>, ChildGetterSetter> componentClasses = FIELD_OWNERS.get(fixVersion, fieldClass);
        if (componentClasses != null) {
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


    public static void fillMaps() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        init();

        for(Map.Entry<CharSequence, CharSequence> version : BuilderUtils.FIX_VERSIONS.entrySet()) {
            BuilderUtils.updatePackagePath(version.getValue());

            ImmutableSet<ClassPath.ClassInfo> fieldClasses = classPath.getTopLevelClasses(String.valueOf(BuilderUtils.PACKAGE_NAME_FIELDS));
            for (ClassPath.ClassInfo fieldClass : fieldClasses) {
                Class<?> qfFieldClass = fieldClass.load();
                int tagValue = qfFieldClass.getField("TAG").getInt(null);
                Method instantiatorByString = qfFieldClass.getDeclaredMethod("getInstance", String.class, QFComponentValidator.class);
                FIX_VERSION_AND_TAG_TO_GETINSTANCE.put(version.getKey(), tagValue, instantiatorByString);
            }

            Set<ClassPath.ClassInfo> componentsClasses = QFUtils.classPath.getAllClasses().parallelStream().filter(classInfo -> classInfo.getPackageName().equals(BuilderUtils.PACKAGE_NAME_COMPONENTS)).collect(Collectors.toSet());
            Set<ClassPath.ClassInfo> messagesClasses = QFUtils.classPath.getAllClasses().parallelStream().filter(classInfo -> classInfo.getPackageName().equals(BuilderUtils.PACKAGE_NAME_MESSAGES)).collect(Collectors.toSet());
            final Sets.SetView<ClassPath.ClassInfo> annotatedClasses = Sets.union(componentsClasses, messagesClasses);
            for (ClassPath.ClassInfo annotatedClass : annotatedClasses) {
                Class<? extends QFComponent> newQFComponentClass = (Class<? extends QFComponent>) annotatedClass.load();
                mapFieldOwners(version.getKey(), newQFComponentClass);
            }

            // Create Message type mapping.
            for (ClassPath.ClassInfo messageClass : messagesClasses) {
                final Class<? extends QFMessage> load = (Class<? extends QFMessage>) messageClass.load();
                if(!load.isInterface() && QFMessage.class.isAssignableFrom(load)) {
                    final Method getMsgType = load.getDeclaredMethod("getMsgType");
                    final QFField<String> msgType = (QFField<String>)getMsgType.invoke(null);
                    MESSAGE_TYPES.put(version.getKey(), msgType, load);
                }
            }
        }
    }

    private static void mapFieldOwners(CharSequence fixVersion, Class<? extends QFComponent> newQFComponentClass) throws NoSuchMethodException {
        Field[] declaredFields = newQFComponentClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            QFMember annotation = declaredField.getAnnotation(QFMember.class);
            if (annotation != null) {
                switch (annotation.type()) {
                    case FIELD: {
                        Class<? extends QFField> fieldClass = (Class<? extends QFField>) declaredField.getType();
                        Map<Class<? extends QFComponent>, ChildGetterSetter> componentClasses = FIELD_OWNERS.get(fixVersion, fieldClass);
                        if (componentClasses == null) {
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
                    case COMPONENT: {
                        Class<? extends QFComponent> componentChildClass = (Class<? extends QFComponent>) declaredField.getType();
                        Method getter = newQFComponentClass.getDeclaredMethod("get" + componentChildClass.getSimpleName());
                        Method setter = newQFComponentClass.getDeclaredMethod("set" + componentChildClass.getSimpleName(), componentChildClass);
                        ChildGetterSetter<? extends QFComponent> childGetterSetter = new ChildGetterSetter(componentChildClass, getter, setter);
                        List<ChildGetterSetter<? extends QFComponent>> componentChildrenSetters = COMPONENT_CHILDREN.get(fixVersion, newQFComponentClass);
                        if (componentChildrenSetters == null) {
                            componentChildrenSetters = new LinkedList<>();
                            COMPONENT_CHILDREN.put(fixVersion, newQFComponentClass, componentChildrenSetters);
                        }
                        if (!componentChildrenSetters.contains(childGetterSetter)) {
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
                        if (groupChildrenSetters == null) {
                            groupChildrenSetters = new LinkedList<>();
                            GROUP_CHILDREN.put(fixVersion, newQFComponentClass, groupChildrenSetters);
                        }
                        if (!groupChildrenSetters.contains(childGroupSetter)) {
                            groupChildrenSetters.add(childGroupSetter);
                        }
                    }
                    break;
                }
            }
        }
    }

    public static Class<? extends QFMessage> getMessageClass(Stack<QFField> tags) {
        final QFField<String> beginString = (QFField<String>)tags.get(tags.size()-1);
        final QFField<String> msgType = (QFField<String>)tags.get(tags.size()-3);
        return MESSAGE_TYPES.get(beginString.getValue(), msgType);
    }

    public static QFField lookupField(CharSequence fixVersion, QFTag tag, QFComponentValidator componentValidator) {
        QFField res = null;
        Method getInstance = FIX_VERSION_AND_TAG_TO_GETINSTANCE.get(fixVersion, tag.getTagKey());
        if (getInstance != null) {
            try {
                res = (QFField) getInstance.invoke(null, tag.getTagValue(), componentValidator);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Unknown tag.
            res = new UnknownTag(tag);
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
                        if(startsWith.equals("set") && method.getParameterCount()==1) {
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
                        merge((QFComponent)newValue, (QFComponent)currentValue);
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

    public static QFComponentValidator getComponentValidator(Class<? extends QFComponent> componentClass) {
        QFComponentValidator validationErrorsHandler = COMPONENT_VALIDATORS.get(componentClass);
        return validationErrorsHandler ==null? LiteFixMessageParser.getInstance().getComponentValidator(): validationErrorsHandler;
    }

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

        public UnknownTag(QFTag tag) {
            this.value = tag.getTagValue();
            this.number = tag.getTagKey();
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
}
