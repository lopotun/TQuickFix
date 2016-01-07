package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.ComponentMetadata;
import net.kem.newtquickfix.blocks.QFComponent;
import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFMember;
import net.kem.newtquickfix.blocks.SetterStructure;

import java.lang.reflect.Method;
import java.util.Stack;

/**
 * Created by Evgeny Kurtser on 10/29/2015 at 9:57 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */ /*
<component name="Instrument">
      <field name="Symbol" required="N"/>
      <field name="SymbolSfx" required="N"/>
      <field name="SecurityID" required="N"/>
      <field name="SecurityIDSource" required="N"/>
      <component name="SecAltIDGrp" required="N"/>
      <field name="Product" required="N"/>
</component>
 */
public class ComponentC extends QFComponent {
    @QFMember(type = QFMember.Type.FIELD)
    private FieldStringA fieldStringA;
    @SuppressWarnings("unused")
    public FieldStringA getFieldStringA() {
        return fieldStringA;
    }
    @SuppressWarnings("unused")
    public void setFieldStringA(FieldStringA fieldStringA) {
        this.fieldStringA = fieldStringA;
    }

    @QFMember(type = QFMember.Type.FIELD)
    private FieldIntegerA fieldIntegerA;
    @SuppressWarnings("unused")
    public FieldIntegerA getFieldIntegerA() {
        return fieldIntegerA;
    }
    @SuppressWarnings("unused")
    public void setFieldIntegerA(FieldIntegerA fieldIntegerA) {
        this.fieldIntegerA = fieldIntegerA;
    }

    @QFMember(type = QFMember.Type.COMPONENT)
    private ComponentB componentB;
    @SuppressWarnings("unused")
    public ComponentB getComponentB() {
        return componentB;
    }
    @SuppressWarnings("unused")
    public void setComponentB(ComponentB componentB) {
        this.componentB = componentB;
    }

    private ComponentC() {
    }

    public static ComponentC getInstance(CharSequence fixVersion, Stack<QFField> tags, ComponentC instance, QFComponentValidator componentValidator) {
        return getInstance(fixVersion, tags, instance, ComponentC.class, componentValidator);
    }

    @Override
    public boolean validate() {
        return true;
    }
    public boolean validate(QFComponentValidator componentValidator) {
        return true;
    }

    @Override
    public void toFIXString(StringBuilder sb) {
        if(fieldStringA != null) {
            fieldStringA.toFIXString(sb);
        }
        if(fieldIntegerA != null) {
            fieldIntegerA.toFIXString(sb);
        }
        if(componentB != null) {
            componentB.toFIXString(sb);
        }
    }


    // Metadata section.
    @SuppressWarnings("unused")
    private static final ComponentMetadata<ComponentC> $METADATA = new ComponentMetadata<ComponentC>() {
        private Method[] $FIELD_SETTERS;
        private Method[] $COMPONENT_SETTERS;
        private SetterStructure[] $GROUP_STRUCTUERS;

        {
            try {
                $FIELD_SETTERS = new Method[2];
                $FIELD_SETTERS[0] = ComponentC.class.getDeclaredMethod("setFieldStringA", FieldStringA.class);
                $FIELD_SETTERS[0].setAccessible(true);
                $FIELD_SETTERS[1] = ComponentC.class.getDeclaredMethod("setFieldIntegerA", FieldIntegerA.class);
                $FIELD_SETTERS[1].setAccessible(true);

                $COMPONENT_SETTERS = new Method[1];
                $COMPONENT_SETTERS[0] = ComponentC.class.getDeclaredMethod("setComponentB", ComponentB.class);
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
