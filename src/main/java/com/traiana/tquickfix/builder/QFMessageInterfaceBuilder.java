package com.traiana.tquickfix.builder;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 10/30/14
 * Time: 2:43 PM
 * Generates QF Component interface file.
 */
public class QFMessageInterfaceBuilder {
    private static final String TEMPLATE = "package " + QFBuilder.getSoucesPackage() + "$VER.common;\n" +
            "\n" +
            "import " + QFBuilder.getSoucesPackage() + "$VER.component.$CCNComponent;\n" +
            "\n" +
            "/**\n" +
            " * Autogenerated interface.\n" +
            " */\n" +
            "public interface I$CCN {\n" +
            "\t$CCNComponent get$CCNComponent();\n" +
            "\tvoid set$CCNComponent($CCNComponent instrument);\n" +
            "}";

    public static String buildInterfaceSource(QFComponentClassBuilder.NameReq componentDef) {
        return TEMPLATE.replace("$VER", QFBuilder.qfVersion).replace("$CCN", componentDef.getName());
    }
}