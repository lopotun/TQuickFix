package net.kem.tquickfix.builder;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 10/30/14
 * Time: 2:43 PM
 * Generates QF Group class file.
 */
public class QFGroupClassBuilder extends  QFComponentClassBuilder {

    public static class GroupTag extends ComponentTag {
        protected NameReq firstGroupField;
        public GroupTag(Node node, QFComponentClassBuilder.NameReq firstGroupField) {
            super(node);
            this.firstGroupField = firstGroupField;
        }
//        public void setFirstGroupField(NameReq firstGroupField) {
//            firstGroupField = firstGroupField;
//        }
    }

    private static final QFGroupClassBuilder INSTANCE = new QFGroupClassBuilder();
	public static QFGroupClassBuilder getInstance() {
		return INSTANCE;
	}

	public String generateClassFile(ComponentTag componentTag) {
		CharSequence classJdoc = generateClassJavaDoc(componentTag);
        CharSequence recognizedFieldsSection = generateRecognizedFieldsSection(componentTag);
		CharSequence fieldsDef = generateFieldsDef(componentTag);
		CharSequence componentsDef = generateComponentsDef(componentTag);
		CharSequence groupsDef = generateGroupsDef(componentTag);
		CharSequence methodParse = generateMethodParse("Group", componentTag);
		CharSequence ctor = generateConstructor(componentTag);
		CharSequence methodIsGroupCounter = generateMethodIsGroupCounter();
		CharSequence methodIsValid = generateMethodIsValid(componentTag);
		CharSequence methodToFIXString = generateMethodToFIXString(componentTag);
        CharSequence methodContainsField = generateMethodContainsField(componentTag);
		CharSequence innerClasses = generateInnerClasses(componentTag);
		String template = getTemplate(componentTag);
		String res = template
				.replace("$CLASSJDOC", classJdoc)
                .replace("$DEF_RECOGNIZED_FIELDS", recognizedFieldsSection)
				.replace("$FIELDSDEF", fieldsDef)
				.replace("$COMPONENTSDEF", componentsDef)
				.replace("$GROUPSDEF", groupsDef)
				.replace("$METHODPARSE", methodParse)
				.replace("$CTOR", ctor)
				.replace("$METHODISGROUPCOUNTER", methodIsGroupCounter)
				.replace("$METHODISVALID", methodIsValid)
				.replace("$METHODTOFIXSTRING", methodToFIXString)
                .replace("$METHODCONTAINSFIELD", methodContainsField)
				.replace("$INNERCLASSES", innerClasses);
		return res;
	}


//	protected static CharSequence generateImports(ComponentTag componentGroupTag) {
//		StringBuilder res = new StringBuilder(); // $IMPORT_FIELDS
//		res.append("import QFGroup;\n");
//		res.append("import net.kem.tquickfix.qf.field.").append(componentGroupTag.name).append(";\n");
//		for(QFComponentClassBuilder.NameReq field : componentGroupTag.fields) {
//			res.append("import net.kem.tquickfix.qf.field.").append(field.name).append(';').append("\n"); // import net.kem.tquickfix.qf.field.QFFieldFld03;
//		}
//        res.append("import net.kem.tquickfix.qf.field.").append(((GroupTag)componentGroupTag).firstGroupField.name).append(';').append("\n"); // import net.kem.tquickfix.qf.field.QFFieldFld03;
//
//		for(QFComponentClassBuilder.NameReq group : componentGroupTag.groups) {
//			res.append("import net.kem.tquickfix.qf.group.").append(group.name).append(';').append("\n"); // import net.kem.tquickfix.qf.field.QFFieldFld03;
//		}
//		for(QFComponentClassBuilder.NameReq component : componentGroupTag.components) {
//			res.append("import net.kem.tquickfix.qf.component.").append(component.name).append("Component;\n");
//		}
//		return res;
//	}

	protected CharSequence generateClassJavaDoc(ComponentTag componentGroupTag) {
		return "/** Group " + componentGroupTag.name + "\n*/";
	}

//	protected CharSequence generateFieldsDef() {
//		final String TEMPLATE_DEF_FIELD = "// $FCN\n" +
//				"\tprotected $FCN $FVN;\n" +
//				"\t$FCN get$FCN() {\n" +
//				"\t\treturn $FVN;\n" +
//				"\t}\n" +
//				"\tvoid set$FCN($FCN $FVN) {\n" +
//				"\t\tthis.$FVN = $FVN;\n" +
//				"\t}\n";
//		StringBuilder res = new StringBuilder(); // $DEF_FIELDS
//		if(!componentGroupTag.fields.isEmpty()) {
//			res.append("\t// ---- Fields begin ----\n");
//			for(NameReq field : componentGroupTag.fields) {
//				res.append(TEMPLATE_DEF_FIELD.replace("$FCN", field.name).replace("$FVN", WordUtils.uncapitalize(field.name)));
//			}
//			res.append("\t// ---- Fields end ----\n");
//		}
//		return res;
//	}

//	protected CharSequence generateComponentsDef() {
//		final String TEMPLATE_DEF_COMPONENTS =
//				"\t// $CCNComponent\n" +
//				"\tprotected $CCNComponent $CVN;\n" +
//				"\t$CCNComponent get$CCNComponent() {\n" +
//				"\t\treturn $CVN;\n" +
//				"\t}\n" +
//				"\tvoid set$CCNComponent($CCNComponent $CVN) {\n" +
//				"\t\tthis.$CVN = $CVN;\n" +
//				"\t}\n";
//		StringBuilder res = new StringBuilder(); // $DEF_COMPONENTS
//		if(!componentGroupTag.components.isEmpty()) {
//			res.append("\t// ---- Components begin ----\n");
//			for(NameReq component : componentGroupTag.components) {
//				res.append(TEMPLATE_DEF_COMPONENTS.replace("$CCN", component.name).replace("$CVN", WordUtils.uncapitalize(component.name)));
//			}
//			res.append("\t// ---- Components end ----\n");
//		}
//		return res;
//	}

//	protected CharSequence generateGroupsDef() {
//		final String TEMPLATE_DEF_GROUPS =
//				"\t// $GCN\n" +
//						"\tprotected List<$GCNGroup> $GVN = null;\n" +
//						"\tList<$GCNGroup> get$GCNGroup() {\n" +
//						"\t\treturn $GVN;\n" +
//						"\t}\n" +
//						"\tvoid set$GCNGroup(List<$GCNGroup> $GVN) {\n" +
//						"\t\tthis.$GVN = $GVN;\n" +
//						"\t}\n";
//		StringBuilder res = new StringBuilder(); // $DEF_GROUPS
//		if(!componentGroupTag.groups.isEmpty()) {
//			res.append("\t// ---- Groups begin ----\n");
//			for(NameReq group : componentGroupTag.groups) {
//				res.append(TEMPLATE_DEF_GROUPS.replace("$GCN", group.name).replace("$GVN", WordUtils.uncapitalize(group.name)));
//			}
//			res.append("\t// ---- Groups end ----\n");
//		}
//		return res;
//	}

	/*protected CharSequence generateMethodParse() {
		final String TEMPLATE_METHOD_PARSE = "\tpublic static $CLASS_NAMEGroup parse(List<String[]> rawData, MutableInt index) throws ParseException {\n" +
				"\t\t$CLASS_NAMEGroup component = new $CLASS_NAMEGroup();\n" +
				"\t\tboolean currentTagParsed, anyTagParsed = false;\n" +
				"\t\tdo {\n" +
				"\t\t\tcurrentTagParsed = false;\n" +
				"\t\t\tQFTag tag = new QFTag(rawData.get(index.intValue()));\n" +
				"\t\t\t$TEMPLATE_METHOD_PARSE_LOOK_IN_FIELDS\n" +

				"\t\t\tif(!currentTagParsed) {\n" +
				"\t\t\t$TEMPLATE_METHOD_PARSE_LOOK_IN_GROUPS\n" +
				"\t\t\t$TEMPLATE_METHOD_PARSE_LOOK_IN_COMPONENTS\n" +
				"\t\t\t} else {\n" +
				"\t\t\t\tindex.increment();\n" +
				"\t\t\t}" +

				"\n\t\t} while(currentTagParsed && index.intValue() < rawData.size()-1);\n" +
				"\t\treturn anyTagParsed? component: null;\n" +
				"\t}";

		final String TEMPLATE_METHOD_PARSE_LOOK_IN_FIELDS = "// Look in fields.\n" +
                "\t\t\tif(tag.getTagKey() == FIRST_MEMBER_NUMBER && component.$FIRST_MEMBER_NAME != null) {\n" +//hopCompID
                "\t\t\t\tbreak;\n" +
                "\t\t\t}" +
				"\t\t\tswitch(tag.getTagKey()) {\n" +
				"$TEMPLATE_METHOD_PARSE_LOOK_IN_FIELDS_CASE\n" +
				"\t\t\t}";

		final String TEMPLATE_METHOD_PARSE_LOOK_IN_COMPONENTS = "\t// Look in component.\n" +
				"\t\t\t\tif(!currentTagParsed) {\n" +
				"\t\t\t\t\t$CCNComponent $CVN = $CCNComponent.parse(rawData, index);\n" +
				"\t\t\t\t\tif($CVN != null) {\n" +
				"\t\t\t\t\t\tcomponent.$CVN = $CVN;\n" +
				"\t\t\t\t\t\tcurrentTagParsed = true;\n" +
				"\t\t\t\t\t\tanyTagParsed = true;\n" +
				"\t\t\t\t\t}\n" +
				"\t\t\t\t}";

		final String TEMPLATE_METHOD_PARSE_LOOK_IN_GROUPS = "\t// Look in groups.\n" +
				"\t\t\t\tif($GCNGroup.isGroupCounter(tag.getTagKey())) {\n" +
				"\t\t\t\t\tcomponent.$GVN = new ArrayList<>(Integer.parseInt(tag.getTagValue()));\n" +
				"\t\t\t\t\tcurrentTagParsed = true;\n" +
				"\t\t\t\t\tanyTagParsed = true;\n" +
				"\t\t\t\t\tindex.increment();\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tif(!currentTagParsed) {\n" +
				"\t\t\t\t\t$GCNGroup groupMember = $GCNGroup.parse(rawData, index);\n" +
				"\t\t\t\t\tif(groupMember != null) {\n" +

                "\t\t\t\t\t\tif(component.$GVN == null) {\n" +
                "\t\t\t\t\t\t\tthrow new ParseException(\"Group $GCN is declared without its group count (tag \" + $GCNGroup.GROUP_COUNT_NUMBER + \")\", index.intValue());\n" +
                "\t\t\t\t\t\t} else {\n" +
                "\t\t\t\t\t\t\tcomponent.$GVN.add(groupMember);\n" +
                "\t\t\t\t\t\t\tcurrentTagParsed = true;\n" +
                "\t\t\t\t\t\t\tanyTagParsed = true;\n" +
                "\t\t\t\t\t\t}\n" +

                "\t\t\t\t\t}\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\t\n";

		String methodParseLiF;
		if(!componentGroupTag.fields.isEmpty()) {
			StringBuilder sbMethodParseLookInFields = new StringBuilder(); // $TEMPLATE_METHOD_PARSE_LOOK_IN_FIELDS_CASE
			for(NameReq field : componentGroupTag.fields) {
				sbMethodParseLookInFields.append("\t\t\t\tcase $FCN.NUMBER: component.$FVN = new $FCN(tag.getTagValue(), validate); anyTagParsed = true; currentTagParsed = true; break;\n".replace("$FCN", field.name).replace("$FVN", WordUtils.uncapitalize(field.name)));
			}
			methodParseLiF = TEMPLATE_METHOD_PARSE_LOOK_IN_FIELDS
                    .replace("$FIRST_MEMBER_NAME", WordUtils.uncapitalize(componentGroupTag.fields.get(0).getName()))
                    .replace("$TEMPLATE_METHOD_PARSE_LOOK_IN_FIELDS_CASE", sbMethodParseLookInFields);
		} else {
			methodParseLiF = "\t//No fields.";
		}


		StringBuilder sbMethodParseLookInComponents = new StringBuilder(); // $TEMPLATE_METHOD_PARSE_LOOK_IN_COMPONENTS
		for(NameReq component : componentGroupTag.components) {
			sbMethodParseLookInComponents.append(TEMPLATE_METHOD_PARSE_LOOK_IN_COMPONENTS.replace("$CCN", component.name).replace("$CVN", WordUtils.uncapitalize(component.name)));
		}

        StringBuilder sbMethodParseLookInGroups = new StringBuilder(); // $TEMPLATE_METHOD_PARSE_LOOK_IN_GROUPS
        if(!componentGroupTag.groups.isEmpty()) {
			for(NameReq group : componentGroupTag.groups) {
				sbMethodParseLookInGroups.append(TEMPLATE_METHOD_PARSE_LOOK_IN_GROUPS.replace("$GCN", group.name).replace("$GVN", WordUtils.uncapitalize(group.name)));
			}
		} else {
            sbMethodParseLookInGroups.append("\t//No groups.");
        }

		String res = TEMPLATE_METHOD_PARSE.replace("$CLASS_NAME", componentGroupTag.name)
				.replace("$TEMPLATE_METHOD_PARSE_LOOK_IN_FIELDS", methodParseLiF)
				.replace("$TEMPLATE_METHOD_PARSE_LOOK_IN_GROUPS", sbMethodParseLookInGroups)
				.replace("$TEMPLATE_METHOD_PARSE_LOOK_IN_COMPONENTS", sbMethodParseLookInComponents);
		return res;
	}*/

	protected CharSequence generateMethodIsGroupCounter() {
		return "\tpublic static boolean isGroupCounter(int tagKey) {\n" +
				"\t\t\treturn GROUP_COUNT_NUMBER == tagKey;\n" +
				"\t}\n";
	}
	protected CharSequence generateConstructor(ComponentTag componentGroupTag) {
		return ("\tpublic $CNGroupBase(QFBuilderConfig config) {\n" +
				"\t\tsuper(NAME, GROUP_COUNT_NUMBER, FIRST_MEMBER_NUMBER, config);\n" +
				"\t}\n").replace("$CN", componentGroupTag.name);
	}

	protected String getTemplate(ComponentTag componentGroupTag) {
		String res =(
						"$CLASSJDOC\n" +
                        "@SuppressWarnings(\"unused\")\n" +
						"static abstract class $CNGroupBase extends QFGroup {\n" +
						"\tpublic static final String NAME = $CN.NAME;\n" +
						"\tpublic static final int GROUP_COUNT_NUMBER = $CN.NUMBER;\n" +
						"\tpublic static final int FIRST_MEMBER_NUMBER = $FIRST_FIELD.NUMBER;\n" +
						"//\tpublic static boolean validate = false;\n" +
                        "$DEF_RECOGNIZED_FIELDS\n" +
						"$FIELDSDEF\n" +
						"$COMPONENTSDEF\n" +
						"$GROUPSDEF\n" +
						"$METHODPARSE\n" +
						"$CTOR\n" +
						"$METHODISGROUPCOUNTER\n" +
						"$METHODISVALID\n" +
						"$METHODTOFIXSTRING\n" +
                        "$METHODCONTAINSFIELD\n" +
						"$INNERCLASSES\n" +
						"}\n\npublic static class $CNGroup extends $CNGroupBase {\n" +
                        "\tpublic $CNGroup(QFBuilderConfig config) {\n" +
                        "\t\tsuper(config);\n" +
                        "\t}\n" +
						"}").replace("$CN", componentGroupTag.name).replace("$FIRST_FIELD", ((GroupTag)componentGroupTag).firstGroupField.name);
		return res;  //To change body of created methods use File | Settings | File Templates.
	}
}