package net.kem.newtquickfix.blocks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import net.kem.newtquickfix.builders.BuilderUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Evgeny Kurtser on 12/27/2015 at 9:25 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFUtils {
    private static final Map<Class<? extends QFField<?>>, ValidationHandler> FIELD_VALIDATORS = new HashMap<>();
    static final Map<Class<? extends QFComponent>, ValidationHandler> MESSAGE_VALIDATORS = new HashMap<>();
    static ClassPath classPath;

    static final Map<Integer, Method> MAP = new HashMap<>();
    private static final Map<Class<? extends QFField>, Map<Class<? extends QFComponent>, ChildGetterSetter>> FIELD_OWNERS = new HashMap<>();
    private static final Map<Class<? extends QFComponent>, List<ChildGetterSetter<? extends QFComponent>>> COMPONENT_CHILDREN = new HashMap<>();
    private static final Map<Class<? extends QFComponent>, List<ChildGetterSetterGroup<? extends QFComponent>>> GROUP_CHILDREN = new HashMap<>();


    static void init() throws IOException {
        if(classPath == null) {
            classPath = ClassPath.from(ClassPath.class.getClassLoader());
        }
    }

    public static ChildGetterSetter getFieldGetterSetter(Class<? extends QFField> fieldClass, Class<? extends QFComponent> compClass) {
        ChildGetterSetter getterSetter = null;
        Map<Class<? extends QFComponent>, ChildGetterSetter> componentClasses = FIELD_OWNERS.get(fieldClass);
        if (componentClasses != null) {
            getterSetter = componentClasses.get(compClass);
        }
        return getterSetter;
    }

    public static List<ChildGetterSetter<? extends QFComponent>> getChildrenComponentClasses(Class<? extends QFComponent> compClass) {
        List<ChildGetterSetter<? extends QFComponent>> componentChildrenClasses = COMPONENT_CHILDREN.get(compClass);
        return componentChildrenClasses;
    }

    public static List<ChildGetterSetterGroup<? extends QFComponent>> getChildrenGroupClasses(Class<? extends QFComponent> compClass) {
        List<ChildGetterSetterGroup<? extends QFComponent>> groupChildrenClasses = GROUP_CHILDREN.get(compClass);
        return groupChildrenClasses;
    }


    public static void fillMaps() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException {
        init();
        ImmutableSet<ClassPath.ClassInfo> fieldClasses = classPath.getTopLevelClasses(String.valueOf(BuilderUtils.PACKAGE_NAME_FIELDS));
        for (ClassPath.ClassInfo fieldClass : fieldClasses) {
            Class<?> qfFieldClass = fieldClass.load();
            int tagValue = qfFieldClass.getField("TAG").getInt(null);
            Method instantiatorByString = qfFieldClass.getDeclaredMethod("getInstance", String.class);
            MAP.put(tagValue, instantiatorByString);
        }

        Set<ClassPath.ClassInfo> annotatedComponentClasses = QFUtils.classPath.getTopLevelClasses(String.valueOf(BuilderUtils.PACKAGE_NAME_COMPONENTS));
        Set<ClassPath.ClassInfo> annotatedMessagesClasses = QFUtils.classPath.getTopLevelClasses(String.valueOf(BuilderUtils.PACKAGE_NAME_MESSAGES));
        final Sets.SetView<ClassPath.ClassInfo> annotatedClasses = Sets.union(annotatedComponentClasses, annotatedMessagesClasses);
        for (ClassPath.ClassInfo annotatedClass : annotatedClasses) {
            Class<? extends QFComponent> newQFComponentClass = (Class<? extends QFComponent>) annotatedClass.load();
            Field[] declaredFields = newQFComponentClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                QFMember QFMember = declaredField.getAnnotation(QFMember.class);
                if (QFMember != null) {
                    switch (QFMember.type()) {
                        case FIELD: {
                            Class<? extends QFField> fieldClass = (Class<? extends QFField>) declaredField.getType();
                            Map<Class<? extends QFComponent>, ChildGetterSetter> componentClasses = FIELD_OWNERS.get(fieldClass);
                            if (componentClasses == null) {
                                componentClasses = new HashMap<>();
                                FIELD_OWNERS.put(fieldClass, componentClasses);
                            }
                            Method getter = newQFComponentClass.getDeclaredMethod("get" + fieldClass.getSimpleName());
                            Method setter = newQFComponentClass.getDeclaredMethod("set" + fieldClass.getSimpleName(), fieldClass);
                            ChildGetterSetter<? extends QFComponent> childGetterSetter = new ChildGetterSetter(fieldClass, getter, setter);
                            componentClasses.put(newQFComponentClass, childGetterSetter);
                        }
                        break;
                        // @QFMember(type = QFMember.Type.COMPONENT) public void setComponentC(ComponentC componentC)
                        case COMPONENT: {
                            Class<? extends QFComponent> componentChildClass = (Class<? extends QFComponent>) declaredField.getType();
                            Method getter = newQFComponentClass.getDeclaredMethod("get" + componentChildClass.getSimpleName());
                            Method setter = newQFComponentClass.getDeclaredMethod("set" + componentChildClass.getSimpleName(), componentChildClass);
                            ChildGetterSetter<? extends QFComponent> childGetterSetter = new ChildGetterSetter(componentChildClass, getter, setter);
                            List<ChildGetterSetter<? extends QFComponent>> componentChildrenSetters = COMPONENT_CHILDREN.get(newQFComponentClass);
                            if (componentChildrenSetters == null) {
                                componentChildrenSetters = new LinkedList<>();
                                COMPONENT_CHILDREN.put(newQFComponentClass, componentChildrenSetters);
                            }
                            if (!componentChildrenSetters.contains(childGetterSetter)) {
                                componentChildrenSetters.add(childGetterSetter);
                            }
                        }
                        break;
                        // @QFMember(type = QFMember.Type.GROUP, groupClass = ComponentMain.GroupA.class) public void setGroupA(List<GroupA> groupA)
                        case GROUP: {
                            Class<? extends QFComponent> groupChildClass = QFMember.groupClass();
                            Method getter = newQFComponentClass.getDeclaredMethod("get" + groupChildClass.getSimpleName());
                            Method setter = newQFComponentClass.getDeclaredMethod("set" + groupChildClass.getSimpleName(), List.class);
                            // @QFGroupDef(count = FieldIntegerGroupCount.TAG, delimiter = FieldStringGroupDelimiter.TAG) public static class GroupA extends QFComponent
                            QFGroupDef groupAnnotation = groupChildClass.getAnnotation(QFGroupDef.class);
                            ChildGetterSetterGroup<? extends QFComponent> childGroupSetter = new ChildGetterSetterGroup(groupChildClass, getter, setter, groupAnnotation.count(), groupAnnotation.delimiter());
                            List<ChildGetterSetterGroup<? extends QFComponent>> groupChildrenSetters = GROUP_CHILDREN.get(newQFComponentClass);
                            if (groupChildrenSetters == null) {
                                groupChildrenSetters = new LinkedList<>();
                                GROUP_CHILDREN.put(newQFComponentClass, groupChildrenSetters);
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
    }

    public static QFField lookupField(QFTag tag) {
        QFField res = null;
        Method getInstance = MAP.get(tag.getTagKey());
        if (getInstance != null) {
            try {
                res = (QFField) getInstance.invoke(null, tag.getTagValue());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            // Unknown tag.
            res = new UnknownTag(tag);
        }
        return res;
    }

    public static <V> ValidationHandler<V> getValidationHandler(Class<? extends QFField<?>> fieldClass) {
        ValidationHandler<V> validationHandler = FIELD_VALIDATORS.get(fieldClass);
        if(QFDateTimeField.class.isAssignableFrom(fieldClass)) {
            validationHandler = ValidationHandler.Temporals.VALIDATION_HANDLER_WARNING;
        }
        if(QFDateField.class.isAssignableFrom(fieldClass)) {
            validationHandler = ValidationHandler.Temporals.VALIDATION_HANDLER_WARNING;
        }
        if(QFTimeField.class.isAssignableFrom(fieldClass)) {
            validationHandler = ValidationHandler.Temporals.VALIDATION_HANDLER_WARNING;
        }
        if(QFMonthYearField.class.isAssignableFrom(fieldClass)) {
            validationHandler = ValidationHandler.Temporals.VALIDATION_HANDLER_WARNING;
        }
        return validationHandler==null? ValidationHandler.Numbers.VALIDATION_HANDLER_WARNING: validationHandler;
    }

    // final Integer integer = QFFieldUtils.<Integer>handleError(AllocTransType.class, "abcd", new NumberFormatException("Bad abcd"));
    public static <V> V handleError(Class<? extends QFField<?>> fieldClass, Object problematicValue, Throwable t) {
        return null;
    }

    public static <V> ValidationHandler<V> getMessageValidationHandler(Class<? extends QFComponent> fieldClass) {
        final ValidationHandler<V> validationHandler = MESSAGE_VALIDATORS.get(fieldClass);
        return validationHandler==null? ValidationHandler.Numbers.VALIDATION_HANDLER_WARNING: validationHandler;
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