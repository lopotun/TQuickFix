package net.kem.newtquickfix.builders;

import net.kem.newtquickfix.blocks.QFMember;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Evgeny Kurtser on 11/5/2015 at 3:43 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFMemberBrick implements QFBrick {

    protected QFMember.Type type;
    protected String name;
    protected boolean required;
    protected List<QFBrick> members;
    protected StringBuilder sb;

    protected QFMemberBrick(Element startElement, StringBuilder sb) throws IllegalArgumentException {
        this.type = QFMember.Type.valueOf(startElement.getTagName().toUpperCase());
        String attribute;
        attribute = startElement.getAttribute("name");
        this.name = attribute;
        attribute = startElement.getAttribute("required");
        this.required = attribute != null && attribute.equalsIgnoreCase("Y");
        this.sb = sb;

        NodeList values = startElement.getChildNodes();
        if (values != null && values.getLength() > 0) {
            members = new LinkedList<>();
            for (int j = 0; j < values.getLength(); j++) {
                Node value = values.item(j);
                if (value instanceof Element) {
                    QFBrick member = BuilderUtils.getQFMemberBrick((Element) value, sb);
                    if (member != null) {
                        members.add(member);
                    }
                }
            }
        }
    }

    @Override
    public QFMember.Type getFIXType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public void toJavaSource() {
//				public class FieldIntegerExample extends QFField<Integer> {
        getClassTitle();

//				"    public static final int TAG = 1497;\n" +
        getMembers();

//				"    private FieldIntegerExample(Integer value) {\n" +
//				"        this.value = value;\n" +
//				"    }\n" +
        getConstructor(container);

//				"    @Override\n" +
//				"    public int getTag() {\n" +
//				"        return TAG;\n" +
//				"    }\n" +
        getMethodGetTag(container);

//				"    public static FieldIntegerExample getInstance(String value) {\n" +
//				"        return getInstance(Integer.parseInt(value));\n" +
//				"    }\n" +
        getMethodGetInstanceString(container);

//				"    public static FieldIntegerExample getInstance(Integer value) {\n" +
//				"        FieldIntegerExample res = STATIC_VALUES_MAPPING.get(value);\n" +
//				"        if (res == null) {\n" +
//				"            res = new FieldIntegerExample(value);\n" +
//				"        }\n" +
//				"        return res;\n" +
//				"    }\n" +
        getMethodGetInstanceType(container);
        container.sb.append('}'); // end of class
        return container;
    }

    protected void getMethodGetInstanceType(Container container) {
        /*
		public static FieldIntegerExample getInstance(Integer value) {
			FieldIntegerExample res = STATIC_VALUES_MAPPING.get(value);
			if (res == null) {
				res = new FieldIntegerExample(value);
			}
			return res;
			// OR
			return new FieldIntegerExample(value);
		}
        */
        container.sb.append("\tpublic static ").append(container.name).append(" getInstance(").append(typeClass.getSimpleName()).append(" value) {\n");
        if (container.defaultValues != null) {
            container.sb.append("\t\t").append(container.name).append(" res = STATIC_VALUES_MAPPING.get(value);\n")
                    .append("\t\tif (res == null) {\n").append("\t\t\tres = new ").append(container.name).append("(value);\n")
                    .append("\t\t}\n")
                    .append("\t\treturn res;\n");
        } else {
            container.sb.append("\t\treturn new ").append(container.name).append("(value);\n");
        }
        container.sb.append("\t}\n");
    }

    protected void getMethodGetInstanceString(Container container) {
		/*
		public static FieldIntegerExample getInstance(String value) {
			return getInstance(Integer.parseInt(value));
		};
        */
        if (typeToStringConversion != null) {
            container.sb.append("\tpublic static ").append(container.name).append(" getInstance(String value) {\n")
                    .append("\t\treturn getInstance(")
                    .append(typeToStringConversion)//"Integer.parseInt(value)"
                    .append(");\n\t}\n\n");
        }
    }

    private void getMethodGetTag(Container container) {
		/*
		@Override
		public int getTag() {
			return TAG;
		};*/
        container.sb.append("\t@Override\n").append("\tpublic int getTag() {\n").append("\t\treturn TAG;\n").append("\t}\n\n");
    }

    protected void getConstructor(Container container) {
		/*
		private FieldIntegerExample(Integer value) {
			this.value = value;
		}
		 */
        container.sb.append("\tprivate ").append(container.name).append('(').append(typeClass.getSimpleName()).append(" value) {\n")
                .append("\t\tthis.value = value;\n")
                .append("\t}\n\n");
    }

    protected void getClassTitle() {
        sb.append("public class ").append(name).append(" extends QFComponent {\n");
    }

    protected void getCreditsSection() {
        LocalDateTime now = LocalDateTime.now();
        sb.append("/**\n")
                .append(" * Eugene Kurtzer\n")
                .append(" * Date: ").append(now.format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n")
                .append(" * Time: ").append(now.format(DateTimeFormatter.ISO_TIME)).append("\n")
                .append(" * <a href=mailto:Lopotun@gmail.com>Eugene Kurtzer</a>\n")
                .append(" */\n");
    }

    /*
		private GroupA() {}
		 */
    protected void getConstructor() {
        sb.append("\tprivate ").append(name).append("() {}\n\n");
    }

    /*
        public static GroupA getInstance(Stack<QFField> tags, GroupA instance) {
            return getInstance(tags, instance, GroupA.class);
        }
        */
    protected void getMethodGetInstance() {
        sb.append("\tpublic static ").append(name).append(" getInstance(Stack<QFField> tags, ").append(name).append(" instance) {\n")
                .append("\t\treturn getInstance(tags, instance, ").append(name).append(".class);\n\t}\n\n");
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
        for (QFBrick member : members) {
            String memberClassName = member.getName();
            String memberVarName = StringUtils.uncapitalize(memberClassName);
            member.getMemberAnnotation();//sb.append("\t@QFMember(type = ").append(member.getFIXType().name()).append(")\n")
            sb.append("\tprivate ").append(memberClassName).append(' ').append(memberVarName).append(";\n")

            .append("\tpublic ").append(memberClassName).append(" get").append(memberClassName).append("() {\n")
            .append("\t\treturn ").append(memberVarName).append(";\n\t}\n")

            .append("\tpublic void set").append(memberClassName).append('(').append(memberClassName).append(' ').append(memberVarName).append(") {\n")
            .append("\t\tthis.").append(memberVarName).append(" = ").append(memberVarName).append(";\n\t}\n\n");
        }
    }

    public static QFMemberBrick getNewQFFieldBrick(Element startElement) throws ClassNotFoundException {
        String attribute = startElement.getAttribute("type");
        return BuilderUtils.getJavaSourceFieldBuildingBrick(attribute);
    }
}