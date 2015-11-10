package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.ComponentMetadata;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFFieldUtils;
import net.kem.newtquickfix.blocks.QFGroupDef;
import net.kem.newtquickfix.blocks.QFMember;
import net.kem.newtquickfix.blocks.SetterStructure;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Stack;

/**
 * Created by Evgeny Kurtser on 10/29/2015 at 9:57 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */ /*
<component name="InstrmtLegGrp">
      <group name="NoLegs" required="N">
        <component name="InstrumentLeg" required="N"/>
      </group>
</component>
 */
@SuppressWarnings("unused")
public class ComponentD extends QFComponent {
    @QFGroupDef(count = FieldIntegerGroupCount.TAG, delimiter = FieldStringGroupDelimiter.TAG)
    public static class GroupA extends QFComponent {
        private static final int $GROUP_COUNT;
        static {
            QFGroupDef groupAnnotation = GroupA.class.getAnnotation(QFGroupDef.class);
            $GROUP_COUNT = groupAnnotation.count();
        }
        @QFMember(type = QFMember.Type.FIELD)
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
        }

        // Metadata section.
        private static final ComponentMetadata<GroupA> $METADATA = new ComponentMetadata<GroupA>() {
            private static final int $GROUP_COUNTER = FieldIntegerGroupCount.TAG;
            private static final int $GROUP_DELIMITER = FieldStringGroupDelimiter.TAG;

            private Method[] $FIELD_SETTERS;
            private Method[] $COMPONENT_SETTERS;
            private SetterStructure[] $GROUP_STRUCTUERS;

            {
                try {
                    $FIELD_SETTERS = new Method[2];
                    $FIELD_SETTERS[1] = FieldStringGroupDelimiter.class.getDeclaredMethod("setFieldStringGroupDelimiter", FieldStringGroupDelimiter.class);
                    $FIELD_SETTERS[1].setAccessible(true);
                    $FIELD_SETTERS[1] = FieldIntegerA.class.getDeclaredMethod("setFieldIntegerA", FieldIntegerA.class);
                    $FIELD_SETTERS[1].setAccessible(true);

                    $GROUP_STRUCTUERS = new SetterStructure[1];
                    $GROUP_STRUCTUERS[0] = new SetterStructure(ComponentD.class, GroupA.class, $GROUP_COUNTER, $GROUP_DELIMITER);
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

    @QFMember(type = QFMember.Type.GROUP, groupClass = ComponentD.GroupA.class)
    private List<ComponentD.GroupA> groupA;
    public List<ComponentD.GroupA> getGroupA() {
        return groupA;
    }
    public void setGroupA(List<ComponentD.GroupA> groupA) {
        this.groupA = groupA;
    }

    private ComponentD() {
    }

    public static ComponentD getInstance(Stack<QFField> tags, ComponentD instance) {
        return getInstance(tags, instance, ComponentD.class);
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
    private static final ComponentMetadata<ComponentD> $METADATA = new ComponentMetadata<ComponentD>() {
        private Method[] $FIELD_SETTERS;
        private Method[] $COMPONENT_SETTERS;
        private SetterStructure[] $GROUP_STRUCTUERS;

        {

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