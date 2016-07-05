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
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by Evgeny Kurtser on 1/5/2016 at 5:04 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class FIXGenerator {
    private static XPath xpath = XPathFactory.newInstance().newXPath();
    private Document document;
    private NodeList xmlComponents;

    //Get the DOM Builder Factory
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private static FIXGenerator of(String fixConfigFileName) throws ParserConfigurationException, IOException, SAXException {
        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();

        FIXGenerator res = new FIXGenerator();
        //Load and Parse the XML document
        res.document = builder.parse(fixConfigFileName);
        //Optional, but recommended. Read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        res.document.getDocumentElement().normalize();
        setFIXVersion(res.document);
        return res;
    }

    private FIXGenerator init() throws XPathExpressionException {
        xmlComponents = null;
        BuilderUtils.COMPONENTS_FIRST_FIELD.clear();
        // Fill Component-first-field values.
        NodeList components = getXMLComponents();
        for (int j = 0; j < components.getLength(); j++) {
            Element component = (Element) components.item(j);//<component>
            CharSequence firstField = getFirstField(component);
            BuilderUtils.COMPONENTS_FIRST_FIELD.put(component.getAttribute("name"), firstField);
        }
        return this;
    }

    private String getFirstField(Element component) throws XPathExpressionException {
        Node firstChild = component.getFirstChild();
        if(firstChild != null) {//First child is either <component> or <group> or <field>
            if(firstChild instanceof Element) {
                if(((Element)firstChild).getTagName().equals("field")) {//<field>
                    return ((Element)firstChild).getAttribute("name");
                } else {//Either <component> or <group>
                    return getFirstField((Element)firstChild);
                }
            } else {//First child is Text. Remove it and try again.
                component.removeChild(firstChild);
                return getFirstField(component);
            }
        } else { // <component name="LegOrdGrp"> -> <group name="NoLegs" required="Y"> -> <component name="InstrumentLeg" required="N"/>
            final String componentName = component.getAttribute("name");
            final Element componentByName = getComponentByName(componentName);
            return componentByName==null? null: getFirstField(componentByName);
        }
    }

    private Element getComponentByName(CharSequence name) throws XPathExpressionException {
        NodeList components = getXMLComponents();
        for (int j = 0; j < components.getLength(); j++) {
            Element component = (Element) components.item(j);//<component>
            if(component.getAttribute("name").equals(name)) {
                return component;
            }
        }
        return null;
    }

    private FIXGenerator fields() throws XPathExpressionException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
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

    private FIXGenerator components() throws XPathExpressionException, IOException {
        File dirComponents = new File("./TQuickFix/src/main/java/" + BuilderUtils.PACKAGE_NAME_COMPONENTS.replace('.', '/'));// net.kem.newtquickfix.5_0_sp2.components
        dirComponents.mkdirs();
        File componentInterfacesDir = new File("./TQuickFix/src/main/java/" + BuilderUtils.PACKAGE_NAME_MESSAGES.replace('.', '/'));// net.kem.newtquickfix.5_0_sp2.messages
        componentInterfacesDir.mkdirs();
        NodeList nodes = getXMLComponents();
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

    private FIXGenerator messages() throws XPathExpressionException, IOException {
        XPathExpression expr;

        File dirMessages = new File("./TQuickFix/src/main/java/" + BuilderUtils.PACKAGE_NAME_MESSAGES.replace('.', '/'));// net.kem.newtquickfix.5_0_sp2.messages
        dirMessages.mkdirs();

        Path from = Paths.get("./TQuickFix/src/main/resources/java/" + BuilderUtils.PACKAGE_FIX_VERSION + "/AMessage.jav");
        Path to = Paths.get(dirMessages.getPath(), "AMessage.java");
        //overwrite existing file, if exists
        CopyOption[] options = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        };
        Files.copy(from, to, options);

        expr = xpath.compile("/fix/header | /fix/trailer");// //person/*//*text()
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
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

    private static void setFIXVersion(Document document) {
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

    private NodeList getXMLComponents() throws XPathExpressionException {
        if(xmlComponents == null) {
            XPathExpression expr = xpath.compile("/fix/components/component | /fix/header | /fix/trailer");// //person/*//*text()
            xmlComponents = (NodeList) expr.evaluate(document, XPathConstants.NODESET);//NodeList components = document.getElementsByTagName("components");
        }
        return xmlComponents;
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