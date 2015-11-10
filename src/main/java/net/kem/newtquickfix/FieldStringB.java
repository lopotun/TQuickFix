package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.QFField;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeny Kurtser
 * Date: 19-Oct-15
 * Time: 8:53 AM
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */ /*
<field number="1497" name="StreamAsgnReqID" type="STRING"/>
 */
public class FieldStringB extends QFField<String> {

	public static final int TAG = 1098;

	private FieldStringB(String value) {
		this.value = value;
	}

	@Override
	public int getTag() {
		return TAG;
	}

	public static FieldStringB getInstance(String value) {
		return new FieldStringB(value);
	}
}