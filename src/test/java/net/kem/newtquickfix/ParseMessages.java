package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFMessage;
import net.kem.newtquickfix.blocks.QFTag;
import net.kem.newtquickfix.blocks.QFUtils;
import net.kem.newtquickfix.builders.BuilderUtils;

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
        //final CharSequence src = "8=FIX50SP2\u00019=209\u000135=AT\u000152=20151220-16:47:08.011\u0001755=Alloc_Report_ID_1\u000170=1234000007\u000175=20151220\u000160=20151220-16:47:08.010\u000187=1\u000188=0\u000158=Failed to delete Block Giveup due to CCP status restriction\u000110002=549000071\u000110003=549000007\u000110=000\u0001";
        final CharSequence src = "8=FIX.5.0\u00019=0\u000135=J\u000152=20120325-07:45:05.364\u000170=1234\u000171=0\u000172=1234\u0001626=2\u0001857=1\u000154=2\u000155=\u000148=IBM.TH\u000122=5\u000153=1220\u00016=10.01\u000115=USD\u0001453=2\u0001448=default_test_client\u0001447=D\u0001452=3\u0001448=default_test_eb\u0001447=D\u0001452=1\u0001207=J_EXCHANGE\u000175=20120325\u000173=1\u000137=\u000163=4\u000164=20120325\u000178=2\u000179=default_test_giveup_account\u000180=1210\u0001467=5678\u000181=3\u0001539=1\u0001524=default_test_cb\u0001525=C\u0001538=4\u0001161=Electronic\u0001153=10.01\u0001155=1\u0001156=M\u0001120=USD\u000110251=10251.111111111\u000110252=10252.111111111\u000112=5\u000113=1\u0001479=USD\u000179=GIVEUP ACCOUNT_1\u000180=10\u0001467=1234\u000181=3\u0001539=1\u0001524=default_test_cb\u0001525=C\u0001538=4\u0001161=Electronic\u0001153=10.01\u0001155=1\u0001156=M\u0001120=USD\u000110251=10251.222222222\u000110252=10252.22222222\u000112=5\u000113=1\u0001479=USD\u000160=20120325-07:45:05.364\u000110351=10351.123456789\u000110352=10352.123456789\u000110=0\u0001";

        ParseMessages theRabbit = new ParseMessages();
        theRabbit.init();
        theRabbit.testParseMessages();
//        theRabbit.testParseMessage(src);
    }

    private void init() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, IOException, InvocationTargetException {
        GenerateFields.setFIXVersion("v50sp2");
        QFUtils.fillMaps();
    }

    public static QFMessage parseMessage(CharSequence src) {
        return parseMessage(src, QFComponentValidator.getDefaultComponentValidator());
    }

    public static QFMessage parseMessage(CharSequence src, QFComponentValidator componentValidator) {
        BuilderUtils.UNCLAIMED_TAGS.get().clear();
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
            return (QFMessage) getInstanceMethod.invoke(null, tags, componentValidator);
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
        StringBuilder sb = new StringBuilder();
        msg.toFIXString(sb);
        System.out.println(sb.toString());
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
