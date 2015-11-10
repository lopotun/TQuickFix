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
public class FieldIntegerA extends QFField<Integer> {
	public static final FieldIntegerA UNKNOWN_CLIENT = new FieldIntegerA(0);
	public static final FieldIntegerA EXCEEDS_MAXIMUM_SIZE = new FieldIntegerA(1);
	public static final FieldIntegerA UNKNOWN_OR_INVALID_CURRENCY_PAIR = new FieldIntegerA(2);
	public static final FieldIntegerA NO_AVAILABLE_STREAM = new FieldIntegerA(3);
	public static final FieldIntegerA OTHER = new FieldIntegerA(99);
	private static final Map<Integer, FieldIntegerA> STATIC_VALUES_MAPPING = new HashMap<>();
	static {
		STATIC_VALUES_MAPPING.put(UNKNOWN_CLIENT.getValue(), UNKNOWN_CLIENT);
		STATIC_VALUES_MAPPING.put(EXCEEDS_MAXIMUM_SIZE.getValue(), EXCEEDS_MAXIMUM_SIZE);
		STATIC_VALUES_MAPPING.put(UNKNOWN_OR_INVALID_CURRENCY_PAIR.getValue(), UNKNOWN_OR_INVALID_CURRENCY_PAIR);
		STATIC_VALUES_MAPPING.put(NO_AVAILABLE_STREAM.getValue(), NO_AVAILABLE_STREAM);
		STATIC_VALUES_MAPPING.put(OTHER.getValue(), OTHER);
	}

	public static final int TAG = 1502;

	private FieldIntegerA(Integer value) {
		this.value = value;
	}

	@Override
	public int getTag() {
		return TAG;
	}

	public static FieldIntegerA getInstance(String value) {
		return getInstance(Integer.parseInt(value));
	}

	public static FieldIntegerA getInstance(Integer value) {
		FieldIntegerA res = STATIC_VALUES_MAPPING.get(value);
		if(res == null) {
			res = new FieldIntegerA(value);
		}
		return res;
	}
}