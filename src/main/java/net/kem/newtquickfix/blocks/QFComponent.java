package net.kem.newtquickfix.blocks;

import com.sun.istack.internal.Nullable;
import net.kem.newtquickfix.LiteFixMessageParser;
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
    protected QFComponentValidator componentValidator;

    public String getName() {
        return getClass().getSimpleName();
    }

    protected static <QFComp extends QFComponent> QFComp getInstance(CharSequence fixVersion, Stack<QFField> tags, QFComp thisInstance, Class<? extends QFComponent> compClass, QFComponentValidator componentValidator) {
        return getInstance(fixVersion, tags, thisInstance, compClass, 0, componentValidator);
    }
    protected static <QFComp extends QFComponent> QFComp getInstance(CharSequence fixVersion, Stack<QFField> tags, QFComp thisInstance, Class<? extends QFComponent> compClass, int groupDelimiterTag, QFComponentValidator componentValidator) {
        NEXT_TAG:
        while (true) {
            if (tags.isEmpty()) {
                return thisInstance;
            }
            QFField qfField = tags.peek();
            if(!qfField.isKnown()) {
                // Keep this unclaimed tag for the future reference.
                componentValidator.unprocessedTag(tags.pop(), compClass);
                continue;
            }
            int stackSize = tags.size();

            // Look for fields.
            QFUtils.ChildGetterSetter childGS = QFUtils.getFieldGetterSetter(fixVersion, qfField.getClass(), compClass);
            if(childGS != null) { // i.e. this component can contain this field.
                // Create instance of this component/group if needed.
                if(thisInstance == null) {
                    thisInstance = createThisInstance(thisInstance, compClass);
                } else {
                    // This instance is a Group and this field is a group element delimiter ->
                    // 1. We're at beginning of next group element.
                    // 2. Stop processing this group element (and then go to next one).
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
                        // The value has been already assigned to this component member.
                        componentValidator.duplicatedTag(qfField, currentValue, thisInstance);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            // The current field does not belong to this component. Check if the filed belongs to one of its child components (if any).
            List<QFUtils.ChildGetterSetter<? extends QFComponent>> componentChildrenGSs = QFUtils.getChildrenComponentClasses(fixVersion, compClass);
            if(componentChildrenGSs != null) {
                // Get child component's metadata.
                for (QFUtils.ChildGetterSetter<? extends QFComponent> componentChildrenGS : componentChildrenGSs) {
                    try {
                        // Try to create (recursively) child component's instance.
                        QFComp componentChildInstance = getInstance(fixVersion, tags, null, componentChildrenGS.getChildClass(), groupDelimiterTag, componentValidator);
                        if (componentChildInstance != null) {
                            // Child component instance has been created (that means that the current filed belongs to this child component (or to one of its descenders)).
                            // Create instance of this component if needed.
                            thisInstance = createThisInstance(thisInstance, compClass);
                            // Check if the value was not assigned previously.
                            Method fieldGetter = componentChildrenGS.getGetter();
                            QFComponent currentValue = (QFComponent) fieldGetter.invoke(thisInstance);
                            if(currentValue == null) {
                                // Assign the value of the newly created child component.
                                componentChildrenGS.getSetter().invoke(thisInstance, componentChildInstance);
                                // Set parent reference.
                                componentChildInstance.parent = thisInstance;
                            } else {
                                // The value has been already assigned to this member.
                                //componentValidator.duplicatedComponent(componentChildInstance, thisInstance); //TODO warn about wrong placement of the current field.
                                QFUtils.merge(componentChildInstance, currentValue);
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
            List<QFUtils.ChildGetterSetterGroup<? extends QFComponent>> componentGroupsGSs = QFUtils.getChildrenGroupClasses(fixVersion, compClass);
            if(componentGroupsGSs != null) {
//                // I'll need to know whether the current component is an ordinal component or a group.
//                // I rely on the fact that every group has QFGroupDef annotation.
//                QFGroupDef groupAnnotation = compClass.getAnnotation(QFGroupDef.class);
//                if(groupAnnotation != null) {
//                    groupDelimiterTag = groupAnnotation.delimiter();
//                }

                // Get child group component metadata.
                for (QFUtils.ChildGetterSetterGroup<? extends QFComponent> componentGroupGS : componentGroupsGSs) {
                    try {
                        // Check if this field is a group count.
                        if(qfField.getTag() == componentGroupGS.getGroupCount()) {
                            groupDelimiterTag = componentGroupGS.getGroupDelimiter();
                            // This field is is a group count. Pop the stack and create list of group members.
                            tags.pop();
                            int numberOfGroupMembers = Integer.parseInt(qfField.getValue().toString());
                            List<QFComp> groupInstances = new ArrayList<>(numberOfGroupMembers);
                            for (int i=0; i<numberOfGroupMembers; i++) {
                                // Try to create group instance.
                                QFComp groupChildInstance = getInstance(fixVersion, tags, null, componentGroupGS.getChildClass(), groupDelimiterTag, componentValidator);
                                if (groupChildInstance != null) {
                                    groupInstances.add(groupChildInstance);
                                }
                            }
                            // Assign the value of the newly created child group.
                            if (!groupInstances.isEmpty()) {
                                thisInstance = createThisInstance(thisInstance, compClass);
                                componentGroupGS.getSetter().invoke(thisInstance, groupInstances);
                                // Set parent reference.
                                for (QFComp groupInstance : groupInstances) {
                                    groupInstance.parent = thisInstance;
                                }

                                // I'll need to know whether the current component is an ordinal component or a group.
                                // I rely on the fact that every group has QFGroupDef annotation.
                                int groupDlm = 0;
                                QFGroupDef grpAnnotation = groupInstances.get(0).getClass().getAnnotation(QFGroupDef.class);
                                if(grpAnnotation != null) {
                                    groupDlm = grpAnnotation.delimiter();
                                    final QFField nextVal = tags.peek();
                                    if(nextVal.getTag() == groupDlm) {
                                        componentValidator.invalidGroupCount(qfField, groupInstances, compClass);
                                    }
                                }
                            }
                            // Warn about possible group size inconsistency.
                            if(numberOfGroupMembers != groupInstances.size()) {
                                componentValidator.invalidGroupCount(qfField, groupInstances, compClass);
                            }
                            // Proceed to next field. Note: do not pop stack since it has been already popped while child group creation.
                            continue NEXT_TAG;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
//            // This component is Group and the current tag hasn't been processed by this group (and all its children components).
//            if(groupDelimiterTag > 0 && stackSize == tags.size()) {
//                // Keep this unclaimed tag for the future reference.
//                BuilderUtils.UNCLAIMED_TAGS.get().add(new ImmutablePair<>(tags.peek(), compClass));
//                // Check whether this unclaimed tag was meant to belong to this group.
//                // To do so, I check if one of the next several (for example, up to 5) tags belongs to this group.
//                // If it is then, most probably, this unclaimed tag belongs to this group and, therefore, just remove it from stack and proceed to next tag within this group.
//                // Pop the stack and create list of group members.
//                final List<QFField> qfFields = tags.subList(Math.max(0, tags.size() - 5), tags.size());
//                for (QFField field : qfFields) {
//                    QFUtils.ChildGetterSetter groupChildGS = QFUtils.getFieldGetterSetter(field.getClass(), compClass);
//                    if(groupChildGS != null) {
//                        tags.pop();
//                        continue NEXT_TAG;
//                    }
//                }
//            }
            return thisInstance;
        }
    }

    private static <QFComp extends QFComponent> QFComp createThisInstance(@Nullable QFComp instance, Class<? extends QFComponent> compClass) {
        if (instance == null) {
            try {
                if(QFMessage.class.isAssignableFrom(compClass)) {
                    Method getInstance = compClass.getDeclaredMethod("getInstance");//TODO call getInstance that won't initialize header and trailer.
                    instance = (QFComp) getInstance.invoke(null);
                } else {
                    final Constructor<? extends QFComponent> constructor = compClass.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    instance = (QFComp) constructor.newInstance();
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public QFComponentValidator getComponentValidator() {
	    if(componentValidator == null) {
		    setComponentValidator(null);
	    }
        return componentValidator;
    }
    public void setComponentValidator(QFComponentValidator newComponentValidator) {
	    if(newComponentValidator == null) {
		    newComponentValidator = LiteFixMessageParser.getInstance().getComponentValidator();
	    }
        componentValidator = newComponentValidator;
    }

    public abstract boolean validate();
    public abstract boolean validate(QFComponentValidator componentValidator);
    public abstract void toFIXString(StringBuilder sb);
}