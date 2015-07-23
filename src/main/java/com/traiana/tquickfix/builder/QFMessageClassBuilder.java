package com.traiana.tquickfix.builder;

import org.w3c.dom.Node;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 10/30/14
 * Time: 2:43 PM
 * Generates QF Message class file.
 */
public class QFMessageClassBuilder extends QFComponentClassBuilder {

    private static final QFMessageClassBuilder INSTANCE = new QFMessageClassBuilder();

    public static QFMessageClassBuilder getInstance() {
        return INSTANCE;
    }

    public String generateClassFile(ComponentTag componentTag) {
        CharSequence pckg = generatePackage();
        CharSequence imports = generateImports(componentTag);
        CharSequence classJdoc = generateClassJavaDoc(componentTag);
        CharSequence implementsSection = generateImplementsSection(componentTag);
        CharSequence recognizedFieldsSection = generateRecognizedFieldsSection(componentTag);
        CharSequence fieldsDef = generateFieldsDef(componentTag);
        CharSequence componentsDef = generateComponentsDef(componentTag);
        CharSequence groupsDef = generateGroupsDef(componentTag);
        CharSequence methodParse = generateMethodParse("Message", componentTag);
        CharSequence ctor = generateConstructor(componentTag);
        CharSequence methodIsValid = generateMethodIsValid(componentTag);
        CharSequence methodToFIXString = generateMethodToFIXString(componentTag);
        CharSequence methodContainsField = generateMethodContainsField(componentTag);
        CharSequence innerClasses = generateInnerClasses(componentTag);
        String template = getTemplate(componentTag);
        String res = template
                .replace("$PCKG", pckg)
                .replace("$IMPORTS", imports)
                .replace("$CLASSJDOC", classJdoc)
                .replace("$IMPLEMENTS", implementsSection)
                .replace("$DEF_RECOGNIZED_FIELDS", recognizedFieldsSection)
                .replace("$FIELDSDEF", fieldsDef)
                .replace("$COMPONENTSDEF", componentsDef)
                .replace("$GROUPSDEF", groupsDef)
                .replace("$METHODPARSE", methodParse)
                .replace("$CTOR", ctor)
                .replace("$METHODISVALID", methodIsValid)
                .replace("$METHODTOFIXSTRING", methodToFIXString)
                .replace("$METHODCONTAINSFIELD", methodContainsField)
                .replace("$INNERCLASSES", innerClasses);
        return res;
    }

    protected CharSequence generatePackage() {
        return "package com.traiana.tquickfix.qf." + QFBuilder.qfVersion + ".message;";
    }

    protected CharSequence generateImports(ComponentTag componentGroupTag) {
        StringBuilder res = new StringBuilder();
        res
                .append("import com.traiana.tquickfix.ThreadContext;\n")
                .append("import com.traiana.tquickfix.blocks.QFField;\n")
                .append(componentGroupTag.groupTags.isEmpty()? "": "import com.traiana.tquickfix.blocks.QFGroup;\n")
//                .append("import com.traiana.tquickfix.blocks.QFMessage;\n")
                .append("import com.traiana.tquickfix.blocks.QFTag;\n")
                .append("import com.traiana.tquickfix.builder.QFBuilderConfig;\n")
                .append("import com.traiana.tquickfix.qf." + QFBuilder.qfVersion + ".field.*;\n")
                .append("import com.traiana.tquickfix.qf." + QFBuilder.qfVersion + ".common.*;\n")
                .append("import com.traiana.tquickfix.qf." + QFBuilder.qfVersion + ".component.*;\n\n");

        res.append("\nimport org.apache.commons.lang3.mutable.MutableInt;\n\n")
                .append("import java.util.ArrayList;\n")
                .append("import java.util.HashSet;\n")
                .append("import java.util.List;\n")
                .append("import java.util.Set;\n");
        return res;  //To change body of created methods use File | Settings | File Templates.
    }

    protected CharSequence generateClassJavaDoc(ComponentTag componentGroupTag) {
        return "/** Message " + componentGroupTag.name + "\n*/";
    }

    protected CharSequence generateImplementsSection(ComponentTag componentGroupTag) {
        CharSequence res = "";
        if(componentGroupTag.hasComponents()) {
            removeHeaderTrailer(componentGroupTag);
            if(componentGroupTag.hasComponents()) {
                StringBuilder sb = new StringBuilder(1024);
                sb.append("implements ");
                for(NameReq component : componentGroupTag.components) {
                    sb.append("I").append(component.name).append(", ");
                }
                sb.setLength(sb.length() - 2); // Get rid of the last ", ".
                sb.append(" ");
                res = sb.toString();
            }
            restoreHeaderTrailer(componentGroupTag);
        }
        return res;
    }

    protected CharSequence generateComponentsDef(ComponentTag componentGroupTag) {
        removeHeaderTrailer(componentGroupTag);
        CharSequence res = super.generateComponentsDef(componentGroupTag);
        restoreHeaderTrailer(componentGroupTag);
        return res;
    }

    private QFComponentClassBuilder.NameReq header;
    private QFComponentClassBuilder.NameReq trailer;
    private void removeHeaderTrailer(ComponentTag componentGroupTag) {
        removeHeader(componentGroupTag);
        removeTrailer(componentGroupTag);
    }
    private void removeHeader(ComponentTag componentGroupTag) {
        header = ((LinkedList<QFComponentClassBuilder.NameReq>)componentGroupTag.components).removeFirst();
    }
    private void removeTrailer(ComponentTag componentGroupTag) {
        trailer = ((LinkedList<QFComponentClassBuilder.NameReq>)componentGroupTag.components).removeLast();
    }
    private void restoreHeaderTrailer(ComponentTag componentGroupTag) {
        restoreHeader(componentGroupTag);
        restoreTrailer(componentGroupTag);
    }
    private void restoreHeader(ComponentTag componentGroupTag) {
        ((LinkedList<QFComponentClassBuilder.NameReq>)componentGroupTag.components).addFirst(header);
    }
    private void restoreTrailer(ComponentTag componentGroupTag) {
        ((LinkedList<QFComponentClassBuilder.NameReq>)componentGroupTag.components).addLast(trailer);
    }

    protected CharSequence generateConstructor(ComponentTag componentGroupTag) {
        return ("\tpublic $CNMessageBase(QFBuilderConfig config) {\n" +
                "\t\tsuper(NAME, TYPE, config);\n" +
                "\t}\n\n" +
                "\tpublic $CNMessageBase(boolean createHeaderTrailer, QFBuilderConfig config) {\n" +
                "\t\tsuper(NAME, TYPE, createHeaderTrailer, config);\n" +
                "\t}").replace("$CN", componentGroupTag.name);
    }

    protected CharSequence generateMethodToFIXString(ComponentTag componentGroupTag) {
        StringBuilder res = new StringBuilder(16384);
        res.append("\tpublic String toFIXString() {\n").append("\t\tStringBuilder sb = new StringBuilder(2048);\n");
        res.append("\t\tif(standardHeader != null) {standardHeader.toFIXString(sb);} // Message Standard Header\n");
        res.append(generateMethodToFIXStringBody(componentGroupTag));
        res.append("\t\t// Unrecognized fields (if any)\n" +
                "\t\tif(unrecognizedFields != null) {\n" +
                "\t\t\tfor(QFTag unrecognizedField : unrecognizedFields) {\n" +
                "\t\t\t\tsb.append(unrecognizedField.toFIXString());\n" +
                "\t\t\t}\n" +
                "\t\t}\n");
        res.append("\t\tif(standardTrailer != null) {standardTrailer.toFIXString(sb);} // Message Standard Trailer\n");
        res.append("\t\treturn sb.toString();\n").append("\t\t}");
        return res;
    }

    protected CharSequence generateMethodContainsField(ComponentTag componentGroupTag) {
//        ((LinkedList<QFComponentClassBuilder.NameReq>)componentGroupTag.components).addFirst(new QFComponentClassBuilder.NameReq("StandardHeader", 'Y'));
//        ((LinkedList<QFComponentClassBuilder.NameReq>)componentGroupTag.components).addLast(new QFComponentClassBuilder.NameReq("StandardTrailer", 'Y'));
//
        CharSequence res = super.generateMethodContainsField(componentGroupTag);
//
//        ((LinkedList<QFComponentClassBuilder.NameReq>)componentGroupTag.components).removeFirst();
//        ((LinkedList<QFComponentClassBuilder.NameReq>)componentGroupTag.components).removeLast();
        return res;
    }

    protected String getTemplate(ComponentTag componentGroupTag) {
        String res = (
                "$PCKG\n\n" +
                        "$IMPORTS\n\n" +
                        "$CLASSJDOC\n" +

                        "@SuppressWarnings(\"unused\")\n" +
                        "abstract class $CNMessageBase extends QFMessage $IMPLEMENTS{\n" +
                        "\tpublic static final String NAME = \"$CN\";\n" +
                        "\tpublic static final String TYPE = \"$CTYPE\";\n" +
                        "\tpublic static final String CATEGORY = \"$CCAT\";\n" +
                        "\tpublic static boolean validate = false;\n" +
                        "$DEF_RECOGNIZED_FIELDS\n\n" +
                        "$FIELDSDEF\n\n" +
                        "$COMPONENTSDEF\n\n" +
                        "$GROUPSDEF\n\n" +
                        "$METHODPARSE\n\n" +
                        "$CTOR\n\n" +
                        "$METHODISVALID\n\n" +
                        "$METHODTOFIXSTRING\n\n" +
                        "$METHODCONTAINSFIELD\n\n" +
                        "$INNERCLASSES\n" +
                        "}\n\npublic class $CNMessage extends $CNMessageBase {\n" +
                        "\tpublic $CNMessage(QFBuilderConfig config) {\n" +
                        "\t\tsuper(config);\n" +
                        "\t}\n" +
                        "\n" +
                        "\tpublic $CNMessage(boolean createHeaderTrailer, QFBuilderConfig config) {\n" +
                        "\t\tsuper(createHeaderTrailer, config);\n" +
                        "\t}" +
                        "\n" +
                        "}").replace("$CN", componentGroupTag.name).replace("$CTYPE", ((MessageTag) componentGroupTag).msgType).replace("$CCAT", ((MessageTag) componentGroupTag).msgCat);
        return res;  //To change body of created methods use File | Settings | File Templates.
    }

    public static class MessageTag extends ComponentTag {
        protected String msgType;
        protected String msgCat;

        public MessageTag(Node node, String msgType, String msgCat) {
            super(node);
            this.msgType = msgType;
            this.msgCat = msgCat;
        }
    }
}