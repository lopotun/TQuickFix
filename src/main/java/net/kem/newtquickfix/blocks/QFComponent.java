package net.kem.newtquickfix.blocks;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeny Kurtser
 * Date: 19-Oct-15
 * Time: 8:46 AM
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */

/*
<component name="OrdAllocGrp">
      <group name="NoOrders" required="N">
        <field name="ClOrdID" required="N"/>
        <field name="OrderID" required="N"/>
        <field name="SecondaryOrderID" required="N"/>
        <field name="SecondaryClOrdID" required="N"/>
        <field name="ListID" required="N"/>
        <component name="NestedParties2" required="N"/>
        <field name="OrderQty" required="N"/>
        <field name="OrderAvgPx" required="N"/>
        <field name="OrderBookingQty" required="N"/>
      </group>
    </component>

    <component name="DisplayInstruction">
      <field name="DisplayQty" required="N"/>
      <field name="SecondaryDisplayQty" required="N"/>
      <field name="DisplayWhen" required="N"/>
      <field name="DisplayMethod" required="N"/>
      <field name="DisplayLowQty" required="N"/>
      <field name="DisplayHighQty" required="N"/>
      <field name="DisplayMinIncr" required="N"/>
      <field name="RefreshQty" required="N"/>
    </component>
 */
public abstract class QFComponent {

    public String getName() {
        return getClass().getSimpleName();
    }

    protected static <QFComp extends QFComponent> QFComp getInstance(Stack<QFField> tags, QFComp instance, Class<? extends QFComponent> compClass) {
        // I'll need to know whether the current component is an ordinal component or a group.
        // I rely on the fact that every group has QFGroupDef annotation.
        int groupDelimiterTag = 0;
        QFGroupDef groupAnnotation = compClass.getAnnotation(QFGroupDef.class);
        if(groupAnnotation != null) {
            groupDelimiterTag = groupAnnotation.delimiter();
        }

        NEXT_TAG:
        while (true) {
            if (tags.isEmpty()) {
                return instance;
            }
            QFField qfField = tags.peek();

            // Look for fields.
            QFFieldUtils.ChildGetterSetter childGS = QFFieldUtils.lookupField(qfField.getClass(), compClass);
            if(childGS != null) {
                // Create instance of this component/group if needed.
                if(instance == null) {
                    instance = createInstance(instance, compClass);
                } else {
                    // This instance is a Group and this field is a group element delimiter ->
                    // 1. We're at beginning of next group element.
                    // 2. Stop precessing this group element (and then go to next one).
                    if(groupDelimiterTag != 0 && qfField.getTag() == groupDelimiterTag) {
                        return instance;
                    }
                }
                // Assign field value to the component member.
                try {
                    // Check if the value was not assigned previously.
                    Method fieldGetter = childGS.getGetter();
                    QFField currentValue = (QFField) fieldGetter.invoke(instance);
                    if(currentValue == null) {
                        // Assign field value to the component member by calling a field setter method.
                        Method fieldSetter = childGS.getSetter();
                        fieldSetter.invoke(instance, qfField);
                        // Proceed to next field.
                        tags.pop();
                        continue;
                    } else {
                        // The value has been already assigned to this member.
                        System.out.println("Tag \"" + qfField + "\" will not replace value \"" + currentValue + "\" in class \"" + compClass.getName() + "\". This tag will be used in some other component.");
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            // The current field does not belong to this component. Check if the filed belongs to one of its child components (if any).
            List<QFFieldUtils.ChildGetterSetter<? extends QFComponent>> childrenComponentGSs = QFFieldUtils.getChildrenComponentClasses(compClass);
            if(childrenComponentGSs != null) {
                // Get child component's metadata.
                for (QFFieldUtils.ChildGetterSetter<? extends QFComponent> childrenComponentGS : childrenComponentGSs) {
                    try {
                        // Create (recursively) child component's instance.
                        QFComp childrenComponentInstance = getInstance(tags, null, childrenComponentGS.getChildClass());
                        if (childrenComponentInstance != null) {
                            // Child component instance has been created (that means that the current filed belongs to this child component (or to one of its descenders)).
                            // Create instance of this component if needed.
                            if(instance == null) {
                                instance = createInstance(instance, compClass);
                            } else {
                                // Check if the value was not assigned previously.
                                Method fieldGetter = childrenComponentGS.getGetter();
                                QFComponent currentValue = (QFComponent) fieldGetter.invoke(instance);
                                if(currentValue == null) {
                                    // Assign the value of the newly created child component.
                                    childrenComponentGS.getSetter().invoke(instance, childrenComponentInstance);
                                } else {
                                    // The value has been already assigned to this member.
                                    System.out.println("Component \"" + childrenComponentInstance.getName() + "\" already exists in class \"" + compClass.getName() + "\". Please, check the incoming FIX message for data integrity.");
                                }
                            }
                            // Proceed to next field. Note: do not pop stack since it has been already popped while child component creation.
                            continue NEXT_TAG;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            // The current field belong neither to this component nor to one of its child components. Check if the filed belongs to one of child groups (if any).
            List<QFFieldUtils.ChildGetterSetterGroup<? extends QFComponent>> groupComponentGSs = QFFieldUtils.getChildrenGroupClasses(compClass);
            if(groupComponentGSs != null) {
                // Get child group component metadata.
                for (QFFieldUtils.ChildGetterSetterGroup<? extends QFComponent> groupComponentGS : groupComponentGSs) {
                    try {
                        // Check if this field is a group count.
                        if(qfField.getTag() == groupComponentGS.getGroupCount()) {
                            // This field is is a group count. Pop the stack and create list of group members.
                            tags.pop();
                            int numberOfGroupMembers = Integer.parseInt(qfField.getValue().toString());
                            List<QFComp> groupInstances = new ArrayList<>(numberOfGroupMembers);
                            for (int i=0; i<numberOfGroupMembers; i++) {
                                // Create group instance.
                                QFComp childrenGroupInstance = getInstance(tags, null, groupComponentGS.getChildClass());
                                if (childrenGroupInstance != null) {
                                    groupInstances.add(childrenGroupInstance);
                                }
                            }
                            if(numberOfGroupMembers != groupInstances.size()) {
                                //TODO Issue a warn.
                            }
                            // Assign the value of the newly created child group.
                            if (!groupInstances.isEmpty()) {
                                instance = createInstance(instance, compClass);
                                groupComponentGS.getSetter().invoke(instance, groupInstances);
                            }
                            // Proceed to next field. Note: do not pop stack since it has been already popped while child group creation.
                            continue NEXT_TAG;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            return instance;
        }
    }

    /*protected static <QFComp extends QFComponent> QFComp getInstanceOLD1(Stack<QFTag> tags, QFComp instance, Class<QFComp> compClass) {
        QFField qfField;
        boolean qfFieldAccepted = false;
        ComponentMetadata<QFComp> componentMetadata = getComponentMetadata(compClass);
        while ((qfField = getNextField(tags, true)) != null) {
            // Look for fields.
            qfFieldAccepted = false;
            Method[] fieldSetters = componentMetadata.getFieldSetters();
            if(fieldSetters != null) {
                for (Method fieldSetterMethod : fieldSetters) {
                    if (fieldSetterMethod.getParameterTypes()[0].equals(qfField.getClass())) {
                        if(instance == null) {
                            instance = createInstance(instance, compClass);
                        }
                        try {
                            fieldSetterMethod.invoke(instance, qfField);
                            qfFieldAccepted = true;
                            break;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(qfFieldAccepted) {
                    continue;
                }
            }

            // Look for components.
            Method[] componentSetters = componentMetadata.getComponentSetters();
            if(componentSetters != null) {
                for (Method componentSetterMethod : componentSetters) {
                    try {
                        // Get group component Class.
                        Class<QFComp> componentClass = (Class<QFComp>)componentSetterMethod.getParameterTypes()[0];
                        // Create component instance.
                        QFComp componentInstance = getInstanceOLD1(tags, null, componentClass);
                        if (componentInstance != null) {
                            instance = createInstance(instance, compClass);
                            componentSetterMethod.invoke(instance, componentInstance);
                            qfFieldAccepted = true;
                            break;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                if(qfFieldAccepted) {
                    continue;
                }
            }

            // Look for groups.
            SetterStructure[] setterStructures = componentMetadata.getSetterStructure();
            if(setterStructures != null) {
                for (SetterStructure setterStructure : setterStructures) {
                    if (qfField.getTag() == setterStructure.getGroupCounter()) {
                        // Get number of group members.
                        int numOfGroupMembers = Integer.parseInt(qfField.getValue().toString());
                        List<QFComp> groupMembers = new ArrayList<>(numOfGroupMembers);
                        for(int i=0; i<numOfGroupMembers; i++) {
                            // Get group member Class.
                            Class<QFComp> groupClass = setterStructure.getGroupClazz();
                            // Create group member instance.
                            QFComp component = getInstanceOLD1(tags, null, groupClass);
                            if(component != null) {
                                groupMembers.add(component);
                            }
                        }
                        try {
                            instance = createInstance(instance, compClass);
                            Method groupSetterMethod = setterStructure.getGroupOwnerSetter();
                            groupSetterMethod.invoke(instance, groupMembers);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return instance;
    }*/

    /*private static <QFComp extends QFComponent> ComponentMetadata<QFComp> getComponentMetadata(Class<QFComp> compClass) {
        try {
            Field reflectionField = compClass.getDeclaredField("$METADATA");
            reflectionField.setAccessible(true);
            ComponentMetadata<QFComp> res = (ComponentMetadata<QFComp>)reflectionField.get(null);
            return res;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    /*protected static <QFComp extends QFComponent> QFComp getInstanceOLD(Stack<QFTag> tags, QFComp instance, Class<? extends QFComp> compClass) {
        QFField field = getNextField(tags, false);
        if (field == null) {
            return instance;
        }
        Field reflectionField;

        try {
            reflectionField = compClass.getDeclaredField("$FIELD_SETTERS");
            reflectionField.setAccessible(true);
            Method[] fieldSetterMethods = (Method[]) reflectionField.get(null);
            for (Method fieldSetterMethod : fieldSetterMethods) {
                if (fieldSetterMethod.getParameterTypes()[0].equals(field.getClass())) {
                    instance = createInstance(instance, compClass);
                    try {
                        fieldSetterMethod.invoke(instance, field);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    field = getNextField(tags, true);
                    if (field == null) {
                        return instance;
                    }
                }
            }
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        // Field setter is not found.
        // Look in Components.
        try {
            reflectionField = compClass.getDeclaredField("$COMPONENT_SETTERS");
            reflectionField.setAccessible(true);
            Method[] componentSetterMethods = (Method[]) reflectionField.get(null);
            for (Method componentSetterMethod : componentSetterMethods) {
                Class<? extends QFComponent> componentClass = (Class<? extends QFComponent>) componentSetterMethod.getParameterTypes()[0];
                QFComponent component = getInstanceOLD(tags, instance, componentClass);
                if (component != null) {
                    try {
                        instance = createInstance(instance, compClass);
                        componentSetterMethod.invoke(instance, component);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    field = getNextField(tags, true);
                    if (field == null) {
                        return instance;
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Neither Field nor Component setter are found.
        // Look in Groups.
        try {
            reflectionField = compClass.getDeclaredField("$GROUP_SETTERS");
            reflectionField.setAccessible(true);
            int groupCounter = 0;
            SetterStructure[] setterStructures = (SetterStructure[]) reflectionField.get(null);
            for (SetterStructure setterStructure : setterStructures) {
                if (field.getTag() == setterStructure.getGroupCounter()) {
                    groupCounter = setterStructure.getGroupCounter();
                    field = getNextField(tags, true);
                    if (field == null) {
                        return instance;
                    }
                }
                Class<? extends QFComponent> groupClass = setterStructure.getGroupClazz();
                QFComponent component = getInstanceOLD(tags, instance, groupClass);
                try {
                    instance = createInstance(instance, compClass);
                    Method groupSetterMethod = setterStructure.getGroupOwnerSetter();
                    groupSetterMethod.invoke(instance, component);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
//					tags.pop();
                return (QFComp) getInstanceOLD(tags, instance, instance.getClass());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }*/

    /*private static <QFComp extends QFComponent> QFComp createInstance(QFComp instance, Class<? extends QFComp> compClass) {
        if (instance == null) {
            try {
                Constructor constructor = compClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                instance = (QFComp) constructor.newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }*/

    private static <QFComp extends QFComponent> QFComp createInstance(QFComp instance, Class<? extends QFComponent> compClass) {
        if (instance == null) {
            try {
                Constructor constructor = compClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                instance = (QFComp) constructor.newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public abstract void toFIXString(StringBuilder sb);

    /*private static QFField getNextField(Stack<QFTag> tags, boolean doPop) {
        if (tags.isEmpty()) {
            return null;
        }
        QFTag kv = doPop ? tags.pop() : tags.peek();
        QFField field = QFFieldUtils.lookupField(kv);
        return field;
    }*/
}