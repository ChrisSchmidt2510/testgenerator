//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.11.09 um 07:31:43 PM CET 
//


package org.testgen.logging.config.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.testgen.logging.config.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Configuration_QNAME = new QName("http://www.testgenerator.org/logging", "Configuration");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.testgen.logging.config.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Configuration }
     * 
     */
    public Configuration createConfiguration() {
        return new Configuration();
    }

    /**
     * Create an instance of {@link AppendersType }
     * 
     */
    public AppendersType createAppendersType() {
        return new AppendersType();
    }

    /**
     * Create an instance of {@link LoggersType }
     * 
     */
    public LoggersType createLoggersType() {
        return new LoggersType();
    }

    /**
     * Create an instance of {@link RollingFileAppender }
     * 
     */
    public RollingFileAppender createRollingFileAppender() {
        return new RollingFileAppender();
    }

    /**
     * Create an instance of {@link ConsoleAppender }
     * 
     */
    public ConsoleAppender createConsoleAppender() {
        return new ConsoleAppender();
    }

    /**
     * Create an instance of {@link SizeBasedTriggeringPolicyType }
     * 
     */
    public SizeBasedTriggeringPolicyType createSizeBasedTriggeringPolicyType() {
        return new SizeBasedTriggeringPolicyType();
    }

    /**
     * Create an instance of {@link BaseLoggerType }
     * 
     */
    public BaseLoggerType createBaseLoggerType() {
        return new BaseLoggerType();
    }

    /**
     * Create an instance of {@link LoggerType }
     * 
     */
    public LoggerType createLoggerType() {
        return new LoggerType();
    }

    /**
     * Create an instance of {@link RootLoggerType }
     * 
     */
    public RootLoggerType createRootLoggerType() {
        return new RootLoggerType();
    }

    /**
     * Create an instance of {@link AppenderRefType }
     * 
     */
    public AppenderRefType createAppenderRefType() {
        return new AppenderRefType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Configuration }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Configuration }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.testgenerator.org/logging", name = "Configuration")
    public JAXBElement<Configuration> createConfiguration(Configuration value) {
        return new JAXBElement<Configuration>(_Configuration_QNAME, Configuration.class, null, value);
    }

}
