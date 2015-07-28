package net.kem.tquickfix.builder;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;
import net.kem.tquickfix.blocks.QFField;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Parsing configuration.
 * User: EvgenyK
 * Date: 12/23/14
 * Time: 8:35 AM
 */
@XStreamAlias("tqfparser")
public class QFBuilderConfig {
    private static final Map<CharSequence, QFBuilderConfig> BUILDER_CONFIGS = new HashMap<CharSequence, QFBuilderConfig>();
    private static QFBuilderConfig INSTANCE;

    @XStreamAsAttribute @SuppressWarnings("unused")
    private double version;

    @XStreamAlias("parse_validations")
    private Map<CharSequence, QFField.Validation> xmlParseValidations;
    @XStreamAlias("field_validations")
    private Map<CharSequence, QFField.Validation> xmlFieldValidations;

    @XStreamOmitField
    private Map<CharSequence, Map<CharSequence, QFField.Validation>> parseValidations;
    @XStreamOmitField
    private Map<CharSequence, Map<CharSequence, QFField.Validation>> fieldValidations;

    /**
     * Initializes <strong>default</strong> {@linkplain QFBuilderConfig} with the given configuration file. This method should be called before any use
     * of the {@linkplain #getDefaultInstance()} method.
     * The default configuration can be used by an application with single message processing rules.
     *
     * @param configXMLFileName path to configuration file. If <em>null</em>, then value of the "tqfParser.xml" system property
     *                       will be used to load the configuration.
     *                       If the "tqfParser.xml" system property is not set,
     *                       then the configuration will be looked in the "./tqfParser.xml" file.
     * @see #init(CharSequence, String)
     * @see #getDefaultInstance()
     * @see #getInstance(CharSequence)
     */
    public static void init(String configXMLFileName) {
        init(null, configXMLFileName);
    }

    /**
     * Initializes {@linkplain QFBuilderConfig} with the given configuration file. This method should be called before any use
     * of the {@linkplain #getInstance(CharSequence)} method.
     * Multiple configurations can be used by an application with different message processing rules.
     * For example, different message processing rules may be applied on different assets such like CFD, Equities, ETD etc.
     *
     * @param configXMLFileName path to configuration file. If <em>null</em>, then value of the "tqfParser.xml" system property
     *                          will be used to load the configuration.
     *                          If the "tqfParser.xml" system property is not set,
     *                          then the configuration will be looked in the "./tqfParser.xml" file.
     * @see #init(String)
     * @see #getDefaultInstance()
     * @see #getInstance(CharSequence)
     */
    public static void init(CharSequence key, String configXMLFileName) {
        XStream xstream = new XStream();
        xstream.processAnnotations(QFBuilderConfig.class);
        NamedMapConverter namedMapConverter = new NamedMapConverter(xstream.getMapper(), "field", "name", String.class, "value", QFField.Validation.class, true, true, xstream.getConverterLookup());
        xstream.registerConverter(namedMapConverter);

        QFBuilderConfig config = (QFBuilderConfig) xstream.fromXML(new File(configXMLFileName));

        config.parseValidations = new HashMap<CharSequence, Map<CharSequence, QFField.Validation>>();
        for(Map.Entry<CharSequence, QFField.Validation> parseValidationEntry : config.xmlParseValidations.entrySet()) {
            config.handleMapping(config.parseValidations, parseValidationEntry.getKey().toString(), parseValidationEntry.getValue());
        }

        config.fieldValidations = new HashMap<CharSequence, Map<CharSequence, QFField.Validation>>();
        for(Map.Entry<CharSequence, QFField.Validation> parseValidationEntry : config.xmlFieldValidations.entrySet()) {
            config.handleMapping(config.fieldValidations, parseValidationEntry.getKey().toString(), parseValidationEntry.getValue());
        }
        // Free up unnecessary fields.
        config.xmlParseValidations = null;
        config.xmlFieldValidations = null;

        // Set default configuration instance or map newly created configuration.
        if(key == null) {
            INSTANCE = config;
        } else {
            BUILDER_CONFIGS.put(key, config);
        }
    }

    public static QFBuilderConfig getDefaultInstance() {
        return INSTANCE;
    }

    public static QFBuilderConfig getInstance(CharSequence key) {
        return BUILDER_CONFIGS.get(key);
    }

    public Map<CharSequence, QFField.Validation> getParseValidations(CharSequence className) {
        return parseValidations.get(className);
    }

    public Map<CharSequence, QFField.Validation> getFieldValidations(CharSequence className) {
        return fieldValidations.get(className);
    }

    private void handleMapping(Map<CharSequence, Map<CharSequence, QFField.Validation>> mapping, String line, QFField.Validation validation) {
        // net.kem.tquickfix.qf.component.PartiesComponentBase.noPartyIDs (group)
        // net.kem.tquickfix.qf.component.PartiesComponentBase.NoPartyIDsGroupBase.partyID
        // net.kem.tquickfix.qf.component.StandardHeaderComponentBase.senderCompID
        // net.kem.tquickfix.qf.component.MDFullGrpComponentBase.NoMDEntriesGroupBase.rateSource (component)
        int lastDotPosition = line.lastIndexOf('.');
        String className = line.substring(0, lastDotPosition);
        String variableName = line.substring(lastDotPosition + 1);

        Map<CharSequence, QFField.Validation> affectedVars = mapping.get(className);
        if(affectedVars == null) {
            affectedVars = new HashMap<CharSequence, QFField.Validation>();
            mapping.put(className, affectedVars);
        }
        affectedVars.put(variableName, validation);
    }
//    public static void main(String[] args) {
//        String configXMLFileName = "D:\\HLSTools\\TQuickFix\\src\\main\\resources\\tqfParser.xml";
//        QFBuilderConfig.init(configXMLFileName);
//    }
}