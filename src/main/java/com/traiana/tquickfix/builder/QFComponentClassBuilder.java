package com.traiana.tquickfix.builder;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 10/30/14
 * Time: 2:43 PM
 * Generates QF Component class file.
 */
public class QFComponentClassBuilder {
//	protected ComponentTag componentGroupTag;

	private static final QFComponentClassBuilder INSTANCE = new QFComponentClassBuilder();

	public static class ComponentTag {
		protected String name;
        protected List<QFComponentClassBuilder.NameReq> fields = new LinkedList<>();
        protected List<QFComponentClassBuilder.NameReq> groups = new LinkedList<>();
        protected List<QFComponentClassBuilder.NameReq> components = new LinkedList<>();
        protected List<ComponentTag> groupTags = new LinkedList<>();

        protected enum TagType{FILED, COMPONENT, GROUP}
        protected List<Pair<TagType, NameReq>> allChildren = new LinkedList<>();

        public ComponentTag(Node node) {
			name =  node.getAttributes().getNamedItem("name").getNodeValue();
		}
		public void addField(NameReq nameReq) {
			fields.add(nameReq);
            allChildren.add(new ImmutablePair<>(TagType.FILED, nameReq));
		}
		public List<QFComponentClassBuilder.NameReq> getFields() {
			return fields;
		}
		public boolean hasFields() {
			return !fields.isEmpty();
		}

		public void addGroup(NameReq nameReq) {
			groups.add(nameReq);
            allChildren.add(new ImmutablePair<>(TagType.GROUP, nameReq));
		}
        public boolean hasGroups() {
            return !groups.isEmpty();
        }

		public void addComponent(NameReq nameReq) {
			components.add(nameReq);
            allChildren.add(new ImmutablePair<>(TagType.COMPONENT, nameReq));
		}
		public List<QFComponentClassBuilder.NameReq> getComponents() {
			return components;
		}
		public boolean hasComponents() {
			return !components.isEmpty();
		}

		public void addComponentGroupTag(ComponentTag groupTag) {
			groupTags.add(groupTag);
		}
		public String getName() {
			return name;
		}
	}

	public static class NameReq {
		protected String name;
		protected char required;
		public NameReq(String name, char required) {
			this.name = name;
			this.required = required;
		}

		public String getName() {
			return name;
		}
	}

	public static QFComponentClassBuilder getInstance() {
		return INSTANCE;
	}

	public String generateClassFile(ComponentTag componentGroupTag) {
//		this.componentGroupTag = componentGroupTag;

		CharSequence pckg = generatePackage();
		CharSequence imports = generateImports(componentGroupTag);
		CharSequence classJdoc = generateClassJavaDoc(componentGroupTag);
        CharSequence recognizedFieldsSection = generateRecognizedFieldsSection(componentGroupTag);
        CharSequence fieldsDef = generateFieldsDef(componentGroupTag);
		CharSequence componentsDef = generateComponentsDef(componentGroupTag);
		CharSequence groupsDef = generateGroupsDef(componentGroupTag);
		CharSequence methodParse = generateMethodParse("Component", componentGroupTag);
		CharSequence ctor = generateConstructor(componentGroupTag);
		CharSequence methodIsValid = generateMethodIsValid(componentGroupTag);
		CharSequence methodToFIXString = generateMethodToFIXString(componentGroupTag);
		CharSequence methodContainsField = generateMethodContainsField(componentGroupTag);
		CharSequence innerClasses = generateInnerClasses(componentGroupTag);
//		CharSequence mainClass = generateMainClass(name, fields, groups, components);
		String template = getTemplate(componentGroupTag);
		String res = template
				.replace("$PCKG", pckg)
				.replace("$IMPORTS", imports)
				.replace("$CLASSJDOC", classJdoc)
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
		return "package " + QFBuilder.getSoucesPackage() + QFBuilder.qfVersion + ".component;\n";
	}

	protected CharSequence generateImports(ComponentTag componentGroupTag) {
		StringBuilder res = new StringBuilder(); // $IMPORT_FIELDS

//        if(!componentGroupTag.groupTags.isEmpty()) {
//			res.append("// Groups-related imports begin\n");
//			for(ComponentTag groupTag : componentGroupTag.groupTags) {
//				CharSequence groupInnerClass = QFGroupClassBuilder.getInstance().generateImports(groupTag);
//				res.append(groupInnerClass);
//			}
//			res.append("// Groups-related imports end\n");
//		}

		res
//                .append("import com.traiana.tquickfix.QFParser;\n")
                .append("import com.traiana.tquickfix.ThreadContext;\n")
                .append("import com.traiana.tquickfix.blocks.QFComponent;\n")
                .append("import com.traiana.tquickfix.builder.QFBuilderConfig;\n")
                .append("import com.traiana.tquickfix.blocks.QFField;\n");
        if(componentGroupTag.groups != null) {
            res.append("import com.traiana.tquickfix.blocks.QFGroup;\n");
        }
        res.append("import com.traiana.tquickfix.blocks.QFTag;\n\n");
        if(componentGroupTag.hasComponents()) {
            res.append("import ").append(QFBuilder.getSoucesPackage()).append(QFBuilder.qfVersion).append(".component.*;\n");
        }
        res.append("import ").append(QFBuilder.getSoucesPackage()).append(QFBuilder.qfVersion).append(".field.*;\n");

//		for(NameReq field : componentGroupTag.fields) {
//			res.append("import com.traiana.tquickfix.qf.field.").append(field.name).append(';').append("\n"); // import com.traiana.tquickfix.qf.field.QFFieldFld03;
//		}
//		for(NameReq component : componentGroupTag.components) {
//			res.append("import com.traiana.tquickfix.qf.component.").append(component.name).append("Component;\n"); // import com.traiana.tquickfix.qf.field.QFFieldFld03;
//		}

		res.append("\nimport org.apache.commons.lang3.mutable.MutableInt;\n\n")
                .append("import java.util.ArrayList;\n")
                .append("import java.util.HashSet;\n")
                .append("import java.util.List;\n")
                .append("import java.util.Set;\n");

		return res;  //To change body of created methods use File | Settings | File Templates.
	}

	protected CharSequence generateClassJavaDoc(ComponentTag componentGroupTag) {
		return "/** Component " + componentGroupTag.name + "\n*/";
	}

	protected CharSequence generateFieldsDef(ComponentTag componentGroupTag) {
		final String TEMPLATE_DEF_FIELD = "\t// $FCN\n" +
				"\tprotected $FCN $FVN;\n" +
				"\tpublic $FCN get$FCN() {\n" +
				"\t\treturn $FVN;\n" +
				"\t}\n" +
				"\tpublic void set$FCN($FCN $FVN) {\n" +
				"\t\tthis.$FVN = $FVN;\n" +
				"\t}\n";
		StringBuilder res = new StringBuilder(); // $DEF_FIELDS
		if(componentGroupTag.hasFields()) {
			res.append("\t// ---- Fields begin ----\n");
			for(NameReq field : componentGroupTag.fields) {
				res.append(TEMPLATE_DEF_FIELD.replace("$FCN", field.name).replace("$FVN", WordUtils.uncapitalize(field.name)));
			}
			res.append("\t// ---- Fields end ----\n");
		}
		return res;
	}

    protected CharSequence generateRecognizedFieldsSection(ComponentTag componentGroupTag) {
        StringBuilder res = new StringBuilder(1024); // $DEF_RECOGNIZED_FIELDS
        res.append("\tprivate static final Set<Integer> RECOGNIZED_FIELDS = new HashSet<Integer>();\n");
		if(componentGroupTag.hasFields()) {
			res.append("\tstatic{\n");
			for(NameReq field : componentGroupTag.fields) {
				res.append("\t\tRECOGNIZED_FIELDS.add($FCN.NUMBER);\n".replace("$FCN", field.name));
			}
			res.append("\t}\n");
		}
		return res;
	}

	protected CharSequence generateComponentsDef(ComponentTag componentGroupTag) {
		final String TEMPLATE_DEF_COMPONENTS =
				"\t// $CCNComponent\n" +
				"\tprotected $CCNComponent $CVN;\n" +
				"\tpublic $CCNComponent get$CCNComponent() {\n" +
				"\t\treturn $CVN;\n" +
				"\t}\n" +
				"\tpublic void set$CCNComponent($CCNComponent $CVN) {\n" +
				"\t\tthis.$CVN = $CVN;\n" +
				"\t}\n";
		StringBuilder res = new StringBuilder(); // $DEF_COMPONENTS
		if(componentGroupTag.hasComponents()) {
			res.append("\t// ---- Components begin ----\n");
			for(NameReq component : componentGroupTag.components) {
				res.append(TEMPLATE_DEF_COMPONENTS.replace("$CCN", component.name).replace("$CVN", WordUtils.uncapitalize(component.name)));
			}
			res.append("\t// ---- Components end ----\n");
		}
		return res;
	}

	protected CharSequence generateGroupsDef(ComponentTag componentGroupTag) {
		final String TEMPLATE_DEF_GROUPS =
				"\t// $GCN\n" +
						"\tprotected List<$GCNGroup> $GVN = null;\n" +
						"\tpublic List<$GCNGroup> get$GCNGroup() {\n" +
						"\t\treturn $GVN;\n" +
						"\t}\n" +
						"\tpublic void set$GCNGroup(List<$GCNGroup> $GVN) {\n" +
						"\t\tthis.$GVN = $GVN;\n" +
						"\t}\n";
		StringBuilder res = new StringBuilder(); // $DEF_GROUPS
		if(componentGroupTag.hasGroups()) {
			res.append("\t// ---- Groups begin ----\n");
			for(NameReq group : componentGroupTag.groups) {
				res.append(TEMPLATE_DEF_GROUPS.replace("$GCN", group.name).replace("$GVN", WordUtils.uncapitalize(group.name)));
			}
			res.append("\t// ---- Groups end ----\n");
		}
		return res;
	}

    protected CharSequence generateMethodParse(CharSequence classSiffix, ComponentTag componentGroupTag) {
        final StringBuilder sb = new StringBuilder(16384);
        sb.append(("\tpublic static $CLASS_NAME$CLASS_SUFFIX parse(List<QFTag> rawData, MutableInt index, QFBuilderConfig config) {\n" +
                "\t\t$CLASS_NAME$CLASS_SUFFIX component = null;\n" +
                "\t\tboolean currentTagParsed;\n" +
                "\t\tdo {\n" +
                "\t\t\tcurrentTagParsed = false;\n" +
                "\t\t\tQFTag tag = rawData.get(index.intValue());\n" +
                "\n" +
                "\t\t\tContainResult cr = $CLASS_NAME$CLASS_SUFFIX.containsField(tag.getTagKey(), true);\n" +
                "\t\t\tswitch(cr) {\n").replace("$CLASS_NAME", componentGroupTag.name).replace("$CLASS_SUFFIX", classSiffix));

        sb.append("\t\t\t\tcase HAS_THIS:\n");
        if(componentGroupTag.hasFields()) {
            sb.append(("\t\t\t\t\tif(component == null) {\n" +
                    "\t\t\t\t\t\tcomponent = new $CLASS_NAME$CLASS_SUFFIX(config);\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t\t// Look in fields.\n").replace("$CLASS_NAME", componentGroupTag.name).replace("$CLASS_SUFFIX", classSiffix));

            if(this instanceof QFGroupClassBuilder) {
                sb.append(("\t\t\t\t\tif(tag.getTagKey() == FIRST_MEMBER_NUMBER && component.$FIRST_MEMBER_NAME != null) {\n" +
                        "\t\t\t\t\t\tbreak;\n" +
                        "\t\t\t\t\t}\n").replace("$CLASS_NAME", componentGroupTag.name).replace("$FIRST_MEMBER_NAME", WordUtils.uncapitalize(componentGroupTag.fields.get(0).getName())));
            }

            sb.append("\t\t\t\t\tQFField.Validation validation;\n");
            sb.append("\t\t\t\t\tswitch(tag.getTagKey()) {\n");
            for(NameReq field : componentGroupTag.fields) {
                sb.append("\t\t\t\t\t\tcase $FCN.NUMBER:\n".replace("$FCN", field.name).replace("$FVN", WordUtils.uncapitalize(field.name)));
//                sb.append("\t\t\t\t\t\t\tvalidation = component.forceParseValidation != null && component.forceParseValidation.contains(\"$FVN\")? QFField.Validation.FULL: QFField.Validation.NONE;\n".replace("$FCN", field.name).replace("$FVN", WordUtils.uncapitalize(field.name)));
                sb.append("\t\t\t\t\t\t\tvalidation = component.getParseFieldValidation(\"$FVN\");\n".replace("$FCN", field.name).replace("$FVN", WordUtils.uncapitalize(field.name)));
                sb.append("\t\t\t\t\t\t\tcomponent.$FVN = $FCN.getInstance(tag.getTagValue(), validation);\n".replace("$FCN", field.name).replace("$FVN", WordUtils.uncapitalize(field.name)));
                sb.append("\t\t\t\t\t\t\tbreak;\n");
            }
            sb.append("\t\t\t\t\t}\n");
            sb.append("\t\t\t\t\tcurrentTagParsed = true;\n");
            sb.append("\t\t\t\t\tindex.increment();\n");
        } else {
            sb.append("\t\t\t\t\t//No fields.\n");
        }
        sb.append("\t\t\t\t\tbreak;\n");


        sb.append("\t\t\t\tcase HAS_DESCENDANT:\n");
        if(componentGroupTag.hasGroups() || componentGroupTag.hasComponents()) {
            sb.append(("\t\t\t\t\tif(component == null) {\n" +
                    "\t\t\t\t\t\tcomponent = new $CLASS_NAME$CLASS_SUFFIX(config);\n" +
                    "\t\t\t\t\t}\n").replace("$CLASS_NAME", componentGroupTag.name).replace("$CLASS_SUFFIX", classSiffix));
            if(componentGroupTag.hasComponents()) {
                sb.append("\t\t\t\t\t// Look in components.\n");
                for(NameReq component : componentGroupTag.components) {
                    sb.append(("\t\t\t\t\tif(!currentTagParsed && component.$CVN == null) {\n" +
                            "\t\t\t\t\t\t$CCNComponent $CVN = $CCNComponent.parse(rawData, index, config);\n" +
                            "\t\t\t\t\t\tif($CVN != null) {\n" +
                            "\t\t\t\t\t\t\tcomponent.$CVN = $CVN;\n" +
                            "\t\t\t\t\t\t\tcurrentTagParsed = true;\n" +
                            "\t\t\t\t\t\t\tbreak;\n" +
                            "\t\t\t\t\t\t}\n" +
                            "\t\t\t\t\t}\n").replace("$CCN", component.name).replace("$CVN", WordUtils.uncapitalize(component.name)));
                }
            } else {
                sb.append("\t\t\t\t\t//No components.\n");
            }
            if(componentGroupTag.hasGroups()) {
                sb.append("\t\t\t\t\t// Look in groups.\n");
                for(NameReq group : componentGroupTag.groups) {
                    sb.append(("\t\t\t\t\tif(!currentTagParsed) {\n" +
                            "\t\t\t\t\t\tif($GCNGroup.isGroupCounter(tag.getTagKey())) {\n" +
                            "\t\t\t\t\t\t\tint numOfMembers;\n" +
                            "\t\t\t\t\t\t\ttry {\n" +
                            "\t\t\t\t\t\t\t\tnumOfMembers = Integer.parseInt(tag.getTagValue());\n" +
                            "\t\t\t\t\t\t\t} catch(NumberFormatException e) {\n" +
                            "\t\t\t\t\t\t\t\tnumOfMembers = 4;\n" +
                            "\t\t\t\t\t\t\t\taddError(ThreadContext.ErrorType.WARNING, \"Value of the group count tag \" + tag.toString() + \" [group $GCN(\" + $GCNGroup.GROUP_COUNT_NUMBER + \")] must be integer.\");\n" +
                            "\t\t\t\t\t\t\t}\n" +
                            "\t\t\t\t\t\t\tcomponent.$GVN = new ArrayList<$GCNGroup>(numOfMembers);\n" +
                            "\t\t\t\t\t\t\tcurrentTagParsed = true;\n" +
                            "\t\t\t\t\t\t\tindex.increment();\n" +
                            "\t\t\t\t\t\t}\n" +
                            "\t\t\t\t\t\tif(!currentTagParsed) {\n" +
                            "\t\t\t\t\t\t\t$GCNGroup groupMember = $GCNGroup.parse(rawData, index, config);\n" +
                            "\t\t\t\t\t\t\tif(groupMember != null) {\n" +
                            "\t\t\t\t\t\t\t\tif(component.$GVN == null) {\n" +
                            "\t\t\t\t\t\t\t\t\taddError(ThreadContext.ErrorType.PARSING, \"Error while parsing tag \" + tag.toString() + \": group $GCN(\" + $GCNGroup.GROUP_COUNT_NUMBER + \") that this tag belongs to is missing or its first member (tag \" + $GCNGroup.FIRST_MEMBER_NUMBER + \") is missing\");\n" +
                            "\t\t\t\t\t\t\t\t} else {\n" +
                            "\t\t\t\t\t\t\t\t\tcomponent.$GVN.add(groupMember);\n" +
                            "\t\t\t\t\t\t\t\t\tcurrentTagParsed = true;\n" +
                            "\t\t\t\t\t\t\t\t}\n" +
                            "\t\t\t\t\t\t\t}\n" +
                            "\t\t\t\t\t\t}\n").replace("$GCN", group.name).replace("$GVN", WordUtils.uncapitalize(group.name)));
                            sb.append("\t\t\t\t\t}\n");
                }
            } else {
                sb.append("\t\t\t\t\t//No groups.\n");
            }
        } else {
            sb.append("\t\t\t\t\t//No groups and components.\n");
        }
        sb.append("\t\t\t\t\tbreak;\n");

        sb.append("\t\t\t\tcase DOESNT_HAVE:\n");
        if(this instanceof QFMessageClassBuilder) {
            sb.append(("\t\t\t\t\tif(component == null) {\n" +
                    "\t\t\t\t\t\tcomponent = new $CLASS_NAME$CLASS_SUFFIX(config);\n" +
                    "\t\t\t\t\t}\n" +
//                    "\t\t\t\t\tStandardTrailerComponent trailer = StandardTrailerComponent.parse(rawData, index);\n" +
                    "\t\t\t\t\tif(component.standardTrailer == null) {\n" +
//                    "\t\t\t\t\t\tif(trailer != null) {\n" +
//                    "\t\t\t\t\t\t\tcomponent.standardTrailer = trailer;\n" +
//                    "\t\t\t\t\t\t} else {\n" +
                    "\t\t\t\t\t\tif(component.unrecognizedFields == null) {\n" +
                    "\t\t\t\t\t\t\tcomponent.unrecognizedFields = new ArrayList<QFTag>(5);\n" +
                    "\t\t\t\t\t\t}\n" +
                    "\t\t\t\t\t\tcomponent.unrecognizedFields.add(tag);\n" +
//                    "\t\t\t\t\t\t}\n" +
                    "\t\t\t\t\t\tindex.increment();\n" +
                    "\t\t\t\t\t} else {\n" +
                    "\t\t\t\t\t\tindex.decrement();\n" +
                    "\t\t\t\t\t\t\taddError(ThreadContext.ErrorType.WARNING, \"Extra tag \" + (rawData.get(index.intValue())) + \" was detected at index \" + index + \" after the finalizing tag \" + CheckSum.NAME + \"(\" + CheckSum.NUMBER + \"). This and all following tags will be ignored.\");\n" +
                    "\t\t\t\t\t\t\treturn component;\n" +
                    "\t\t\t\t\t\t}\n" +
                    "\t\t\t\t\tcurrentTagParsed = true;\n").replace("$CLASS_NAME", componentGroupTag.name).replace("$CLASS_SUFFIX", "Message"));
        } else {
            sb.append("\t\t\t\t\tcurrentTagParsed = false;\n");
        }
        sb.append("\t\t\t\t\tbreak;\n");

        sb.append(
                "\t\t\t}\n" +
                "\t\t} while(currentTagParsed && index.intValue() < rawData.size());\n" +
                "\t\treturn component;\n" +
                "\t}");
        return sb;
    }

	protected CharSequence generateConstructor(ComponentTag componentGroupTag) {
		return ("\tpublic $CNComponentBase(QFBuilderConfig config) {\n" +
				"\t\tsuper(NAME, config);\n" +
				"\t}\n").replace("$CN", componentGroupTag.name);
	}

	protected CharSequence generateMethodIsValid(ComponentTag componentGroupTag) {
		final String TEMPLATE_METHOD_ISVALID = "\tpublic boolean isValid(QFField.Validation validation) {\n" +
				"\t\tboolean res = true;\n" +
				"$TEMPLATE_METHOD_ISVALID_FIELDS" +
				"$TEMPLATE_METHOD_ISVALID_COMPONENTS" +
				"$TEMPLATE_METHOD_ISVALID_GROUPS" +
				"\t\treturn res;\n" +
				"\t}\n";

		StringBuilder sbMethodIsValidFields = new StringBuilder(); // $TEMPLATE_METHOD_ISVALID_FIELDS
		if(componentGroupTag.hasFields()) {
			sbMethodIsValidFields.append("\t\t// Fields");
			for(NameReq field : componentGroupTag.fields) {
//                if(field.required=='Y') {
//                    sbMethodIsValidFields.append("\n\t\tif(res) {\t// Required member\n");
//                    sbMethodIsValidFields.append(("\t\t\tif(suppressValidation == null || !suppressValidation.contains(\"$FVN\")) {\n" +
//                            "\t\t\t\tif($FVN != null) {\n" +
//                            "\t\t\t\t\tres = $FVN.isValid(validation);\n" +
//                            "\t\t\t\t} else {\n" +
//                            "\t\t\t\t\taddError(ThreadContext.ErrorType.MISSING_MANDATORY_VALUE, \"$FCN in \" + name);\n" +
//                            "\t\t\t\t\tres = false;\n" +
//                            "\t\t\t\t}\n" +
//                            "\t\t\t}").replace("$FCN", field.name).replace("$FVN", WordUtils.uncapitalize(field.name)));
//                } else {
//                    sbMethodIsValidFields.append("\n\t\tif(res) {\n\t\t\t")
//                            .append("res = $FVN==null? res: $FVN.isValid(validation);".replace("$FVN", WordUtils.uncapitalize(field.name)));
//                }
                if(field.required=='Y') {
                    sbMethodIsValidFields.append("\n\t\tif(res) {\t// Required member\n");
                    sbMethodIsValidFields.append(("\t\t\tQFField.Validation v = getFieldValidation(\"$FVN\", validation);\n" +
                            "\t\t\tif(v != QFField.Validation.NONE) {\n" +
                            "\t\t\t\tif($FVN == null) {\n" +
                            "\t\t\t\t\taddError(ThreadContext.ErrorType.MISSING_MANDATORY_VALUE, \"$FCN in \" + name);\n" +
                            "\t\t\t\t\tres = false;\n" +
                            "\t\t\t\t} else {\n" +
                            "\t\t\t\t\tres = $FVN.isValid(v);\n" +
                            "\t\t\t\t}\n" +
                            "\t\t\t}").replace("$FCN", field.name).replace("$FVN", WordUtils.uncapitalize(field.name)));
                } else {
                    sbMethodIsValidFields.append("\n\t\tif(res) {\n");
                    sbMethodIsValidFields.append(("\t\t\tif($FVN != null) {\n" +
                            "\t\t\t\tQFField.Validation v = getFieldValidation(\"$FVN\", validation);\n" +
                            "\t\t\t\tres = $FVN.isValid(v);\n" +
                            "\t\t\t}").replace("$FVN", WordUtils.uncapitalize(field.name)));
                }
                sbMethodIsValidFields.append("\n\t\t} else {\n" +
                        "\t\t\treturn res;\n" +
                        "\t\t}\n");
			}
		}

		StringBuilder sbMethodIsValidComponents = new StringBuilder(); // $TEMPLATE_METHOD_ISVALID_COMPONENTS
		if(componentGroupTag.hasComponents()) {
			sbMethodIsValidComponents.append("\t\t// Components");
			for(NameReq component : componentGroupTag.components) {
                if(component.required=='Y') {
                    sbMethodIsValidComponents.append("\n\t\tif(res) {\t// Required member\n");
//                    sbMethodIsValidComponents.append(("\t\t\tif(suppressValidation == null || !suppressValidation.contains(\"$CVN\")) {\n" +
//                            "\t\t\t\tif($CVN != null) {\n" +
//                            "\t\t\t\t\tres = $CVN.isValid(validation);\n" +
//                            "\t\t\t\t} else {\n" +
//                            "\t\t\t\t\taddError(ThreadContext.ErrorType.MISSING_MANDATORY_VALUE, \"$CCN in \" + name);\n" +
//                            "\t\t\t\t\tres = false;\n" +
//                            "\t\t\t\t}\n" +
//                            "\t\t\t}").replace("$CCN", component.name).replace("$CVN", WordUtils.uncapitalize(component.name)));
                    sbMethodIsValidComponents.append(("\t\t\tQFField.Validation v = getFieldValidation(\"$CVN\", validation);\n" +
                            "\t\t\tif(v != QFField.Validation.NONE) {\n" +
                            "\t\t\t\tif($CVN == null) {\n" +
                            "\t\t\t\t\taddError(ThreadContext.ErrorType.MISSING_MANDATORY_VALUE, \"$CCN in \" + name);\n" +
                            "\t\t\t\t\tres = false;\n" +
                            "\t\t\t\t} else {\n" +
                            "\t\t\t\t\tres = $CVN.isValid(v);\n" +
                            "\t\t\t\t}\n" +
                            "\t\t\t}").replace("$CCN", component.name).replace("$CVN", WordUtils.uncapitalize(component.name)));

                } else {
//                    sbMethodIsValidComponents.append("\n\t\tif(res) {\n\t\t\t")
//                            .append("res = $CVN==null? res: $CVN.isValid(validation);".replace("$CVN", WordUtils.uncapitalize(component.name)));
                    sbMethodIsValidComponents.append(("\n\t\tif(res) {\n" +
                            "\t\t\tif($CVN != null) {\n" +
                            "\t\t\t\tQFField.Validation v = getFieldValidation(\"$CVN\", validation);\n" +
                            "\t\t\t\tres = $CVN.isValid(v);\n" +
                            "\t\t\t}").replace("$CVN", WordUtils.uncapitalize(component.name)));
                }
                sbMethodIsValidComponents.append("\n\t\t} else {\n" +
                        "\t\t\treturn res;\n" +
                        "\t\t}\n");
			}
		}

		StringBuilder sbMethodIsValidGroups = new StringBuilder(); // $TEMPLATE_METHOD_ISVALID_GROUPS
		if(componentGroupTag.hasGroups()) {
			sbMethodIsValidGroups.append("\t\t// Groups\n").append("\t\tif(res) {\n");
			for(NameReq group : componentGroupTag.groups) {
//                if(group.required=='Y') {
//                    sbMethodIsValidGroups.append("\t\t\tif(suppressValidation == null || !suppressValidation.contains(\"$GVN\")) {// Required member\n".replace("$GVN", WordUtils.uncapitalize(group.name)));
//                }
//
//                if(group.required=='Y') {
//                    sbMethodIsValidGroups.append("\t\t\t\tif($GVN != null && !$GVN.isEmpty()) {\t\n".replace("$GVN", WordUtils.uncapitalize(group.name)));
//                } else {
//                    sbMethodIsValidGroups.append("\t\t\tif($GVN != null) {\n".replace("$GVN", WordUtils.uncapitalize(group.name)));
//                }
//
//                sbMethodIsValidGroups.append(("\t\t\t\tfor($GCNGroup grp : $GVN) {\n" +
//                        "\t\t\t\t\tif(!grp.isValid(validation)) {\n" +
//                        "\t\t\t\t\t\tres = false;\n" +
//                        "\t\t\t\t\t\tbreak;\n" +
//                        "\t\t\t\t\t}\n" +
//                        "\t\t\t\t}\n" +
//                        "\t\t\t} else {\n" +
//                        "\t\t\t\tres = false;\n" +
//                        "\t\t\t}\n").replace("$GCN", group.name).replace("$GVN", WordUtils.uncapitalize(group.name)));
//
//                if(group.required=='Y') {
//                    sbMethodIsValidGroups.append("\t\t\t}\n");
//                }

                if(group.required=='Y') {
                    sbMethodIsValidGroups.append("\t\t\tif(res) {// Required member\n");
                    sbMethodIsValidGroups.append(("\t\t\t\tQFField.Validation v = getFieldValidation(\"$GVN\", validation);\n" +
                            "\t\t\t\tif(v != QFField.Validation.NONE) {\n" +
                            "\t\t\t\t\tif($GVN == null) {\n" +
                            "\t\t\t\t\t\taddError(ThreadContext.ErrorType.MISSING_MANDATORY_VALUE, \"$GCN in \" + name);\n" +
                            "\t\t\t\t\t\tres = false;\n" +
                            "\t\t\t\t\t} else {\n").replace("$GCN", group.name).replace("$GVN", WordUtils.uncapitalize(group.name)));
                    // Insert common here
                } else {
                    sbMethodIsValidGroups.append("\t\t\tif(res) {\n");
                    sbMethodIsValidGroups.append(("\t\t\t\tif($GVN != null) {\n" +
                            "\t\t\t\t\tQFField.Validation v = getFieldValidation(\"$GVN\", validation);\n" +
                            "\t\t\t\t\tif(v != QFField.Validation.NONE) {\n").replace("$GVN", WordUtils.uncapitalize(group.name)));
                }
                sbMethodIsValidGroups.append(("\t\t\t\t\t\t// Common begin\n" +
                        "\t\t\t\t\t\tfor($GCNGroup grp : $GVN) {\n" +
                        "\t\t\t\t\t\t\tif(!grp.isValid(validation)) {\n" +
                        "\t\t\t\t\t\t\t\tres = false;\n" +
                        "\t\t\t\t\t\t\t\tbreak;\n" +
                        "\t\t\t\t\t\t\t}\n" +
                        "\t\t\t\t\t\t}\n").replace("$GCN", group.name).replace("$GVN", WordUtils.uncapitalize(group.name)));
                sbMethodIsValidGroups.append("\t\t\t\t\t}\n\t\t\t\t}\n\t\t\t} else {\n\t\t\t\treturn res;\n\t\t\t}\n\t\t\t// Common end\n");
			}
			sbMethodIsValidGroups.append("\t\t}\n");
		}

		String res = TEMPLATE_METHOD_ISVALID
				.replaceFirst("\\$TEMPLATE_METHOD_ISVALID_FIELDS", sbMethodIsValidFields.toString())
				.replaceFirst("\\$TEMPLATE_METHOD_ISVALID_COMPONENTS", sbMethodIsValidComponents.toString())
				.replaceFirst("\\$TEMPLATE_METHOD_ISVALID_GROUPS", sbMethodIsValidGroups.toString());
		return res;
	}

    protected CharSequence generateMethodToFIXStringBody(ComponentTag componentGroupTag) {
        StringBuilder res = new StringBuilder(16384);
        for(Pair<ComponentTag.TagType, NameReq> child : componentGroupTag.allChildren) {
            switch(child.getKey()) {
                case COMPONENT:
                    if(!child.getValue().getName().equals("StandardHeader") && !child.getValue().getName().equals("StandardTrailer")) {
                        res.append("\t\tif($CVN !=null) {$CVN.toFIXString(sb);}\n".replace("$CVN", WordUtils.uncapitalize(child.getValue().getName())));
                    }
                    break;
                case GROUP:
                    res.append(("\t\tif($GVN != null) {\n" +
                            "\t\t\tsb.append($GCNGroup.GROUP_COUNT_NUMBER).append('=').append($GVN.size()).append('\\u0001');\n" +
                            "\t\t\tfor($GCNGroup grp: $GVN) {\n" +
                            "\t\t\t\tgrp.toFIXString(sb);\n" +
                            "\t\t\t}\n" +
                            "\t\t}\n").replace("$GCN", child.getValue().getName()).replace("$GVN", WordUtils.uncapitalize(child.getValue().getName())));
                    break;
                case FILED:
                    res.append("\t\tif($FVN != null) {$FVN.toFIXString(sb);}\n".replace("$FVN", WordUtils.uncapitalize(child.getValue().getName())));
                    break;
            }
        }
//        res.append("\t\treturn sb.toString();\n").append("\t\t}");
        return res;
    }

	protected CharSequence generateMethodToFIXString(ComponentTag componentGroupTag) {
        StringBuilder res = new StringBuilder(16384);
        res.append("\tpublic String toFIXString() {\n")
                .append("\t\tStringBuilder sb = new StringBuilder(2048);\n")
                .append("\t\ttoFIXString(sb);\n")
                .append("\t\treturn sb.toString();\n")
                .append("\t}\n\n");

        res.append("\tpublic void toFIXString(StringBuilder sb) {\n");
        res.append(generateMethodToFIXStringBody(componentGroupTag));
        res.append("\t}");
        return res;
        /*final String TEMPLATE_METHOD_TOFIXSTRING = "public String toFIXString() {\n" +
				"\t\tStringBuilder sb = new StringBuilder(128);\n" +
				"\t\t// Groups\n" +
				"\t\t$METHOD_TOFIXSTRING_GROUPS\n" +
				"\t\t// Components\n" +
				"\t\t$METHOD_TOFIXSTRING_COMPONENTS\n" +
				"\t\t// Fields\n" +
				"\t\t$METHOD_TOFIXSTRING_FIELDS\n" +
				"\t\treturn sb.toString();\n" +
				"\t\t}";

		StringBuilder sbMethodToFIXStringGroups = new StringBuilder(); // $METHOD_TOFIXSTRING_GROUPS
		for(NameReq group : componentGroupTag.groups) {
			sbMethodToFIXStringGroups.append(("if($GVN != null) {\n" +
					"\t\t\tsb.append($GCNGroup.GROUP_COUNT_NUMBER).append('=').append($GVN.size()).append('\\\u0001');\n" +
					"\t\t\tfor($GCNGroup grp: $GVN) {\n" +
					"\t\t\t\tsb.append(grp.toFIXString());\n" +
					"\t\t\t}\n" +
					"\t\t}\n").replace("$GCN", group.name).replace("$GVN", WordUtils.uncapitalize(group.name)));
		}

		StringBuilder sbMethodToFIXStringComponents = new StringBuilder(); // $METHOD_TOFIXSTRING_COMPONENTS
		for(NameReq component : componentGroupTag.components) {
			sbMethodToFIXStringComponents.append("\t\tsb.append($CVN==null? \"\": $CVN.toFIXString());\n".replace("$CVN", WordUtils.uncapitalize(component.name)));
		}

		StringBuilder sbMethodToFIXStringFields = new StringBuilder(); // $METHOD_TOFIXSTRING_FIELDS
		for(NameReq field : componentGroupTag.fields) {
			sbMethodToFIXStringFields.append("\t\tsb.append($FVN==null? \"\": $FVN.toFIXString());\n".replace("$FVN", WordUtils.uncapitalize(field.name)));
		}
		String res = TEMPLATE_METHOD_TOFIXSTRING
				.replaceFirst("\\$METHOD_TOFIXSTRING_GROUPS", sbMethodToFIXStringGroups.toString())
				.replaceFirst("\\$METHOD_TOFIXSTRING_COMPONENTS", sbMethodToFIXStringComponents.toString())
				.replaceFirst("\\$METHOD_TOFIXSTRING_FIELDS", sbMethodToFIXStringFields.toString());
		return res;*/
	}

    protected CharSequence generateMethodContainsField(ComponentTag componentGroupTag) {
        /*
        public static ContainResult containsField(int tag, boolean inDeep) {
        if(RECOGNIZED_FIELDS.contains(tag)) {
            return ContainResult.HAS_THIS;
        }
        if(!inDeep) {
            return ContainResult.DOESNT_HAVE;
        }
        // res = false, inDeep = true
        ContainResult res = HopGrpComponent.containsField(tag, inDeep);
        if(res == ContainResult.HAS_THIS) {
            res = ContainResult.HAS_DESCENDANT;
        }
        return res;
    }
         */
        StringBuilder res = new StringBuilder(16384);
        res.append("\n\tpublic static ContainResult containsField(int tag, boolean inDeep) {\n")
                .append("\t\tif(RECOGNIZED_FIELDS.contains(tag)) {\n")
                .append("\t\t\treturn ContainResult.HAS_THIS;\n")
                .append("\t\t}\n")
                .append("\t\tif(!inDeep) {\n")
                .append("\t\t\treturn ContainResult.DOESNT_HAVE;\n")
                .append("\t\t}\n")
                .append("\t\t// res = false, inDeep = true");

        res.append("\n\t\tContainResult res = ContainResult.DOESNT_HAVE;\n");
        if(componentGroupTag.hasGroups()) {
            for(NameReq group : componentGroupTag.groups) {
                String s1 = ("\n\t\tif($GCNGroup.GROUP_COUNT_NUMBER == tag) {\n" +
                        "\t\t\tres = ContainResult.HAS_DESCENDANT;\n" +
                        "\t\t\treturn res;\n" +
                        "\t\t}").replace("$GCN", group.name);
                String s2 = ("\n\t\tres = $GCNGroup.containsField(tag, inDeep);\n" +
                        "\t\tif(res != ContainResult.DOESNT_HAVE) {\n" +
                        "\t\t\tres = ContainResult.HAS_DESCENDANT;\n" +
                        "\t\t\treturn res;\n" +
                        "\t\t}").replace("$GCN", group.name);
                res.append(s1).append(s2);
            }
        }

        if(componentGroupTag.hasComponents()) {
            for(NameReq component : componentGroupTag.components) {
                String s = ("\n\t\tres = $CCNComponent.containsField(tag, inDeep);\n" +
                        "\t\tif(res != ContainResult.DOESNT_HAVE) {\n" +
                        "\t\t\tres = ContainResult.HAS_DESCENDANT;\n" +
                        "\t\t\treturn res;\n" +
                        "\t\t}").replace("$CCN", component.name);
                res.append(s);
            }
        }

        res.append("\n\t\treturn res;\n\t}\n");
        return res;
	}

	protected CharSequence generateInnerClasses(ComponentTag componentGroupTag) {
		StringBuilder res = new StringBuilder();
		for(ComponentTag groupTag : componentGroupTag.groupTags) {
			String groupInnerClass = QFGroupClassBuilder.getInstance().generateClassFile(groupTag);
			res.append(groupInnerClass);
		}
		return res;
	}

	protected String getTemplate(ComponentTag componentGroupTag) {
		String res =(
				"$PCKG\n" +
                "$IMPORTS\n\n" +
                "$CLASSJDOC\n" +

                "@SuppressWarnings(\"unused\")\n" +
                "class $CNComponentBase extends QFComponent {\n" +
                "\tpublic static final String NAME = \"$CN\";\n" +
                "//\tpublic static boolean validate = false;\n" +

                "$DEF_RECOGNIZED_FIELDS\n" +
                "$FIELDSDEF\n" +
                "$COMPONENTSDEF\n" +
                "$GROUPSDEF\n" +
                "$METHODPARSE\n" +
                "$CTOR\n" +
                "$METHODISVALID\n" +
                "$METHODTOFIXSTRING\n" +
                "$METHODCONTAINSFIELD\n" +
                "$INNERCLASSES\n" +
//				"$MAINCLASS" +
                "}\n\npublic class $CNComponent extends $CNComponentBase {\n" +
                "\tpublic $CNComponent(QFBuilderConfig config) {\n" +
                "\t\tsuper(config);\n" +
                "\t}\n" +
                "}").replace("$CN", componentGroupTag.name);
		return res;  //To change body of created methods use File | Settings | File Templates.
	}
}