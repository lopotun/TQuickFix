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
            if(!qfField.isKnown()) {
                return instance;
            }

            // Look for fields.
            QFUtils.ChildGetterSetter childGS = QFUtils.getFieldGetterSetter(qfField.getClass(), compClass);
            if(childGS != null) {
                // Create instance of this component/group if needed.
                if(instance == null) {
                    instance = createThisInstance(instance, compClass);
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
            List<QFUtils.ChildGetterSetter<? extends QFComponent>> childrenComponentGSs = QFUtils.getChildrenComponentClasses(compClass);
            if(childrenComponentGSs != null) {
                // Get child component's metadata.
                for (QFUtils.ChildGetterSetter<? extends QFComponent> childrenComponentGS : childrenComponentGSs) {
                    try {
                        // Create (recursively) child component's instance.
                        QFComp childrenComponentInstance = getInstance(tags, null, childrenComponentGS.getChildClass());
                        if (childrenComponentInstance != null) {
                            // Child component instance has been created (that means that the current filed belongs to this child component (or to one of its descenders)).
                            // Create instance of this component if needed.
                            instance = createThisInstance(instance, compClass);
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
                            // Proceed to next field. Note: do not pop stack since it has been already popped while child component creation.
                            continue NEXT_TAG;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            // The current field belong neither to this component nor to one of its child components. Check if the filed belongs to one of child groups (if any).
            List<QFUtils.ChildGetterSetterGroup<? extends QFComponent>> groupComponentGSs = QFUtils.getChildrenGroupClasses(compClass);
            if(groupComponentGSs != null) {
                // Get child group component metadata.
                for (QFUtils.ChildGetterSetterGroup<? extends QFComponent> groupComponentGS : groupComponentGSs) {
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
                                instance = createThisInstance(instance, compClass);
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

    private static <QFComp extends QFComponent> QFComp createThisInstance(QFComp instance, Class<? extends QFComponent> compClass) {
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
}