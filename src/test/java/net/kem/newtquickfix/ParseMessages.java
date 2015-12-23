package net.kem.newtquickfix;

import net.kem.newtquickfix.blocks.QFField;
import net.kem.newtquickfix.blocks.QFFieldUtils;
import net.kem.newtquickfix.blocks.QFMessage;
import net.kem.newtquickfix.messages.AllocationReportAck;
import net.kem.tquickfix.blocks.QFTag;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Evgeny Kurtser on 12/22/2015 at 12:46 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class ParseMessages {
    private static final Pattern PATTERN = Pattern.compile("(\\d+)=(.*)");
    public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        final CharSequence src = "8=FIX50SP2\u00019=209\u000135=AT\u000152=20151220-16:47:08.011\u0001755=Alloc_Report_ID_1\u000170=1234000007\u000175=20151220\u000160=20151220-16:47:08.010\u000187=1\u000188=0\u000158=Failed to delete Block Giveup due to CCP status restriction\u000110002=549000071\u000110003=549000007\u000110=000\u0001";

        ParseMessages theRabbit = new ParseMessages();
        theRabbit.init();
        theRabbit.parseMessage(src);
    }

    private void init() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        QFFieldUtils.fillMap();
    }

    private void parseMessage(CharSequence src) {

        Stack<QFTag> origTags = toStack(src);

        //reverse
        Stack<QFField> tags = new Stack<>();
        while (!origTags.empty()) {
            QFTag kv = origTags.pop();
            QFField qfField = QFFieldUtils.lookupField(kv);
            tags.push(qfField);
        }

        QFMessage msg = AllocationReportAck.getInstance(tags, null);

        StringBuilder sb = new StringBuilder();
        msg.toFIXString(sb);
        System.out.printf(sb.toString());
    }

    private Stack<QFTag> toStack(CharSequence src) {
        Stack<QFTag> origTags = new Stack<>();
        final String[] rawTags = src.toString().split("\u0001");
        for (String rawTag : rawTags) {
            final Matcher matcher = PATTERN.matcher(rawTag);
            if(matcher.matches()) {
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
}
