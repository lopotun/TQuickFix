package net.kem.newtquickfix;

import net.kem.newtquickfix.builders.BuilderUtils;
import net.kem.newtquickfix.builders.QFFieldElement;
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
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

/**
 * Created by Evgeny Kurtser on 11/10/2015 at 9:29 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class GenerateFields {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ClassNotFoundException, ParseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        QFField testT1 = AggressorIndicator.of("true");
//        QFField testT2 = AggressorIndicator.of(true);
//        QFField testT3 = AggressorIndicator.of("Y");
//        QFField testT4 = AggressorIndicator.ORDER_INITIATOR_IS_AGGRESSOR;
//        QFField testF1 = AggressorIndicator.of("false");
//        QFField testF2 = AggressorIndicator.of(false);
//        QFField testF3 = AggressorIndicator.of("n");
//        QFField testF4 = AggressorIndicator.ORDER_INITIATOR_IS_PASSIVE;
//        System.out.println(testF1.toFixString() + ", " + testF2.toFixString());
//        System.out.println(testT1.toFixString() + ", " + testT2.toFixString());
//
//        QFField testDT1 = EffectiveTime.of("20151109-14:55:07");//yyyyMMdd-HH:mm:ss
//        QFField testDT2 = EffectiveTime.of(LocalDateTime.now());
//        System.out.println(testDT1.toFixString() + ", " + testDT2.toFixString());

        //Get the DOM Builder Factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Load and Parse the XML document
        //Document document = builder.parse("./TQuickFix/src/main/resources/xml/FIX50SP2.xml");
        Document document = builder.parse("./TQuickFix/src/main/resources/xml/FIX44.xml");

        setFIXVersion(document);

        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression expr;
        NodeList nodes;
        expr = xpath.compile("/fix/fields/field");// //person/*//*text()
        //File dir = new File("./TQuickFix/src/main/java/net/kem/newtquickfix/fields");// net.kem.newtquickfix.5_0_sp2.fields
        File dir = new File("./TQuickFix/src/main/java/" + BuilderUtils.PACKAGE_NAME_FIELDS.replace('.', '/'));// net.kem.newtquickfix.5_0_sp2.fields
        dir.mkdirs();
        nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        for (int j = 0; j < nodes.getLength(); j++) {
            Node fieldNode = nodes.item(j);
            if (fieldNode instanceof Element) {
                QFFieldElement fieldBrick = QFFieldElement.getNewQFFieldBrick((Element) fieldNode);
                fieldBrick.toJavaSource();
//				System.out.println();
                File file = new File(dir, fieldBrick.getJavaSourceFileName() + ".java");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(fieldBrick.getJavaSource().toString());
                fileWriter.close();
            }
        }
    }

    static void setFIXVersion(Document document) {
        // <fix major='5' type='FIX' servicepack='2' minor='0'>
        String major = document.getDocumentElement().getAttribute("major");
        String minor = document.getDocumentElement().getAttribute("minor");
        String servicepack = document.getDocumentElement().getAttribute("servicepack");

        StringBuilder version = new StringBuilder(8);
        version.append("v").append(major).append("").append(minor);
        if(!servicepack.equals("0")) {
            version.append("sp").append(servicepack);
        }
        BuilderUtils.updatePackagePath(version);
    }

    static void setFIXVersion(CharSequence version) {
        BuilderUtils.updatePackagePath(version);
    }
}
