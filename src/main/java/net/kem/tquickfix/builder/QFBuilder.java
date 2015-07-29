package net.kem.tquickfix.builder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 10/29/14
 * Time: 10:20 AM
 * Generated QF source Java files
 */
public class QFBuilder {
	static CharSequence qfVersion = "50sp1";
	static String sourcesPackage = "net.kem.tquickfix.qf.";
	private static File javaSourcesOutputDir = new File("D:/Temp/QF/com/traiana/tquickfix/qf");
	private static File classOutputDir;
	private static File jarFile;
	private static InputStream qfStructureXML;
	private static Set<QFComponentClassBuilder.NameReq> allComponents = new HashSet<>(128);

	/**
	 * Sets output directory for newly generated QF source Java files (e.g. D:/Temp/QF/com/traiana/tquickfix/qf)
	 *
	 * @param javaSourcesOutputDir
	 */
	private static void setJavaSourcesOutputDir(CharSequence javaSourcesOutputDir, CharSequence qfVersion) {
		CharSequence subDir =  sourcesPackage.replace('.', '/');
		QFBuilder.javaSourcesOutputDir = new File(javaSourcesOutputDir + "/" + subDir + qfVersion);// "/com/traiana/tquickfix/qf/"
		QFBuilder.qfVersion = qfVersion;
	}

	/**
	 * Sets input source of QuickFix structure XML file (such like xml/FIX50SP2.xml or xml/HeaderFooter.xml)
	 *
	 * @param qfStructureXML
	 */
	private static void setQfStructureXML(InputStream qfStructureXML) {
		QFBuilder.qfStructureXML = qfStructureXML;
	}

	/**
	 * Sets name of QuickFix structure XML file (such like xml/FIX50SP2.xml or xml/HeaderFooter.xml)
	 *
	 * @param qfStructureXMLFile
	 */
	private static void setQfStructureXML(CharSequence qfStructureXMLFile) {
		QFBuilder.qfStructureXML = ClassLoader.getSystemResourceAsStream(qfStructureXMLFile.toString());
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
//		QFMessage msg = Msg01.parse(rawData, index);
//		System.out.println(msg.toFIXString());
//		System.out.println(msg.isValid());
//
//		Msg01 msg1 = (Msg01)msg;
//		System.out.println(msg1.getComp01().getGroupQFGroupGrp01().get(0).getFld01().toFIXString());
//		return true;
//	}

	/**
	 * Creates QF Java sources files.
	 *
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 */
	public static void buildJavaSources(CharSequence qfStructureXMLFile, CharSequence javaSourcesOutputDir, String sourcesPackage, CharSequence qfVersion) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		if(sourcesPackage != null) {
			if(!sourcesPackage.endsWith(".")) {
				sourcesPackage = sourcesPackage + ".";
			}
			QFBuilder.sourcesPackage = sourcesPackage;
		}
		setQfStructureXML(qfStructureXMLFile);
		setJavaSourcesOutputDir(javaSourcesOutputDir, qfVersion);
		//Get the DOM Builder Factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		//Get the DOM Builder
		DocumentBuilder builder = factory.newDocumentBuilder();

		//Load and Parse the XML document
		Document document = builder.parse(qfStructureXML);

		XPath xpath = XPathFactory.newInstance().newXPath();
		processFields(document, xpath);
		processComponents(document, xpath);
		processMessages(document, xpath, qfVersion);
		processMessageInterfaces();

		if(classOutputDir != null) {
			compile();
		}
	}

	static String getSoucesPackage() {
		return String.valueOf(sourcesPackage);
	}

	private static void processMessageInterfaces() throws IOException {
		for(QFComponentClassBuilder.NameReq componentDef : allComponents) {
			String res = QFMessageInterfaceBuilder.buildInterfaceSource(componentDef);
			storeJavaSources("common", "I" + componentDef.getName(), res);
		}
	}

	private static void processMessages(Node document, XPath xpath, CharSequence qfVersion) throws XPathExpressionException, IOException {
		QFMessageMapperBuilder.clearMessageMapping();
		XPathExpression expr;
		NodeList nodes;
		expr = xpath.compile("/fix/messages/message");
		nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		for(int j = 0; j < nodes.getLength(); j++) {
			Node messageNode = nodes.item(j);
			if(messageNode instanceof Element) {
				QFComponentClassBuilder.ComponentTag message = processComponent(xpath, messageNode);
				String res = QFMessageClassBuilder.getInstance().generateClassFile(message);
				//System.out.println(res);
				storeJavaSources("message", message.getName() + "Message", res);
				QFMessageMapperBuilder.addMessageMapping(messageNode.getAttributes().getNamedItem("name").getNodeValue(), messageNode.getAttributes().getNamedItem("msgtype").getNodeValue());
			}
		}
		storeJavaSources("message", "QFMessage", QFMessageMapperBuilder.buildQFMessage(qfVersion));
		storeJavaSources("", "QFMessageMapper", QFMessageMapperBuilder.buildTypeToMessageMapping());
	}

	private static void processComponents(Node document, XPath xpath) throws XPathExpressionException, IOException {
		allComponents.clear();
		processComponents(document, xpath, "/fix/header");
		processComponents(document, xpath, "/fix/trailer");
		processComponents(document, xpath, "/fix/components/component");
	}

	private static void processComponents(Node document, XPath xpath, String expression) throws XPathExpressionException, IOException {
		XPathExpression expr;
		NodeList nodes;
		expr = xpath.compile(expression);
		nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		for(int j = 0; j < nodes.getLength(); j++) {
			Node componentNode = nodes.item(j);
			if(componentNode instanceof Element) {
				QFComponentClassBuilder.ComponentTag componentGroup = processComponent(xpath, componentNode);
				String res = QFComponentClassBuilder.getInstance().generateClassFile(componentGroup);
				storeJavaSources("component", componentGroup.getName() + "Component", res);
			}
		}
	}

	private static QFComponentClassBuilder.ComponentTag processComponent(XPath xpath, Node componentNode) throws XPathExpressionException, IOException {
		boolean isMessage = false;
		XPathExpression expr;
		QFComponentClassBuilder.ComponentTag componentGroup;
		// Look for first inner field of the group.
		if(componentNode.getNodeName().equals("group")) {
			QFComponentClassBuilder.NameReq firstGroupField = getFirstGroupField(componentNode, xpath);
			componentGroup = new QFGroupClassBuilder.GroupTag(componentNode, firstGroupField);
		} else {
			if(componentNode.getNodeName().equals("message")) {
				isMessage = true;
				String msgType = componentNode.getAttributes().getNamedItem("msgtype").getNodeValue();
				String msgCat = componentNode.getAttributes().getNamedItem("msgcat").getNodeValue();
				componentGroup = new QFMessageClassBuilder.MessageTag(componentNode, msgType, msgCat);
			} else {
				componentGroup = new QFComponentClassBuilder.ComponentTag(componentNode);
			}
		}

		// Go over field values.
		expr = xpath.compile("field");
		NodeList componentInnerNodes = (NodeList) expr.evaluate(componentNode, XPathConstants.NODESET);
		for(int i = 0; i < componentInnerNodes.getLength(); i++) {
			Node fieldInnerNode = componentInnerNodes.item(i);
			if(fieldInnerNode instanceof Element) {
				String fName = fieldInnerNode.getAttributes().getNamedItem("name").getNodeValue();
				if(fName.equals("Currency")) {
					fName = "CurrencyQF";
				}
				componentGroup.addField(new QFComponentClassBuilder.NameReq(
						fName,
						fieldInnerNode.getAttributes().getNamedItem("required").getNodeValue().charAt(0)
				));
			}
		}

		// Go over group values.
		expr = xpath.compile("group");
		componentInnerNodes = (NodeList) expr.evaluate(componentNode, XPathConstants.NODESET);
		for(int i = 0; i < componentInnerNodes.getLength(); i++) {
			Node groupInnerNode = componentInnerNodes.item(i);
			if(groupInnerNode instanceof Element) {
				componentGroup.addGroup(new QFComponentClassBuilder.NameReq(
						groupInnerNode.getAttributes().getNamedItem("name").getNodeValue(),
						groupInnerNode.getAttributes().getNamedItem("required").getNodeValue().charAt(0)
				));
				componentGroup.addComponentGroupTag(processComponent(xpath, groupInnerNode));
			}
		}
		// Go over component values.
		if(isMessage) { // Add message header.
			QFComponentClassBuilder.NameReq header = new QFComponentClassBuilder.NameReq("StandardHeader", 'Y');
			allComponents.add(header);
			componentGroup.addComponent(header);
		}
		expr = xpath.compile("component");
		componentInnerNodes = (NodeList) expr.evaluate(componentNode, XPathConstants.NODESET);
		for(int i = 0; i < componentInnerNodes.getLength(); i++) {
			Node componentInnerNode = componentInnerNodes.item(i);
			if(componentInnerNode instanceof Element) {
				// Could not access "componentInnerNode.getAttributes().getNamedItem("required").getNodeValue()" directly since
				// in some components this attribute is omitted.
				char required;
				Node req = componentInnerNode.getAttributes().getNamedItem("required");
				if(req == null) {
					required = 'N';
				} else {
					required = req.getNodeValue().charAt(0);
				}
				QFComponentClassBuilder.NameReq componentNameReq = new QFComponentClassBuilder.NameReq(
						componentInnerNode.getAttributes().getNamedItem("name").getNodeValue(),
						required
				);
				componentGroup.addComponent(componentNameReq);
				allComponents.add(componentNameReq);
			}
		}
		if(isMessage) { // Add message trailer.
			QFComponentClassBuilder.NameReq trailer = new QFComponentClassBuilder.NameReq("StandardTrailer", 'Y');
			allComponents.add(trailer);
			componentGroup.addComponent(trailer);
		}
		return componentGroup;
	}

	private static QFComponentClassBuilder.NameReq getFirstGroupField(Node groupNode, XPath xpath) throws XPathExpressionException {
		if(groupNode == null) {
			return null;
		}
		Element firstInnerNode = (Element) xpath.evaluate("*[1]", groupNode, XPathConstants.NODE);
		if(firstInnerNode != null) {
			QFComponentClassBuilder.NameReq res;
			String fName = firstInnerNode.getAttributes().getNamedItem("name").getNodeValue();
			if(firstInnerNode.getTagName().equals("field")) {
				if(fName.equals("Currency")) {
					fName = "CurrencyQF";
				}
				res = new QFComponentClassBuilder.NameReq(fName, firstInnerNode.getAttributes().getNamedItem("required").getNodeValue().charAt(0));
				return res;
			}
			if(firstInnerNode.getTagName().equals("component")) {
				Element componentInnerNode = (Element) xpath.evaluate("/fix/components/component[@name='" + fName + "']", groupNode, XPathConstants.NODE);
				res = getFirstGroupField(componentInnerNode, xpath);
				return res;
			}
			if(firstInnerNode.getTagName().equals("group")) {
				res = getFirstGroupField(firstInnerNode, xpath);
				return res;
			}
			return null;
		}
		return null;
	}

	private static void processFields(Node document, XPath xpath) throws XPathExpressionException, IOException {
		XPathExpression expr;
		NodeList nodes;
		expr = xpath.compile("/fix/fields/field");// //person/*//*text()
		nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		for(int j = 0; j < nodes.getLength(); j++) {
			Node fieldNode = nodes.item(j);
			if(fieldNode instanceof Element) {
				//<field number="4" name="AdvSide" type="CHAR">
				String fNumber = fieldNode.getAttributes().getNamedItem("number").getNodeValue();
				String fName = fieldNode.getAttributes().getNamedItem("name").getNodeValue();
				String fType = fieldNode.getAttributes().getNamedItem("type").getNodeValue();
				// Go over field values (optional).
				List<String[]> fEnumDescr = new ArrayList<>(3);
				NodeList fieldValueNodes = fieldNode.getChildNodes();
				for(int k = 0; k < fieldValueNodes.getLength(); k++) {
					Node fieldValueNode = fieldValueNodes.item(k);
					if(fieldValueNode instanceof Element) {
						//<value enum="B" description="BUY"/>
						fEnumDescr.add(new String[]{
								fieldValueNode.getAttributes().getNamedItem("enum").getNodeValue(),
								fieldValueNode.getAttributes().getNamedItem("description").getNodeValue()});
					}
				}
				if(fName.equals("Currency")) {
					fName = "CurrencyQF";
				}
				String res = QFFieldClassBuilder.generateClassFile(fNumber, fName, fType, fEnumDescr);
				//System.out.println(res);
				storeJavaSources("field", fName, res);
			}
		}
	}


	private static void storeJavaSources(String qfType, String fileSourceName, String sourceClassBody) throws IOException {
		File currentDir = new File(javaSourcesOutputDir, qfType);
		currentDir.mkdirs();
		File file = new File(currentDir, fileSourceName + ".java");
		Writer w = null;
		try {
			w = new FileWriter(file);
			w.write(sourceClassBody);
		} finally {
			if(w != null) {
				w.close();
			}
		}
	}

	//See more at: http://www.javabeat.net/the-java-6-0-compiler-api/#sthash.kb3qqndw.dpuf
	private static boolean compile() throws IOException {
		// Generated Java sources to compile.
		List<File> sourcesToCompile = new LinkedList<>();
		getFilesToCompile("field", sourcesToCompile);// Add "field" sources.
		getFilesToCompile("component", sourcesToCompile);// Add "component" sources.
		getFilesToCompile("common", sourcesToCompile);// Add "common" sources.
		getFilesToCompile("message", sourcesToCompile);// Add "message" sources.
		getFilesToCompile("", sourcesToCompile);// Add other root sources.

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourcesToCompile);
		// Set classes output directory.
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(classOutputDir));

		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
		boolean success = task.call();
		fileManager.close();
		return success;
	}

	private static void getFilesToCompile(String subDirName, List<File> toCompile) {
		File currentDir = new File(javaSourcesOutputDir, subDirName);
		File[] files = currentDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".java");
			}
		});
		toCompile.addAll(Arrays.asList(files));
	}


	public static void main(String[] args) {
		// Create Options object
		Options options = new Options();

		Option optQFStructureXMLFile = Option.builder("qfxml").required()
				.hasArg().argName("xml_file")
				.desc("QuickFix structure XML file. E.g. xml/FIX50SP2.xml").build();
		Option optJavaSourcesOutputDir = Option.builder("o").longOpt("sourcesout").required()
				.hasArg().argName("dir")
				.desc("Output directory for generated Java sources. E.g.  D:/HLSTools/TQuickFix/src/main/java").build();
		Option optPackage = Option.builder("p").longOpt("package").required()
				.hasArg().argName("package_name")
				.desc("Name of the package for genetared Java files. E.g. tqf.").build();
		Option optQFVersion = Option.builder("v").longOpt("version").required()
				.hasArg().argName("QFVersion")
				.desc("QuickFix version. E.g. v50sp2").build();

		Option optClassOutputDir = Option.builder("d").longOpt("classesout").required(false)
				.hasArg().argName("dir")
				.desc("Output directory for compiled classes (e.g. target/classes/). If omitted, then the generated Java sources won't be compiled.").build();
		Option optJarFile = Option.builder("j").longOpt("jarfile").required(false)
				.hasArg().argName("file")
				.desc("Name of Jar file that will contain newly compiled classes (e.g. target/qtf_v50sp2.jar). This option can be set only if \"classesout\" is set. If omitted, then no Jar file will be created.").build();

		// Add options
		options.addOption(optQFStructureXMLFile).addOption(optJavaSourcesOutputDir).addOption(optPackage).addOption(optQFVersion).addOption(optClassOutputDir).addOption(optJarFile);

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);

			CharSequence qfStructureXMLFile = cmd.getOptionValue(optQFStructureXMLFile.getOpt());
			CharSequence javaSourcesOutputDir = cmd.getOptionValue(optJavaSourcesOutputDir.getOpt());
			String pckg = cmd.getOptionValue(optPackage.getOpt());
			CharSequence qfVersion = cmd.getOptionValue(optQFVersion.getOpt());

			// Set classes output directory.
			String classOutDir = cmd.getOptionValue(optClassOutputDir.getOpt());
			if(classOutDir != null) {
				classOutputDir = new File(classOutDir);
				classOutputDir.mkdirs();
			}
			// Set output jar file.
			String outJar = cmd.getOptionValue(optJarFile.getOpt());
			if(outJar != null) {
				if(classOutputDir != null) {
					jarFile = new File(outJar);
				} else {
					throw new ParseException(optJarFile.getOpt() + " can be used only if \"" + optClassOutputDir.getOpt() +"\" is set");
				}
			}

			buildJavaSources(qfStructureXMLFile, javaSourcesOutputDir, pckg, qfVersion);
		} catch(ParseException e) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("QFBuilder", options);
			System.err.println(e.getMessage());
		} catch(ParserConfigurationException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(SAXException e) {
			e.printStackTrace();
		} catch(XPathExpressionException e) {
			e.printStackTrace();
		}
	}
}
