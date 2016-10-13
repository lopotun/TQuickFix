package net.kem.newtquickfix.v44.messages;

import net.kem.newtquickfix.QFComponentValidator;
import net.kem.newtquickfix.blocks.QFMember;
import net.kem.newtquickfix.blocks.QFMessage;
import net.kem.newtquickfix.blocks.QFUtils;
import net.kem.newtquickfix.v44.components.StandardHeader;
import net.kem.newtquickfix.v44.components.StandardTrailer;
import net.kem.newtquickfix.v44.fields.BeginString;
import net.kem.newtquickfix.v44.fields.BodyLength;
import net.kem.newtquickfix.v44.fields.CheckSum;
import net.kem.newtquickfix.v44.fields.MsgSeqNum;
import net.kem.newtquickfix.v44.fields.MsgType;
import net.kem.newtquickfix.v44.fields.SenderCompID;
import net.kem.newtquickfix.v44.fields.SendingTime;
import net.kem.newtquickfix.v44.fields.TargetCompID;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Eugene Kurtzer
 * Date: 2016-07-04
 * Time: 14:59:44.965
 * <a href="mailto:Lopotun@gmail.com?subject=Regarding%20LiteFix">Eugene Kurtzer</a>
 */
@SuppressWarnings("unused")
public abstract class AMessage extends QFMessage {
	private static final AtomicInteger MESSAGE_COUNTER = new AtomicInteger(0);

	@QFMember(type = QFMember.Type.COMPONENT)
	protected StandardHeader standardHeader;

	public StandardHeader getStandardHeader() {
		return standardHeader;
	}

	public void setStandardHeader(StandardHeader standardHeader) {
		this.standardHeader = standardHeader;
	}

	@QFMember(type = QFMember.Type.COMPONENT)
	protected StandardTrailer standardTrailer;

	public StandardTrailer getStandardTrailer() {
		return standardTrailer;
	}

	public void setStandardTrailer(StandardTrailer standardTrailer) {
		this.standardTrailer = standardTrailer;
	}

	@Override
	public boolean validate() {
		return validate(getComponentValidator());
	}

	@Override
    	public boolean validate(QFComponentValidator componentValidator) {
    		Boolean valid = componentValidator.validateComponent(this);
    		if(valid != null) {
    			return valid;
    		}
    		if(standardHeader == null) {
    			valid = componentValidator.mandatoryElementMissing(this, StandardHeader.class);
    		} else {
    			valid = standardHeader.validate(componentValidator);
    		}
    		if(standardTrailer == null) {
    			valid = componentValidator.mandatoryElementMissing(this, StandardTrailer.class);
    		} else {
    			valid &= standardTrailer.validate(componentValidator);
    		}
    		return valid;
    	}

    	@Override
    	public abstract MsgType getMessageType();

    	public static BeginString getVersion() {
    		return BeginString.of("FIX50SP2");
    	}

    	public String getMessageCategory() {
    		return "app";
    	}


    	private void addHeaderTrailer() {
    		if(standardHeader == null) {
    			standardHeader = StandardHeader.of();
    			standardHeader.setBeginString(getVersion());
    			standardHeader.setMsgType(getMessageType());
    		}
    		if(standardTrailer == null) {
    			standardTrailer = StandardTrailer.of();
    		}
    	}

    	public StringBuilder seal() {
    		return seal(null, null);
    	}

    	public StringBuilder seal(SenderCompID sender, TargetCompID target) {
    		addHeaderTrailer();

    		if(sender != null) {
    			standardHeader.setSenderCompID(sender);
    		}
    		if(target != null) {
    			standardHeader.setTargetCompID(target);
    		}
    		standardHeader.setMsgSeqNum(MsgSeqNum.of(MESSAGE_COUNTER.getAndIncrement() % Integer.MAX_VALUE));
    		standardHeader.setSendingTime(SendingTime.of());

    		StringBuilder sb = new StringBuilder(1024);
    		toFIXString(sb);
    		standardHeader.setBodyLength(BodyLength.of(sb.length()));

    		int checksum = QFUtils.calculateCheckSum(sb);
    		standardTrailer.setCheckSum(CheckSum.of(String.valueOf(checksum)));
    		standardTrailer.toFIXString(sb);

    		return sb;
    	}
    }