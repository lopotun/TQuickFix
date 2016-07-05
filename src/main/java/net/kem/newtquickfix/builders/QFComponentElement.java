package net.kem.newtquickfix.builders;

import net.kem.newtquickfix.blocks.QFMember;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFComponentElement extends QFElement {
    protected List<QFRequirable> members;

    public QFComponentElement(Element startElement, StringBuilder sb, CharSequence ident) throws IllegalArgumentException {
        super(startElement, sb, ident);
        NodeList values = startElement.getChildNodes();
        if (values != null && values.getLength() > 0) {
            members = new LinkedList<>();
            for (int j = 0; j < values.getLength(); j++) {
                Node value = values.item(j);
                if (value instanceof Element) {
                    QFRequirable member = BuilderUtils.getQFRequirable((Element) value, sb, ident, this);
                    if (member != null) {
                        members.add(member);
                    }
                }
            }
        }
    }

    @Override
    public void toJavaSource() {
        getPackageSection();
        getImportSection();
        generateCreditsSection();
        getClassTitle();
        getMembers();
        getValidateMethods();
        getCustomMethods();
        getConstructor();
        getMethodGetInstance();
        getMethodToFIXString();
        sb.append('}'); // end of class
    }

    public String getFirstFiledName() {
        QFRequirable firstMember = members.get(0);
        if (firstMember.getTagType() == QFMember.Type.FIELD) {
            return BuilderUtils.PACKAGE_NAME_FIELDS + "." + firstMember.getName();
        }
        CharSequence fieldName = BuilderUtils.COMPONENTS_FIRST_FIELD.get(firstMember.name);
        return BuilderUtils.PACKAGE_NAME_FIELDS + "." + fieldName.toString();
    }

    protected void getPackageSection() {
        sb.append("package ").append(BuilderUtils.PACKAGE_NAME_COMPONENTS).append(";\n\n");
    }

    protected void getImportSection() {
        super.getImportSection();
        for (QFRequirable member : members) {
	        if(!shouldIncludeMember(member)) {
		        continue;
	        }
            // In some cases (e.g. in net.kem.newtquickfix.components.RateSource) we have class member (of type QFField)
            // that has the same name as Component class. In this case we have to use full-specified name for this class member.
            if (member.getName().equals(name)) {
                member.useFQDN(true);
            } else {
                member.getImportSectionPart(sb);
            }
        }
        getImportSectionFromSubElements();
        sb.append('\n');
    }

    protected void getImportSectionFromSubElements() {
        sb.append("import ").append(BuilderUtils.PACKAGE_NAME_BLOCKS).append("QFComponent;\n");
    }

    /*
        @SuppressWarnings("unused")
        public class ComponentMain extends QFComponent {
    */
    protected void getClassTitle() {
        sb.append("\t@SuppressWarnings(\"unused\")\n")
                .append("public class ").append(name).append(" extends QFComponent {\n");
    }

    /*
    @QFMember(type = QFMember.Type.FIELD)
    private FieldStringGroupDelimiter fieldStringGroupDelimiter;
    public FieldStringGroupDelimiter getFieldStringGroupDelimiter() {
        return fieldStringGroupDelimiter;
    }
    public void setFieldStringGroupDelimiter(FieldStringGroupDelimiter fieldStringGroupDelimiter) {
        this.fieldStringGroupDelimiter = fieldStringGroupDelimiter;
    }
     */
    protected void getMembers() {
        for (QFRequirable member : members) {
	        if(!shouldIncludeMember(member)) {
		        continue;
	        }
            member.ident = ident;
            member.toJavaSource();
        }
    }

    protected void getValidateMethods() {
        getValidateMethodNoParams();
        // @Override
        // public boolean validate(QFComponentValidator componentValidator) {
        //    Boolean valid = componentValidator.validateComponent(this);
        //    if(valid != null) {
        //      return valid;
        //    }
        //    if(allocReportID == null) {
        //        AllocReportID.getValidationErrorsHandler().invalidValue(OrderMassActionRequest.class, "ClOrdID[+ " + ClOrdID.TAG + "]", null, ValidationErrorsHandler.ErrorType.MISSING);
        //    } else {
        //      allocReportID.validate(componentValidator);
        //    }
        // }
        sb.append(ident).append("\t@Override\n").append(ident).append("\tpublic boolean validate(QFComponentValidator componentValidator) {\n");
        preValidate();

        for (QFRequirable member : members) {
            // Some components should not be included in validation here.
            // For example, in Messages both "StandardHeader" and "StandardTrailer" are validated in its superclass AMessage.
            if(!shouldIncludeMember(member)) {
                continue;
            }
            switch (member.type) {
                case FIELD: { // AllocReportID.getValidationErrorsHandler().invalidValue(OrderMassActionRequest.class, "ClOrdID[+ " + ClOrdID.TAG + "]", null);
                    if (member.isRequired()) {
                        sb.append(ident).append("\t\tif(").append(StringUtils.uncapitalize(member.name)).append(" == null) {\n")
                                .append(ident).append("\t\t\tvalid &= componentValidator.mandatoryElementMissing(this, ").append(member.name).append(".class);\n")
                                .append(ident).append("\t\t}\n");
                    }
                }
                break;
                case GROUP:
                case COMPONENT:
                case HEADER:
                case TRAILER: { // componentValidator.mandatoryElementMissing(this, NoMDEntries.class);
                    if (member.isRequired()) {
                        sb.append(ident).append("\t\tif(").append(StringUtils.uncapitalize(member.name)).append(" == null) {\n")
                                .append(ident).append("\t\t\tvalid &= componentValidator.mandatoryElementMissing(this, ").append(member.name).append(".class);\n")
                                .append(ident).append("\t\t}\n");
                    }
//                    else {
                        // if(standardTrailer != null) {
                        //    standardTrailer.validate(componentValidator);
                        // }
                        //
                        // if(standardTrailer != null) {
                        //    	for(NoLegSecurityAltID groupMemeber: noLegSecurityAltID) {
                        //          groupMemeber.validate(componentValidator);
                        //      }
                        // }
                        sb.append(ident).append("\t\tif(").append(StringUtils.uncapitalize(member.name)).append(" != null) {\n");
                        if (member.getTagType() == QFMember.Type.GROUP) {
	                        //noSides.forEach((g) -> g.validate(componentValidator));
	                        //valid &= noSides.validate(componentValidator);
                            sb.append(ident).append("\t\t\t").append("for(").append(member.name).append(" groupMember: ").append(StringUtils.uncapitalize(member.name)).append(") {\n");
                            sb.append(ident).append("\t\t\t\tvalid &= groupMember.validate(componentValidator);\n");
                            sb.append(ident).append("\t\t\t}\n");
                        } else {
                            sb.append(ident).append("\t\t\tvalid &= ").append(StringUtils.uncapitalize(member.name)).append(".validate(componentValidator);\n");
                        }
                        sb.append(ident).append("\t\t}\n");
//                    }
                }
                break;
            }
        }
        sb.append(ident).append("\t\treturn valid;\n");
        sb.append(ident).append("\t}\n");
    }

    protected void getValidateMethodNoParams() {
        // @Override
        // public boolean validate() {
        //    return validate(getComponentValidator());
        // }
        sb.append(ident).append("\t@Override\n").append(ident).append("\tpublic boolean validate() {\n")
                .append(ident).append("\t\treturn validate(getComponentValidator());\n")
                .append(ident).append("\t}\n");
    }

    protected void preValidate() {
        // Boolean valid = componentValidator.validateComponent(this);
        // if(valid != null) {
        //     return valid;
        // }
        // valid = true;
        sb.append(ident).append("\t\tBoolean valid = componentValidator.validateComponent(this);\n")
                .append(ident).append("\t\tif(valid != null) {\n")
                .append(ident).append("\t\t\treturn valid;\n")
                .append(ident).append("\t\t}\n");
        sb.append(ident).append("\t\tvalid = true;\n");
    }

    protected boolean shouldIncludeMember(QFRequirable member) {
        return true;
    }

    protected void getCustomMethods() {
    }

    protected void getConstructor() {
        sb.append(ident).append("\tprivate ").append(name).append("() {}\n\n");
    }


    protected void getMethodGetInstance() {
        // public static ComponentMain getInstance() {
        //  return new ComponentMain();
        // }
        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance() {\n")
                .append(ident).append("\t\treturn new ").append(name).append("();\n")
                .append(ident).append("\t}\n\n");

        // public static ComponentMain getInstance(CharSequence fixVersion, Stack<QFField> tags, QFComponentValidator componentValidator) {
        //  return tags==null? new ComponentMain(): getInstance(fixVersion, tags, null, ComponentMain.class, QFComponentValidator componentValidator);
        // }
        sb.append(ident).append("\tpublic static ").append(name).append(" getInstance(CharSequence fixVersion, Stack<QFField> tags, QFComponentValidator componentValidator").append(") {\n")
                .append(ident).append("\t\treturn tags==null? new ").append(name).append("(): getInstance(fixVersion, tags, null, ").append(name).append(".class, componentValidator);\n")
                .append(ident).append("\t}\n\n");
    }

    /*
    @Override
    public void toFIXString(StringBuilder sb) {
        if(fieldStringGroupDelimiter != null) {
            fieldStringGroupDelimiter.toFIXString(sb);
        }
        if(fieldStringA != null) {
            fieldStringA.toFIXString(sb);
        }
        if(componentC != null) {
            componentC.toFIXString(sb);
        }
        if(fieldIntegerA != null) {
            fieldIntegerA.toFIXString(sb);
        }
    }
    */
    protected void getMethodToFIXString() {
        sb.append(ident).append("\t@Override\n")
                .append(ident).append("\tpublic void toFIXString(StringBuilder sb) {\n");
        for (QFRequirable member : members) {
            hookMethodToFIXStringMemberPre(member);
            String memberClassName = member.getName();
            String memberVarName = StringUtils.uncapitalize(memberClassName);
            sb.append(ident).append("\t\tif(").append(memberVarName).append(" != null) {\n");
            member.getMethodToFIXStringPart(sb);
            sb.append(ident).append("\t\t}\n");
        }
        sb.append(ident).append("\t}\n");
    }

    protected void hookMethodToFIXStringMemberPre(QFRequirable member) {
    }
}