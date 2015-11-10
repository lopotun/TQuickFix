package net.kem.newtquickfix;

import net.kem.newtquickfix.builders.QFFieldBrick;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Evgeny Kurtser on 11/10/2015 at 9:29 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class GenerateFields {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ClassNotFoundException, ParseException {
//        QFField testT1 = AggressorIndicator.getInstance("true");
//        QFField testT2 = AggressorIndicator.getInstance(true);
//        QFField testT3 = AggressorIndicator.getInstance("Y");
//        QFField testT4 = AggressorIndicator.ORDER_INITIATOR_IS_AGGRESSOR;
//        QFField testF1 = AggressorIndicator.getInstance("false");
//        QFField testF2 = AggressorIndicator.getInstance(false);
//        QFField testF3 = AggressorIndicator.getInstance("n");
//        QFField testF4 = AggressorIndicator.ORDER_INITIATOR_IS_PASSIVE;
//        System.out.println(testF1.toFixString() + ", " + testF2.toFixString());
//        System.out.println(testT1.toFixString() + ", " + testT2.toFixString());
//
//        QFField testDT1 = EffectiveTime.getInstance("20151109-14:55:07");//yyyyMMdd-HH:mm:ss
//        QFField testDT2 = EffectiveTime.getInstance(LocalDateTime.now());
//        System.out.println(testDT1.toFixString() + ", " + testDT2.toFixString());

        //Get the DOM Builder Factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Load and Parse the XML document
        Document document = builder.parse("D:\\projects\\HLSTools\\TQuickFix\\src\\main\\resources\\xml\\FIX50SP2.xml");

        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression expr;
        NodeList nodes;
        expr = xpath.compile("/fix/fields/field");// //person/*//*text()
        File dir = new File("D:\\projects\\HLSTools\\TQuickFix\\src\\main\\java\\net\\kem\\newtquickfix\\fields");
        dir.mkdirs();
        nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        for (int j = 0; j < nodes.getLength(); j++) {
            Node fieldNode = nodes.item(j);
            if (fieldNode instanceof Element) {
                QFFieldBrick fieldBrick = QFFieldBrick.getNewQFFieldBrick((Element) fieldNode);
                QFFieldBrick.Container javaSourceContainer = fieldBrick.toJavaSource((Element) fieldNode);
//				System.out.println();
                File file = new File(dir, javaSourceContainer.getJavaSourceFileName() + ".java");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(javaSourceContainer.getJavaSource().toString());
                fileWriter.close();
            }
        }
    }
}
