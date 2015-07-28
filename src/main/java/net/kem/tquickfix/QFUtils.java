package net.kem.tquickfix;

import net.kem.tquickfix.blocks.QFCommonMessage;
import net.kem.tquickfix.blocks.QFTag;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * QF Utilities.
 * User: EvgenyK
 * Date: 12/15/14
 * Time: 8:00 AM
 */
public class QFUtils {
    private static final Logger LOGGER = Logger.getLogger(QFUtils.class);
    private static final Pattern PAIR = Pattern.compile("(\\d+)=(.*)");
    private static final Pattern CHECK_SUM = Pattern.compile("(.+)\\u000110=(\\d{1,3})\\u0001+");

    /**
     * Sums up the decimal value of the ASCII representation of all the bytes up to the checksum field (which is last) and return the value modulo 256.
     * @param fixMessage every character from the initial '8' up to and including the end-of-field marker that immediately precedes the checksum field.
     * @return checksum.
     */
    public static int calculateCheckSum(CharSequence fixMessage) {
        if(fixMessage == null || fixMessage.length() == 0) {
            return 0;
        }
        int checkSum = 0;
        for(int i=0; i<fixMessage.length(); i++) {
            checkSum += (int)fixMessage.charAt(i);
        }
        System.out.println("Checksum is "+(checkSum%256));
        return checkSum%256;
    }

    /**
     * Checks whether checksum of the given FIX message is correct.
     * @param fixMessage    full FIX message (including the checksum field and the end-of-field marker SOH).
     * @return <em>true</em> if the given FIX message contains correct checksum; otherwise the method returns <em>false</em>.
     */
    public static boolean testCheckSum(CharSequence fixMessage) {
        Pair<String, Integer> checkSumPair = splitCheckSum(fixMessage);
        if(checkSumPair == null) {
            return false;
        }
        int calculatedCheckSum = checkSumPair.getRight();
        int origCheckSum = calculateCheckSum(checkSumPair.getLeft());
        return calculatedCheckSum == origCheckSum;
    }

    /**
     * Checks whether checksum of the given FIX message is correct.
     * @param fixMessage    full FIX message (including the checksum field and the end-of-field marker SOH).
     * @return <em>true</em> if the given FIX message contains correct checksum; otherwise the method returns <em>false</em>.
     */
    public static boolean testCheckSum(QFCommonMessage fixMessage) {
        return testCheckSum(fixMessage.toFIXString());
    }




    private static Pair<String, Integer> splitCheckSum(CharSequence fixMessage) {
        if(fixMessage == null || fixMessage.length() == 0) {
            return null;
        }
        Matcher matcher = CHECK_SUM.matcher(fixMessage);
        if(matcher.matches()) {
            String body = matcher.group(1);
            Integer checksum = Integer.parseInt(matcher.group(2));
            return new ImmutablePair<String, Integer>(body, checksum);
        }
        return null;
    }

    /**
     * This method is used by the framework and should not be used by end user directly.
     */
    public static List<QFTag> getTagPairs(CharSequence fixString) throws ParseException {
        if(fixString == null || fixString.length() == 0) {
            return Collections.emptyList();
        }
        String[] pairs = fixString.toString().split("\u0001|\r|\n");
        if(pairs.length > 6) {
            int failuresCounter = 0;
            List<QFTag> kv = new ArrayList<QFTag>(pairs.length);
            for(String pair: pairs) {
                Matcher m = PAIR.matcher(pair);
                if(m.matches()) {
                    kv.add(new QFTag(Integer.parseInt(m.group(1)), m.group(2)));
                } else {
                    if(failuresCounter++ > 5) {
                        throw new ParseException("More than " + failuresCounter + " invalid tag=value pairs were detected. The message is probably corrupted or not a FIX message. Giving up.", failuresCounter);
                    }
                    LOGGER.warn("Message portion " + pair + " cannot be presented as a valid tag=value pair.");
                }
            }
            if(kv.size() < 6) {
                throw new ParseException("Too few valid tag=value pairs in the message " + fixString, 0);
            }
            return kv;
        } else {
            throw new ParseException("Too few tag=value pairs in the message " + fixString, 0);
        }
    }

    /**
     * This method is used by the framework and should not be used by end user directly.
     */
    public static String readFile(File file) throws IOException {
        LinesCollector lh = new LinesCollector();
        readFile(file, lh);
        return lh.getResult();
    }

    /**
     * This method is used by the framework and should not be used by end user directly.
     */
    public static void readFile(File file, ReadLineHandler handler) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null) {
                handler.lineRead(line);
            }
            handler.eof();
        } finally {
            if(br != null) {
                br.close();
            }
        }
    }
}