package net.kem.newtquickfix;

import net.kem.newtquickfix.builders.BuilderUtils;
import net.kem.newtquickfix.builders.QFComponentElement;
import net.kem.newtquickfix.builders.QFFieldElement;
import net.kem.newtquickfix.builders.QFMessageElement;
import net.kem.newtquickfix.builders.QFMessageInterface;
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

/**
 * Created by Evgeny Kurtser on 1/5/2016 at 5:04 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class FIXGenerator {
    protected static XPath xpath = XPathFactory.newInstance().newXPath();
    protected Document document;

    //Get the DOM Builder Factory
    protected static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public static FIXGenerator of(String fixConfigFileName) throws ParserConfigurationException, IOException, SAXException {
        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();

        FIXGenerator res = new FIXGenerator();
        //Load and Parse the XML document
        res.document = builder.parse(fixConfigFileName);
        setFIXVersion(res.document);
        return res;
    }

    public FIXGenerator init() {
        BuilderUtils.COMPONENTS_FIRST_FIELD.clear();
        return this;
    }

    protected FIXGenerator fields() throws XPathExpressionException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        XPathExpression expr;
        expr = xpath.compile("/fix/fields/field");// //person/*//*text()
        File dirFields = new File("./TQuickFix/src/main/java/" + BuilderUtils.PACKAGE_NAME_FIELDS.replace('.', '/'));// net.kem.newtquickfix.5_0_sp2.fields
        dirFields.mkdirs();
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        for (int j = 0; j < nodes.getLength(); j++) {
            Node fieldNode = nodes.item(j);
            if (fieldNode instanceof Element) {
                QFFieldElement fieldBrick = QFFieldElement.getNewQFFieldBrick((Element) fieldNode);
                fieldBrick.toJavaSource();
//				System.out.println();
                File file = new File(dirFields, fieldBrick.getJavaSourceFileName() + ".java");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(fieldBrick.getJavaSource().toString());
                fileWriter.close();
            }
        }
        return this;
    }

    protected FIXGenerator components() throws XPathExpressionException, IOException {
        XPathExpression expr;
        NodeList nodes;

//        /*
//        <group name="NoUnderlyings">
//            <component name="UnderlyingInstrument" required="N">
//                <XXX name="UnderlyingSymbol" required="N"/>
//         */
//        expr = xpath.compile("/fix/messages/message/group/component");
//        nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
//        for (int j = 0; j < nodes.getLength(); j++) {
//            Node node = nodes.item(j);
//            if (node instanceof Element) {
//                Element child = (Element) node;
//                if(child.getTagName().equals("field")) {
//                    Element parentNode = (Element)child.getParentNode().getParentNode();
//                    BuilderUtils.COMPONENTS_FIRST_FIELD.put(parentNode.getAttribute("name"), child.getAttribute("name"));
//                }
//
//            }
//        }
//
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
                final CharSequence parentName = ((Element)child.getParentNode()).getAttribute("name");
                CharSequence childName = child.getAttribute("name");
                switch(child.getTagName()) {
                    case "field":
                        break;
                    case "group": {
                        CharSequence storedChildName = BuilderUtils.COMPONENTS_FIRST_FIELD.get(childName);
                        if(storedChildName != null) {
                            childName = storedChildName;
                        }
                        break;
                    }
                }
                if(childName != null) {
                    BuilderUtils.COMPONENTS_FIRST_FIELD.put(parentName, childName);
                } else {
                    System.out.println("No " + child.getAttribute("name") + " for " + parentName);
                }
            }
        }

        /*
        <group name="RgstDistInstGrp">
            <component name="NoDistribInsts" required="N">
                <XXX name="DistribPaymentMethod" required="N"/>
         */
        expr = xpath.compile("//group/*[1]");
        nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        for (int j = 0; j < nodes.getLength(); j++) {
            Node node = nodes.item(j);
            if (node instanceof Element) {
                Element child = (Element) node;
                final CharSequence parentName = ((Element)child.getParentNode()).getAttribute("name");
                CharSequence childName = child.getAttribute("name");
                switch(child.getTagName()) {
                    case "field":
                        break;
                    case "component": {
                        CharSequence storedChildName = BuilderUtils.COMPONENTS_FIRST_FIELD.get(childName);
                        if(storedChildName != null) {
                            childName = storedChildName;
                        }
                        break;
                    }
                }
                if(childName != null) {
                    BuilderUtils.COMPONENTS_FIRST_FIELD.put(parentName, childName);
                } else {
                    System.out.println("No " + child.getAttribute("name") + " for " + parentName);
                }
            }
        }

        File dirComponents = new File("./TQuickFix/src/main/java/" + BuilderUtils.PACKAGE_NAME_COMPONENTS.replace('.', '/'));// net.kem.newtquickfix.5_0_sp2.components
        dirComponents.mkdirs();
        File componentInterfacesDir = new File("./TQuickFix/src/main/java/" + BuilderUtils.PACKAGE_NAME_MESSAGES.replace('.', '/'));// net.kem.newtquickfix.5_0_sp2.messages
        componentInterfacesDir.mkdirs();
        expr = xpath.compile("/fix/components/component | /fix/header | /fix/trailer");// //person/*//*text()
        nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        for (int j = 0; j < nodes.getLength(); j++) {
            Node fieldNode = nodes.item(j);
            if (fieldNode instanceof Element) {
                // Generate and store component.
                QFComponentElement block = new QFComponentElement((Element) fieldNode, new StringBuilder(), "");
                block.toJavaSource();
                File file = new File(dirComponents, block.getJavaSourceFileName() + ".java");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(block.getJavaSource().toString());
                fileWriter.close();

                // Generate and store component interface. This QFMessage might implement this interface.
                String componentInterfaceSource = QFMessageInterface.buildInterfaceSource(block.getJavaSourceFileName());
                file = new File(componentInterfacesDir, "I" + block.getJavaSourceFileName() + ".java");
                fileWriter = new FileWriter(file);
                fileWriter.write(componentInterfaceSource);
                fileWriter.close();
            }
        }
        return this;
    }

    protected FIXGenerator messages() throws XPathExpressionException, IOException {
        XPathExpression expr;
        NodeList nodes;

        File dirMessages = new File("./TQuickFix/src/main/java/" + BuilderUtils.PACKAGE_NAME_MESSAGES.replace('.', '/'));// net.kem.newtquickfix.5_0_sp2.messages
        dirMessages.mkdirs();

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
                File file = new File(dirMessages, qfElement.getJavaSourceFileName() + ".java");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(qfElement.getJavaSource().toString());
                fileWriter.close();
            }
        }
        return this;
    }

    static void setFIXVersion(Document document) {
        // <fix major='5' type='FIX' servicepack='2' minor='0'>
        String major = document.getDocumentElement().getAttribute("major");
        String minor = document.getDocumentElement().getAttribute("minor");
        String servicepack = document.getDocumentElement().getAttribute("servicepack");

        StringBuilder version = new StringBuilder(8);
        version.append("v").append(major).append("").append(minor);
        if(!servicepack.isEmpty() && !servicepack.equals("0")) {
            version.append("sp").append(servicepack);
        }
        BuilderUtils.updatePackagePath(version);
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, NoSuchMethodException, InstantiationException, XPathExpressionException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        FIXGenerator.of("./TQuickFix/src/main/resources/xml/FIX40.xml")
                .init()
                .fields()
                .components()
                .messages();
        FIXGenerator.of("./TQuickFix/src/main/resources/xml/FIX44.xml")
                .init()
                .fields()
                .components()
                .messages();
        FIXGenerator.of("./TQuickFix/src/main/resources/xml/FIX50.xml")
                .init()
                .fields()
                .components()
                .messages();
        FIXGenerator.of("./TQuickFix/src/main/resources/xml/FIX50SP1.xml")
                .init()
                .fields()
                .components()
                .messages();
        FIXGenerator.of("./TQuickFix/src/main/resources/xml/FIX50SP2.xml")
                .init()
                .fields()
                .components()
                .messages();
    }
}
