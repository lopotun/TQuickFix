package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFMessage;
import net.kem.newtquickfix.blocks.QFTag;
import net.kem.newtquickfix.blocks.QFUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import net.kem.newtquickfix.v50sp2.components.StandardHeader;
//import net.kem.newtquickfix.v50sp2.components.StandardTrailer;
//import net.kem.newtquickfix.v50sp2.fields.AllocStatus;
//import net.kem.newtquickfix.v50sp2.fields.BeginString;
//import net.kem.newtquickfix.v50sp2.fields.MsgType;
//import net.kem.newtquickfix.v50sp2.fields.SendingTime;
//import net.kem.newtquickfix.v50sp2.fields.TradeDate;
//import net.kem.newtquickfix.v50sp2.messages.AllocationReportAck;

/**
 * Created by Evgeny Kurtser on 12/22/2015 at 12:46 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class ParseMessages {
    private static final Pattern PATTERN = Pattern.compile("(\\d+)=(.*)");

    public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, IOException, InvocationTargetException {
        final CharSequence src = "8=FIX50SP2\u00019=209\u000135=AT\u000152=20151220-16:47:08.011\u0001755=Alloc_Report_ID_1\u000170=1234000007\u000175=20151220\u000160=20151220-16:47:08.010\u000187=1\u000188=0\u000158=Failed to delete Block Giveup due to CCP status restriction\u000110002=549000071\u000110003=549000007\u000110=000\u0001";

        ParseMessages theRabbit = new ParseMessages();
        theRabbit.init();
        theRabbit.testParseMessages();
        theRabbit.testParseMessage(src);
    }

    private void init() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, IOException, InvocationTargetException {
        GenerateFields.setFIXVersion("v50sp2");
        QFUtils.fillMaps();
    }

    public static QFMessage parseMessage(CharSequence src) {
        Stack<QFField> tags = toFieldsStack(src);
        if(tags.size() < 4) {
            throw new UnsupportedOperationException("Too few recognized tags in message " + src);
        }
        final Class<? extends QFMessage> messageClass = QFUtils.getMessageClass(tags);
        if(messageClass == null) {
            throw new UnsupportedOperationException("Unrecognized message");
        }
        try {
            final Method getInstanceMethod = messageClass.getMethod("getInstance", Stack.class, QFComponentValidator.class);
            return (QFMessage) getInstanceMethod.invoke(null, tags, QFComponentValidator.getDefaultComponentValidator());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new UnsupportedOperationException("Internal error", e);
        }
    }

    private void testParseMessages() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("FIXMessages.txt"));//export.txt
        StringBuilder sb = new StringBuilder();
        int count = 0;
        String src;
        while ((src = br.readLine()) != null) {
            try {
                QFMessage msg = parseMessage(src);
                msg.toFIXString(sb);
                System.out.println(String.valueOf(count++) + '\t' + sb.toString());
                sb.setLength(0);
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("ERROR IN LINE " + count);
                e.printStackTrace();
                throw e;
            }
        }
        br.close();
    }

    private void testParseMessage(CharSequence src) {
        QFMessage msg = parseMessage(src);
//        final MsgType msgType = (MsgType) msg.getMessageType();
//        if (msgType == MsgType.ALLOCATIONREPORTACK) {
//            System.out.println("Good!!");
//        }
//
//
//        StringBuilder sb = new StringBuilder();
//        msg.toFIXString(sb);
//        System.out.println(sb.toString());
//
//        AllocationReportAck ara = AllocationReportAck.getInstance();
////        setDefaultMessageHeaderTariler(ara, AllocationReportAck.getComponentValidator());
//        TradeDate tradeDate = TradeDate.getInstance("20151221");
//        ara.setTradeDate(tradeDate);
//        ara.setAllocStatus(AllocStatus.getInstance(120));
//        ara.setAllocStatus(AllocStatus.getInstance("abcd"));
//        ara.getStandardHeader().setSendingTime(SendingTime.getInstance("wrong one"));
//        ara.validate();
////        AllocReportID.setValidationHandler((cls, problematicValue, t, errorType) -> {
////            System.err.println("My custom error handler.");
////            return "my value";
////        });
//        AllocationReportAck.setComponentValidator(new QFComponentValidator() {
//            @Override
//            public void mandatoryElementMissing(@SuppressWarnings("unused") QFComponent thisComponent, @SuppressWarnings("unused") Class<?> missingElement) {
//                System.err.println("My custom component validator.");
//            }
//        });
//        ara.validate();
//
//        sb.setLength(0);
//        ara.toFIXString(sb);
//        System.out.println(sb.toString());
    }

//    public static void setDefaultMessageHeaderTariler(QFMessage message, QFComponentValidator componentValidator) {
//        StandardHeader standardHeader = StandardHeader.getInstance();
//        standardHeader.setBeginString(BeginString.getInstance("", componentValidator));
//        standardHeader.setMsgType(MsgType.getInstance("BL", componentValidator));
//        message.setStandardHeader(standardHeader);
//
//        StandardTrailer standardTrailer = StandardTrailer.getInstance();
//        message.setStandardTrailer(standardTrailer);
//    }

    private Stack<QFTag> toStack(CharSequence src) {
        Stack<QFTag> origTags = new Stack<>();
        final String[] rawTags = src.toString().split("\u0001|\n");
        for (String rawTag : rawTags) {
            final Matcher matcher = PATTERN.matcher(rawTag);
            if (matcher.matches()) {
                origTags.add(new QFTag(Integer.parseInt(matcher.group(1)), matcher.group(2)));
            }
        }
//        for(int i=rawTags.length-1; i>=0; i--) {
//            final Matcher matcher = PATTERN.matcher(rawTags[i]);
//            if(matcher.matches()) {
//                origTags.add(new QFTag(Integer.parseInt(matcher.group(1)), matcher.group(2)));
//            }
//        }
        return origTags;
    }

    private static Stack<QFField> toFieldsStack(CharSequence src) {
        Stack<QFTag> origTags = new Stack<>();
        final String[] rawTags = src.toString().split("\u0001");// \u0001|\n
        for (String rawTag : rawTags) {
            final Matcher matcher = PATTERN.matcher(rawTag);
            if (matcher.matches()) {
                origTags.add(new QFTag(Integer.parseInt(matcher.group(1)), matcher.group(2)));
            }
        }// 0 = {8=FIX.4.4}, 2 = {35=8}
        CharSequence fixVersion = origTags.get(0).getTagValue();
        //reverse
        Stack<QFField> tags = new Stack<>();
        while (!origTags.empty()) {
            QFTag kv = origTags.pop();
            QFField qfField = QFUtils.lookupField(fixVersion, kv, QFComponentValidator.getDefaultComponentValidator());
            tags.push(qfField);
        }
        return tags;
    }
}
