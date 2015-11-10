package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.QFField;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeny Kurtser
 * Date: 19-Oct-15
 * Time: 8:48 AM
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class FieldIntegerGroupCount extends QFField<Integer> {
	private static final Map<Integer, FieldIntegerGroupCount> STATIC_VALUES_MAPPING = new HashMap<>();
	static {

	}

	public static final int TAG = 1234;

	private FieldIntegerGroupCount(Integer value) {
		this.value = value;
	}

	@Override
	public int getTag() {
		return TAG;
	}

	public static FieldIntegerGroupCount getInstance(String value) {
		return getInstance(Integer.parseInt(value));
	}

	public static FieldIntegerGroupCount getInstance(Integer value) {
		FieldIntegerGroupCount res = STATIC_VALUES_MAPPING.get(value);
		if(res == null) {
			res = new FieldIntegerGroupCount(value);
		}
		return res;
	}
}