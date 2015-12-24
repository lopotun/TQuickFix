package net.kem.newtquickfix;

import net.kem.newtquickfix.builders.QFMessageElement;
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
public class GenerateMessages {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ClassNotFoundException, ParseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        File dir = new File("./src/main/java/net/kem/newtquickfix/messages");
        dir.mkdirs();

        //Get the DOM Builder Factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Load and Parse the XML document
        Document document = builder.parse("./src/main/resources/xml/FIX50SP2.xml");

        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression expr;
        NodeList nodes;

        expr = xpath.compile("/fix/header | /fix/trailer");// //person/*//*text()
        nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        Element header = (Element)nodes.item(0);
        Element trailer = (Element)nodes.item(1);

        expr = xpath.compile("/fix/messages/message");// //person/*//*text()
        nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        for (int j = 0; j < nodes.getLength(); j++) {
            Node fieldNode = nodes.item(j);
            if (fieldNode instanceof Element) {
                QFMessageElement qfElement = new QFMessageElement((Element) fieldNode, new StringBuilder(), "");
                qfElement.addHeader(header);
                qfElement.addTrailer(trailer);
                qfElement.toJavaSource();
//				System.out.println();
                File file = new File(dir, qfElement.getJavaSourceFileName() + ".java");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(qfElement.getJavaSource().toString());
                fileWriter.close();
            }
        }
    }
}
