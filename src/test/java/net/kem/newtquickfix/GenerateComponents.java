package net.kem.newtquickfix;

import net.kem.newtquickfix.builders.BuilderUtils;
import net.kem.newtquickfix.builders.QFComponentElement;
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
public class GenerateComponents {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ClassNotFoundException, ParseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //Get the DOM Builder Factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Load and Parse the XML document
        Document document = builder.parse("D:\\projects\\HLSTools\\TQuickFix\\src\\main\\resources\\xml\\FIX50SP2.xml");

        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression expr;
        NodeList nodes;

        /*
        <component name="PegInstructions">
            <XXX name="PegOffsetValue" required="N"/>
            |
         */
        expr = xpath.compile("//component/*[1]");
        nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        for (int j = 0; j < nodes.getLength(); j++) {
            Node node = nodes.item(j);
            if (node instanceof Element) {
                Element child = (Element) node;
                if(child.getTagName().equals("field")) {
                    Element parentNode = (Element)child.getParentNode();
                    BuilderUtils.COMPONENTS_FIRST_FIELD.put(parentNode.getAttribute("name"), child.getAttribute("name"));
                }
            }
        }

        /*
        <component name="RgstDistInstGrp">
            <group name="NoDistribInsts" required="N">
                <XXX name="DistribPaymentMethod" required="N"/>
         */
        expr = xpath.compile("//component/group/*[1]");
        nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        for (int j = 0; j < nodes.getLength(); j++) {
            Node node = nodes.item(j);
            if (node instanceof Element) {
                Element child = (Element) node;
                if(child.getTagName().equals("field")) {
                    Element parentNode = (Element)child.getParentNode().getParentNode();
                    BuilderUtils.COMPONENTS_FIRST_FIELD.put(parentNode.getAttribute("name"), child.getAttribute("name"));
                }

            }
        }

        File dir = new File("D:\\projects\\HLSTools\\TQuickFix\\src\\main\\java\\net\\kem\\newtquickfix\\components");
        dir.mkdirs();
        expr = xpath.compile("/fix/components/component | /fix/header | /fix/trailer");// //person/*//*text()
        nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        for (int j = 0; j < nodes.getLength(); j++) {
            Node fieldNode = nodes.item(j);
            if (fieldNode instanceof Element) {
                QFComponentElement block = new QFComponentElement((Element) fieldNode, new StringBuilder(), "");
                block.toJavaSource();
//				System.out.println();
                File file = new File(dir, block.getJavaSourceFileName() + ".java");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(block.getJavaSource().toString());
                fileWriter.close();
            }
        }
    }
}
