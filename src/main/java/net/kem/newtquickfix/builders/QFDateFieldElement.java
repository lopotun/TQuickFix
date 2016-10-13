package net.kem.newtquickfix.builders;

import org.w3c.dom.Element;

/**
 * Created by Evgeny Kurtser on 11/9/2015 at 12:27 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class QFDateFieldElement extends QFFieldElement {
    public QFDateFieldElement(Element startElement, BuilderUtils.QFFieldBlockDef def) {
        super(startElement, def);
    }

    @Override
    protected void generateClassTitle() {
        sb.append("public class ").append(name).append(" extends ")
                .append(def.parentClassName).append(" {\n");
    }

    /*
    public static OrigTime of(String value, QFComponentValidator componentValidator) {
        try {
        return of(LocalDateTime.parse(value, getDateFormat()), LiteFixMessageParser.getComponentValidator());
        } catch (Exception e) {
            final java.time.LocalDateTime newValue = componentValidator.invalidFieldValue(MDEntryDate.class, LocalDate.class, value, e);

            TradeDate res = of(newValue, componentValidator);
			res.originalValue = value;
			return res;

            return of(newValue);
        }
	}
     */
    @Override
    protected void generateMethodGetInstanceString() {
        if (def.typeToStringConversion != null) {
            sb.append("\tpublic static ").append(name).append(" of(String value) {\n")
                    .append("\t\treturn of(value, LiteFixMessageParser.getComponentValidator());\n")
                    .append("\t}\n\n");

            sb.append("\tpublic static ").append(name).append(" of(String value, QFComponentValidator componentValidator) {\n")
                    .append("\t\ttry {\n")
                    .append("\t\t\treturn of(").append(def.typeClass.getSimpleName()).append(".parse(value, getDateFormat()), componentValidator);\n")
                    .append("\t\t} catch (Exception e) {\n")
                    .append("\t\t\tfinal ").append(def.typeClass.getSimpleName())
                    .append(" newValue = componentValidator.invalidFieldValue(")
                    .append(name).append(".class, ").append(def.typeClass.getSimpleName()).append(".class, value, e);\n")
                    .append("\t\t\t").append(name).append(" res = of(newValue, componentValidator);\n")
                    .append("\t\t\tres.originalValue = value;\n")
                    .append("\t\t\treturn res;\n")
                    .append("\t\t}\n")
                    .append("\t}\n\n");
        }
    }

    @Override
    protected void generateMethodGetInstanceType() {
        super.generateMethodGetInstanceType();
        //public static SendingTime of() {
        //    return new SendingTime(LocalDateTime.now());
        //}
        sb.append("\n")
        .append("\tpublic static ").append(name).append(" of() {\n")
        .append("\t\treturn new ").append(name).append("(").append(def.typeClass.getSimpleName()).append(".now());\n")
        .append("\t}\n\n");
    }
}