package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.ComponentMetadata;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFFieldUtils;
import net.kem.newtquickfix.blocks.QFGroupDef;
import net.kem.newtquickfix.blocks.QFMember;
import net.kem.newtquickfix.blocks.SetterStructure;
import net.kem.tquickfix.blocks.QFTag;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeny Kurtser
 * Date: 18-Oct-15
 * Time: 3:00 PM
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 * <p>
 */
@SuppressWarnings("unused")
public class ComponentMain extends QFComponent {

    public static void main(String[] args) throws NoSuchMethodException {
        QFFieldUtils.fillMap();

        Stack<QFTag> origTags = new Stack<>();
        // ComponentMain
        // GroupA
        origTags.add(new QFTag(FieldIntegerGroupCount.TAG, "2"));
        origTags.add(new QFTag(FieldStringGroupDelimiter.TAG, "GroupADelim1"));
        origTags.add(new QFTag(FieldStringA.TAG, "Main string value 1"));
        // GroupA.ComponentC
        origTags.add(new QFTag(FieldStringA.TAG, "CompC string value 1"));
        origTags.add(new QFTag(FieldIntegerA.TAG, "11"));
        // GroupA.ComponentC.ComponentB
        origTags.add(new QFTag(FieldStringB.TAG, "CompC.CompB string value 1"));

        origTags.add(new QFTag(FieldStringGroupDelimiter.TAG, "GroupADelim2"));
        origTags.add(new QFTag(FieldStringA.TAG, "Main string value 2"));
        origTags.add(new QFTag(FieldStringA.TAG, "REDUNDANT TAG"));
        // GroupA.ComponentC
        origTags.add(new QFTag(FieldStringA.TAG, "CompC string value 2"));
        origTags.add(new QFTag(FieldIntegerA.TAG, "12"));
        // GroupA.ComponentC.ComponentB
        origTags.add(new QFTag(FieldStringB.TAG, "CompC.CompB string value 2"));
        // GroupA.FieldIntegerA
        origTags.add(new QFTag(FieldIntegerA.TAG, "42"));

        //reverse
        Stack<QFField> tags = new Stack<>();
        while (!origTags.empty()) {
            QFTag kv = origTags.pop();
            QFField qfField = QFFieldUtils.lookupField(kv);
            tags.push(qfField);
        }
//		QFComponent theRabbit0 = ComponentC.getInstance(tags, null);
        ComponentMain theRabbit0 = ComponentMain.getInstance(tags, null);
        StringBuilder sb = new StringBuilder();
        theRabbit0.toFIXString(sb);
        System.out.printf(sb.toString());
    }

    @QFGroupDef(count = FieldIntegerGroupCount.TAG, delimiter = FieldStringGroupDelimiter.TAG)
    public static class GroupA extends QFComponent {
        private static final int $GROUP_COUNT;
        static {
            QFGroupDef groupAnnotation = GroupA.class.getAnnotation(QFGroupDef.class);
            $GROUP_COUNT = groupAnnotation.count();
        }
        @QFMember
        private FieldStringGroupDelimiter fieldStringGroupDelimiter;
        public FieldStringGroupDelimiter getFieldStringGroupDelimiter() {
            return fieldStringGroupDelimiter;
        }
        public void setFieldStringGroupDelimiter(FieldStringGroupDelimiter fieldStringGroupDelimiter) {
            this.fieldStringGroupDelimiter = fieldStringGroupDelimiter;
        }

        @QFMember(type = QFMember.Type.FIELD)
        private FieldStringA fieldStringA;
        public FieldStringA getFieldStringA() {
            return fieldStringA;
        }
        public void setFieldStringA(FieldStringA fieldStringA) {
            this.fieldStringA = fieldStringA;
        }

        @QFMember(type = QFMember.Type.COMPONENT)
        private ComponentC componentC;
        public ComponentC getComponentC() {
            return componentC;
        }
        public void setComponentC(ComponentC componentC) {
            this.componentC = componentC;
        }

        @QFMember(type = QFMember.Type.FIELD)
        private FieldIntegerA fieldIntegerA;
        public FieldIntegerA getFieldIntegerA() {
            return fieldIntegerA;
        }
        public void setFieldIntegerA(FieldIntegerA fieldIntegerA) {
            this.fieldIntegerA = fieldIntegerA;
        }

//		private ComponentD componentD;

        private GroupA() {
        }

        public static GroupA getInstance(Stack<QFField> tags, GroupA instance) {
            return getInstance(tags, instance, GroupA.class);
        }

        @Override
        public void toFIXString(StringBuilder sb) {
            if(fieldStringGroupDelimiter != null) {
                fieldStringGroupDelimiter.toFIXString(sb);
            }
            if(fieldStringA != null) {
                fieldStringA.toFIXString(sb);
            }
            if(componentC != null) {
                componentC.toFIXString(sb);
            }
            if(fieldIntegerA != null) {
                fieldIntegerA.toFIXString(sb);
            }
        }

        // Metadata section.
        private static final ComponentMetadata<GroupA> $METADATA = new ComponentMetadata<GroupA>() {
            private Method[] $FIELD_SETTERS;
            private Method[] $COMPONENT_SETTERS;
            private SetterStructure[] $GROUP_STRUCTUERS;

            {
                try {
                    $FIELD_SETTERS = new Method[3];
                    $FIELD_SETTERS[0] = GroupA.class.getDeclaredMethod("setFieldStringGroupDelimiter", FieldStringGroupDelimiter.class);
                    $FIELD_SETTERS[0].setAccessible(true);
                    $FIELD_SETTERS[1] = GroupA.class.getDeclaredMethod("setFieldStringA", FieldStringA.class);
                    $FIELD_SETTERS[1].setAccessible(true);
                    $FIELD_SETTERS[2] = GroupA.class.getDeclaredMethod("setFieldIntegerA", FieldIntegerA.class);
                    $FIELD_SETTERS[2].setAccessible(true);

                    $COMPONENT_SETTERS = new Method[1];
                    $COMPONENT_SETTERS[0] = GroupA.class.getDeclaredMethod("setComponentC", ComponentC.class);
                    $COMPONENT_SETTERS[0].setAccessible(true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Method[] getFieldSetters() {
                return $FIELD_SETTERS;
            }

            @Override
            public Method[] getComponentSetters() {
                return $COMPONENT_SETTERS;
            }

            @Override
            public SetterStructure[] getSetterStructure() {
                return $GROUP_STRUCTUERS;
            }
        };
    }

    @QFMember(type = QFMember.Type.GROUP, groupClass = ComponentMain.GroupA.class)
    private List<ComponentMain.GroupA> groupA;
    public List<ComponentMain.GroupA> getGroupA() {
        return groupA;
    }
    public void setGroupA(List<ComponentMain.GroupA> groupA) {
        this.groupA = groupA;
    }

    private ComponentMain() {
    }

    public static ComponentMain getInstance(Stack<QFField> tags, ComponentMain instance) {
        return getInstance(tags, instance, ComponentMain.class);
    }

    @Override
    public void toFIXString(StringBuilder sb) {
        if(groupA != null && !groupA.isEmpty()) {
            sb.append(GroupA.$GROUP_COUNT).append('=').append(groupA.size()).append(QFFieldUtils.FIELD_SEPARATOR);
            for (GroupA group : groupA) {
                group.toFIXString(sb);
            }
        }
    }

    // Metadata section.
    private static final ComponentMetadata<ComponentMain.GroupA> $METADATA = new ComponentMetadata<ComponentMain.GroupA>() {
        private static final int $GROUP_COUNTER = FieldIntegerGroupCount.TAG;
        private static final int $GROUP_DELIMITER = FieldStringGroupDelimiter.TAG;

        private Method[] $FIELD_SETTERS;
        private Method[] $COMPONENT_SETTERS;
        private SetterStructure[] $GROUP_STRUCTUERS;

        {
            $GROUP_STRUCTUERS = new SetterStructure[1];
            $GROUP_STRUCTUERS[0] = new SetterStructure(ComponentMain.class, GroupA.class, $GROUP_COUNTER, $GROUP_DELIMITER);
        }

        @Override
        public Method[] getFieldSetters() {
            return $FIELD_SETTERS;
        }

        @Override
        public Method[] getComponentSetters() {
            return $COMPONENT_SETTERS;
        }

        @Override
        public SetterStructure[] getSetterStructure() {
            return $GROUP_STRUCTUERS;
        }
    };
}