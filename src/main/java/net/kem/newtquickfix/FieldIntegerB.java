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
public class FieldIntegerB extends QFField<Integer> {
    public static final FieldIntegerB UNKNOWN_CLIENT = new FieldIntegerB(0);
    public static final FieldIntegerB EXCEEDS_MAXIMUM_SIZE = new FieldIntegerB(1);
    public static final FieldIntegerB UNKNOWN_OR_INVALID_CURRENCY_PAIR = new FieldIntegerB(2);
    public static final FieldIntegerB NO_AVAILABLE_STREAM = new FieldIntegerB(3);
    public static final FieldIntegerB OTHER = new FieldIntegerB(99);

    private static final Map<Integer, FieldIntegerB> STATIC_VALUES_MAPPING = new HashMap<>();

    static {
        STATIC_VALUES_MAPPING.put(UNKNOWN_CLIENT.getValue(), UNKNOWN_CLIENT);
        STATIC_VALUES_MAPPING.put(EXCEEDS_MAXIMUM_SIZE.getValue(), EXCEEDS_MAXIMUM_SIZE);
        STATIC_VALUES_MAPPING.put(UNKNOWN_OR_INVALID_CURRENCY_PAIR.getValue(), UNKNOWN_OR_INVALID_CURRENCY_PAIR);
        STATIC_VALUES_MAPPING.put(NO_AVAILABLE_STREAM.getValue(), NO_AVAILABLE_STREAM);
        STATIC_VALUES_MAPPING.put(OTHER.getValue(), OTHER);
    }

    public static final int TAG = 1497;

    private FieldIntegerB(Integer value) {
        this.value = value;
    }

    @Override
    public int getTag() {
        return TAG;
    }

    public static FieldIntegerB getInstance(String value) {
        return getInstance(Integer.parseInt(value));
    }

    public static FieldIntegerB getInstance(Integer value) {
        FieldIntegerB res = STATIC_VALUES_MAPPING.get(value);
        if (res == null) {
            res = new FieldIntegerB(value);
        }
        return res;
    }
}