package net.kem.newtquickfix.blocks;

import net.kem.newtquickfix.LiteFixMessageParser;
import net.kem.newtquickfix.QFComponentValidator;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeny Kurtser
 * Date: 19-Oct-15
 * Time: 8:46 AM
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */ /*
<field number="1502" name="StreamAsgnRejReason" type="INT">
      <value enum="0" description="UNKNOWN_CLIENT"/>
      <value enum="1" description="EXCEEDS_MAXIMUM_SIZE"/>
      <value enum="2" description="UNKNOWN_OR_INVALID_CURRENCY_PAIR"/>
      <value enum="3" description="NO_AVAILABLE_STREAM"/>
      <value enum="99" description="OTHER"/>
</field>
 */
public abstract class QFField<V> implements Serializable {
	private transient int $hashCode;
	protected transient String $fixString;
	protected QFComponentValidator componentValidator;

	protected V value;

	public abstract int getTag();

	public String getName() {
		return getClass().getSimpleName();
	}

	public V getValue() {
		return value;
	}

	public boolean isKnown() {
		return true;
	}

	public QFComponentValidator getComponentValidator() {
		if(componentValidator == null) {
			setComponentValidator(LiteFixMessageParser.getComponentValidator());
		}
		return componentValidator;
	}
	public void setComponentValidator(QFComponentValidator newComponentValidator) {
		componentValidator = newComponentValidator;
	}

	public void toFIXString(StringBuilder sb) {
		if($fixString == null) {
			$fixString = getTag() + "=" + (value==null? "": value) + QFFieldUtils.FIELD_SEPARATOR;
		}
		sb.append($fixString);
	}

	public String toFixString() {
		if($fixString == null) {
			StringBuilder sb = new StringBuilder();
			toFIXString(sb);
		}
		return $fixString;
	}

	@Override
	public String toString() {
		String res = getClass().getSimpleName() + ": " + toFixString();
		return res.substring(0, res.length()-1);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		QFField<?> that = (QFField<?>) o;
		return getTag() == that.getTag() && value.equals(that.value);
	}

	@Override
	public int hashCode() {
		if($hashCode == 0) {
			$hashCode = value.hashCode();
			$hashCode = 31 * $hashCode + getTag();
		}
		return $hashCode;
	}
}