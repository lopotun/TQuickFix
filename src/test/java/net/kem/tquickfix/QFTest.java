package net.kem.tquickfix;

import net.kem.tquickfix.blocks.QFCommonMessage;
import net.kem.tquickfix.blocks.QFField;
import net.kem.tquickfix.blocks.QFTag;
import net.kem.tquickfix.builder.QFBuilder;
import net.kem.tquickfix.builder.QFBuilderConfig;
import org.xml.sax.SAXException;
import tqf.v50sp2.common.IParties;
import tqf.v50sp2.field.AllocAvgPx;
import tqf.v50sp2.field.AllocCancReplaceReason;
import tqf.v50sp2.field.Concession;
import tqf.v50sp2.message.AllocationInstructionMessage;
import tqf.v50sp2.message.ExecutionReportMessage;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 10/29/14
 * Time: 10:20 AM
 * Tests
 */
public class QFTest {
    private static QFBuilderConfig cfdConfig;
    public static void main(String[] args) throws IOException, ParseException, XPathExpressionException, SAXException, ParserConfigurationException {
        QFTest test = new QFTest();

        // Bind parser configuration with product (asset) environment (in this case: CFD).
        String configXMLFileName = "D:\\HLSTools\\TQuickFix\\src\\main\\resources\\tqfParser.xml";
        QFBuilderConfig.init("CFD", configXMLFileName);
        cfdConfig = QFBuilderConfig.getInstance("CFD");

//        test.testBuilder();
        test.testParseMessage(new File("D:\\Messages")); // "D:\\Messages\\Equities\\allocation J 1.txt"  \Equities
//        test.testCreateMessage();
    }

    public void testBuilder() throws XPathExpressionException, SAXException, ParserConfigurationException, IOException {
//        QFBuilder.buildJavaSources("xml/FIX40.xml",     "D:/HLSTools/TQuickFix/src/main/java", "v40");
//        QFBuilder.buildJavaSources("xml/FIX44.xml",     "D:/HLSTools/TQuickFix/src/main/java", "v44");
//        QFBuilder.buildJavaSources("xml/FIX50.xml",     "D:/HLSTools/TQuickFix/src/main/java", "v50");
//        QFBuilder.buildJavaSources("xml/FIX50SP1.xml",  "D:/HLSTools/TQuickFix/src/main/java", "v50sp1");
        QFBuilder.buildJavaSources("xml/FIX50SP2.xml",  "D:/HLSTools/TQuickFix/src/main/java", "tqf.", "v50sp2");
    }

    public void testParseMessage(File fixFile) throws IOException {
        if(fixFile.isDirectory()) {
            File[] files = fixFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.getName().endsWith("xls") && !pathname.getName().endsWith("xml");// && !pathname.getName().endsWith("msg")
                }
            });
            for(File file : files) {
                testParseMessage(file);
            }
            return;
        }

        try {
            System.out.print("------------------------------------------\n");
            QFCommonMessage msg = QFParser.getInstance().parseMessage(fixFile, cfdConfig);
//            msg.isValid(QFField.Validation.FULL);

            // Check for errors
            if(msg.hasErrors()) {
                System.err.println("Parsing errors:");
                Map<ThreadContext.ErrorType, List<CharSequence>> errors = msg.getAllErrors();
                for(Map.Entry<ThreadContext.ErrorType, List<CharSequence>> errorTypeListEntry : errors.entrySet()) {
                    if(!errorTypeListEntry.getValue().isEmpty()) {
                        System.err.println('\t' + errorTypeListEntry.getKey().toString() + ':' + errorTypeListEntry.getValue().toString());
                    }
                }
            }

            // Check for consistency.
            String rawMsg = QFUtils.readFile(fixFile);
            List<QFTag> rawMsgTagPairs = QFUtils.getTagPairs(rawMsg);  // Original tags
            List<QFTag> resMsgTagPairs = QFUtils.getTagPairs(msg.toFIXString());   // Parsed tags

            if(rawMsgTagPairs.size() == resMsgTagPairs.size()) {
                List<QFTag> tmp = new ArrayList<>(rawMsgTagPairs);
                rawMsgTagPairs.removeAll(rawMsgTagPairs);
                if(rawMsgTagPairs.isEmpty()) {
                    resMsgTagPairs.removeAll(tmp);
                    if(resMsgTagPairs.isEmpty()) {
//                        System.out.println("OK\t" + fixFile.getName());
                        return;
                    }
                }
            }
            List<QFTag> unrecognizedFields = msg.getUnrecognizedFields();
            if(unrecognizedFields != null) {
                System.err.println("Unrecognized/misplaced tags:");
                for(QFTag unrecognizedField : unrecognizedFields) {
                    System.err.println('\t' + unrecognizedField.toString());
                }
            }
        } catch(ParseException e) {
            e.printStackTrace();
        }
    }

    public void testCreateMessage() throws IOException {
        try {
            AllocationInstructionMessage ai = (AllocationInstructionMessage)QFParser.getInstance().parseMessage(new File("D:\\Messages\\_CRs\\71631\\21_J_ol_Client235.txt"), cfdConfig);

            IParties partiesAI = ai;    // AllocationInstructionMessage can be present as IParties
            System.out.print("------------------------------------------\n");
            ExecutionReportMessage msg = new ExecutionReportMessage(true, cfdConfig);
            IParties partiesER = msg;   // ExecutionReportMessage can be present as IParties
            // IParties (and any other interface) can be transformed across messages.
            partiesER.setPartiesComponent(partiesAI.getPartiesComponent());

            AllocAvgPx a = AllocAvgPx.getInstance(BigDecimal.TEN);  // No validation is applied to non-string values.
            // By default, full validation is applied when a value is present as a string. However, the validation can be turned off.
            AllocAvgPx b = AllocAvgPx.getInstance("20.0", QFField.Validation.NONE);

            // Set predefined constant. No validation needed.
            ai.setAllocCancReplaceReason(AllocCancReplaceReason.ORIGINAL_DETAILS_INCOMPLETE_INCORRECT);
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.NONE): "Should be always OK";
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.OMIT_PREDEFINED): "Predefined value should be OK";
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.FULL): "Predefined value should be OK";

            // Set value of predefined constant. Full pre-validation.
            ai.setAllocCancReplaceReason(AllocCancReplaceReason.getInstance(2, QFField.Validation.FULL));
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.NONE): "Should be always OK";
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.OMIT_PREDEFINED): "Predefined value should be OK";
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.FULL): "Predefined value should be OK";

            // Set non-predefined value. Predefined pre-validation.
            ai.setAllocCancReplaceReason(AllocCancReplaceReason.getInstance(8, QFField.Validation.OMIT_PREDEFINED));
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.NONE): "Should be always OK";
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.OMIT_PREDEFINED): "Non-predefined value should be OK";
            assert !ai.getAllocCancReplaceReason().isValid(QFField.Validation.FULL): "Non-predefined value should fail";

            // Set non-predefined value. No pre-validation.
            ai.setAllocCancReplaceReason(AllocCancReplaceReason.getInstance(7, QFField.Validation.NONE));
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.NONE): "Should be always OK";
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.OMIT_PREDEFINED): "Non-predefined value should be OK";
            assert !ai.getAllocCancReplaceReason().isValid(QFField.Validation.FULL): "Non-predefined value should fail";

            // Set non-predefined string value. Full pre-validation.
            try {
                ai.setAllocCancReplaceReason(AllocCancReplaceReason.getInstance("9", QFField.Validation.FULL));
                assert false: "Non-predefined value should fail";
            } catch(IllegalArgumentException e) {
                // It's OK.
            }

            // Set invalid string value. Full pre-validation.
            try {
                ai.setAllocCancReplaceReason(AllocCancReplaceReason.getInstance("la-la-la", QFField.Validation.FULL));
                assert false: "Invalid value should fail";
            } catch(IllegalArgumentException e) {
                // It's OK.
            }

            // Set invalid value. Predefined pre-validation.
            try {
                ai.setAllocCancReplaceReason(AllocCancReplaceReason.getInstance("la-la-la", QFField.Validation.OMIT_PREDEFINED));
                assert false: "Invalid value should fail";
            } catch(IllegalArgumentException e) {
                // It's OK.
            }

            // Set invalid value. No pre-validation.
            ai.setAllocCancReplaceReason(AllocCancReplaceReason.getInstance("la-la-la", QFField.Validation.NONE));
            assert ai.getAllocCancReplaceReason().isValid(QFField.Validation.NONE): "Should be always OK";
            assert !ai.getAllocCancReplaceReason().isValid(QFField.Validation.OMIT_PREDEFINED): "Invalid value should fail";
            assert !ai.getAllocCancReplaceReason().isValid(QFField.Validation.FULL): "Invalid value should fail";

            // Set non-predefined value. Full validation.
            try {
                ai.setAllocCancReplaceReason(AllocCancReplaceReason.getInstance(9, QFField.Validation.FULL));
                assert false;
            } catch(IllegalArgumentException e) {
                // It's OK.
            }

            // Set type value. Full validation.
            ai.setConcession(Concession.getInstance(543210d, QFField.Validation.FULL));
            assert ai.getConcession().isValid(QFField.Validation.FULL): "Should be always OK";

            // Set string value. Full validation.
            ai.setConcession(Concession.getInstance("543210", QFField.Validation.FULL));
            assert ai.getConcession().isValid(QFField.Validation.FULL): "Should be always OK";

            // Set invalid string value. Full validation.
            try {
                ai.setConcession(Concession.getInstance("54-la-la-la-3210", QFField.Validation.FULL));
                assert false: "Invalid value should fail";
            } catch(IllegalArgumentException e) {
                // It's OK.
            }

            // Set invalid string value. No predefined validation.
            try {
                ai.setConcession(Concession.getInstance("54-la-la-la-3210", QFField.Validation.OMIT_PREDEFINED));
                assert false: "Invalid value should fail";
            } catch(IllegalArgumentException e) {
                // It's OK.
            }


            IParties parties = msg;
            if(parties.getPartiesComponent() != null) {
                System.out.println("****\t" + parties.getPartiesComponent().toFIXString() + "\t****");
            }

            // Check for errors
            if(msg.hasErrors()) {
                System.err.println("Parsing errors:");
                Map<ThreadContext.ErrorType, List<CharSequence>> errors = msg.getAllErrors();
                for(Map.Entry<ThreadContext.ErrorType, List<CharSequence>> errorTypeListEntry : errors.entrySet()) {
                    if(!errorTypeListEntry.getValue().isEmpty()) {
                        System.err.println('\t' + errorTypeListEntry.getKey().toString() + ':' + errorTypeListEntry.getValue().toString());
                    }
                }
            } else {
                System.out.println("No parsing errors:");
            }
            System.out.println(msg.toFIXString());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //	private boolean test01() {
//		List<String[]> rawData = new LinkedList<>();
//		rawData.add(new String[]{"1", "C"});    // 0
//		rawData.add(new String[]{"5", "4"});    // 1
//		rawData.add(new String[]{"100", "3"});  // 2
//		rawData.add(new String[]{"1", "N"});    // 3
//		rawData.add(new String[]{"2", "B"});    // 4
//		rawData.add(new String[]{"1", "C"});    // 5
//		rawData.add(new String[]{"2", "S"});    // 6
//		rawData.add(new String[]{"1", "R"});    // 7
//		rawData.add(new String[]{"2", "T"});    // 8
//		rawData.add(new String[]{"3", "25"});   // 9
//		rawData.add(new String[]{"4", "USD"});  // 10
//		rawData.add(new String[]{"5", "0"});    // 11
//		rawData.add(new String[]{"10", "0"});   // 12
//		MutableInt index = new MutableInt(0);
//		QFCommonMessage msg = Msg01.parse(rawData, index);
//		System.out.println(msg.toFIXString());
//		System.out.println(msg.isValid());
//
//		Msg01 msg1 = (Msg01)msg;
//		System.out.println(msg1.getComp01().getGroupQFGroupGrp01().get(0).getFld01().toFIXString());
//		return true;
//	}
}