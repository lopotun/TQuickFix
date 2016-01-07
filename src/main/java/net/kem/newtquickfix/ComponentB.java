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
 */
public class ComponentB extends QFComponent {
	@QFMember(type = QFMember.Type.FIELD)
	private FieldStringB fieldStringB;
	@SuppressWarnings("unused")
	public FieldStringB getFieldStringB() {
		return fieldStringB;
	}
	@SuppressWarnings("unused")
	public void setFieldStringB(FieldStringB fieldStringB) {this.fieldStringB = fieldStringB;}

	private ComponentB() {}

	public static ComponentB getInstance(CharSequence fixVersion, Stack<QFField> tags, ComponentB instance, QFComponentValidator componentValidator) {
		return getInstance(fixVersion, tags, instance, ComponentB.class, componentValidator);
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
		if(fieldStringB != null) {
			fieldStringB.toFIXString(sb);
		}
	}

	// Metadata section.
	@SuppressWarnings("unused")
	private static final ComponentMetadata<ComponentB> $METADATA = new ComponentMetadata<ComponentB>() {
		private Method[] $FIELD_SETTERS;
		private Method[] $COMPONENT_SETTERS;
		private SetterStructure[] $GROUP_STRUCTUERS;

		{
			try {
				$FIELD_SETTERS = new Method[1];
				$FIELD_SETTERS[0] = ComponentB.class.getDeclaredMethod("setFieldStringB", FieldStringB.class);
				$FIELD_SETTERS[0].setAccessible(true);
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
