package net.kem.newtquickfix.blocks;

import net.kem.newtquickfix.QFComponentValidator;

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

    protected QFComponent parent;

    public QFComponent getParent() {
        return parent;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    protected static <QFComp extends QFComponent> QFComp getInstance(Stack<QFField> tags, QFComp thisInstance, Class<? extends QFComponent> compClass, QFComponentValidator componentValidator) {
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
                return thisInstance;
            }
            QFField qfField = tags.peek();
            if(!qfField.isKnown()) {
                return thisInstance;
            }

            // Look for fields.
            QFUtils.ChildGetterSetter childGS = QFUtils.getFieldGetterSetter(qfField.getClass(), compClass);
            if(childGS != null) {
                // Create instance of this component/group if needed.
                if(thisInstance == null) {
                    thisInstance = createThisInstance(thisInstance, compClass, componentValidator);
                } else {
                    // This instance is a Group and this field is a group element delimiter ->
                    // 1. We're at beginning of next group element.
                    // 2. Stop precessing this group element (and then go to next one).
                    if(groupDelimiterTag != 0 && qfField.getTag() == groupDelimiterTag) {
                        return thisInstance;
                    }
                }
                // Assign field value to the component member.
                try {
                    // Check if the value was not assigned previously.
                    Method fieldGetter = childGS.getGetter();
                    QFField currentValue = (QFField) fieldGetter.invoke(thisInstance);
                    if(currentValue == null) {
                        // Assign field value to the component member by calling a field setter method.
                        Method fieldSetter = childGS.getSetter();
                        fieldSetter.invoke(thisInstance, qfField);
                        // Proceed to next field.
                        tags.pop();
                        continue;
                    } else {
                        // The value has been already assigned to this member.
                        componentValidator.duplicatedTag(qfField, currentValue, thisInstance);
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
                        QFComp childrenComponentInstance = getInstance(tags, null, childrenComponentGS.getChildClass(), componentValidator);
                        if (childrenComponentInstance != null) {
                            // Child component instance has been created (that means that the current filed belongs to this child component (or to one of its descenders)).
                            // Create instance of this component if needed.
                            thisInstance = createThisInstance(thisInstance, compClass, componentValidator);
                            // Check if the value was not assigned previously.
                            Method fieldGetter = childrenComponentGS.getGetter();
                            QFComponent currentValue = (QFComponent) fieldGetter.invoke(thisInstance);
                            if(currentValue == null) {
                                // Assign the value of the newly created child component.
                                childrenComponentGS.getSetter().invoke(thisInstance, childrenComponentInstance);
                                // Set parent reference.
                                childrenComponentInstance.parent = thisInstance;
                            } else {
                                // The value has been already assigned to this member.
                                componentValidator.duplicatedComponent(childrenComponentInstance, thisInstance);
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
                                QFComp childrenGroupInstance = getInstance(tags, null, groupComponentGS.getChildClass(), componentValidator);
                                if (childrenGroupInstance != null) {
                                    groupInstances.add(childrenGroupInstance);
                                }
                            }
                            if(numberOfGroupMembers != groupInstances.size()) {
                                componentValidator.invalidGroupCount(qfField, groupInstances, thisInstance);
                            }
                            // Assign the value of the newly created child group.
                            if (!groupInstances.isEmpty()) {
                                thisInstance = createThisInstance(thisInstance, compClass, componentValidator);
                                groupComponentGS.getSetter().invoke(thisInstance, groupInstances);
                                // Set parent reference.
                                for (QFComp groupInstance : groupInstances) {
                                    groupInstance.parent = thisInstance;
                                }
                            }
                            // Proceed to next field. Note: do not pop stack since it has been already popped while child group creation.
                            continue NEXT_TAG;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            return thisInstance;
        }
    }

    protected static <QFComp extends QFComponent> QFComp createThisInstance(QFComp instance, Class<? extends QFComponent> compClass, QFComponentValidator componentValidator) {
        if (instance == null) {
            try {
                if(QFMessage.class.isAssignableFrom(compClass)) {
                    Method getInstance = compClass.getDeclaredMethod("getInstance", QFComponentValidator.class);
                    instance = (QFComp) getInstance.invoke(null, componentValidator);
                } else {
                    Constructor constructor = compClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                instance = (QFComp) constructor.newInstance();
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public abstract boolean validate();
    public abstract boolean validate(QFComponentValidator componentValidator);
    public abstract void toFIXString(StringBuilder sb);
}