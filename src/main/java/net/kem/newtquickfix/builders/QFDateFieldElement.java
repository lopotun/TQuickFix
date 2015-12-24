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
    public static OrigTime getInstance(String value) {
        try {
        return getInstance(LocalDateTime.parse(value, getDateFormat()));
        } catch (Exception e) {
            final java.time.LocalDateTime newValue = validationHandler.invalidValue(OrigTime.class, value, e);
            return getInstance(newValue);
        }
	}
     */
    @Override
    protected void generateMethodGetInstanceString() {
        if (def.typeToStringConversion != null) {
            sb.append("\tpublic static ").append(name).append(" getInstance(String value) {\n")
                    .append("\t\ttry {\n")
                    .append("\t\treturn getInstance(").append(def.typeClass.getSimpleName()).append(".parse(value, getDateFormat()));\n")
                    .append("\t\t} catch (Exception e) {\n")
                    .append("\t\t\tfinal ").append(def.typeClass.getName()).append(" newValue = validationHandler.invalidValue(").append(name).append(".class, value, e);\n")
                    .append("\t\t\treturn getInstance(newValue);\n")
                    .append("\t\t}\n")
                    .append("\t}\n\n");
        }
    }

    @Override
    protected void generateMethodGetInstanceType() {
        super.generateMethodGetInstanceType();
        //public static SendingTime getInstance() {
        //    return new SendingTime(LocalDateTime.now());
        //}
        sb.append("\n")
        .append("\tpublic static ").append(name).append(" getInstance() {\n")
        .append("\t\treturn new ").append(name).append("(").append(def.typeClass.getSimpleName()).append(".now());\n")
        .append("\t}\n");
    }
}