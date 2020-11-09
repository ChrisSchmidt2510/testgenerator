//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.11.09 um 07:31:43 PM CET 
//

package org.testgen.logging.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für Configuration complex type.
 * 
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Configuration"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Appenders" type="{http://www.testgenerator.org/logging}AppendersType"/&gt;
 *         &lt;element name="ConsoleAppender" type="{http://www.testgenerator.org/logging}ConsoleAppender" minOccurs="0"/&gt;
 *         &lt;element name="Loggers" type="{http://www.testgenerator.org/logging}LoggersType"/&gt;
 *         &lt;element name="Root" type="{http://www.testgenerator.org/logging}RootLoggerType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Configuration", propOrder = { "appenders", "consoleAppender", "loggers", "root" })
@XmlRootElement(name = "Configuration")
public class Configuration {

	@XmlElement(name = "Appenders", required = true)
	protected AppendersType appenders;
	@XmlElement(name = "ConsoleAppender")
	protected ConsoleAppender consoleAppender;
	@XmlElement(name = "Loggers", required = true)
	protected LoggersType loggers;
	@XmlElement(name = "Root", required = true)
	protected RootLoggerType root;

	/**
	 * Ruft den Wert der appenders-Eigenschaft ab.
	 * 
	 * @return possible object is {@link AppendersType }
	 * 
	 */
	public AppendersType getAppenders() {
		return appenders;
	}

	/**
	 * Legt den Wert der appenders-Eigenschaft fest.
	 * 
	 * @param value allowed object is {@link AppendersType }
	 * 
	 */
	public void setAppenders(AppendersType value) {
		this.appenders = value;
	}

	/**
	 * Ruft den Wert der consoleAppender-Eigenschaft ab.
	 * 
	 * @return possible object is {@link ConsoleAppender }
	 * 
	 */
	public ConsoleAppender getConsoleAppender() {
		return consoleAppender;
	}

	/**
	 * Legt den Wert der consoleAppender-Eigenschaft fest.
	 * 
	 * @param value allowed object is {@link ConsoleAppender }
	 * 
	 */
	public void setConsoleAppender(ConsoleAppender value) {
		this.consoleAppender = value;
	}

	/**
	 * Ruft den Wert der loggers-Eigenschaft ab.
	 * 
	 * @return possible object is {@link LoggersType }
	 * 
	 */
	public LoggersType getLoggers() {
		return loggers;
	}

	/**
	 * Legt den Wert der loggers-Eigenschaft fest.
	 * 
	 * @param value allowed object is {@link LoggersType }
	 * 
	 */
	public void setLoggers(LoggersType value) {
		this.loggers = value;
	}

	/**
	 * Ruft den Wert der root-Eigenschaft ab.
	 * 
	 * @return possible object is {@link RootLoggerType }
	 * 
	 */
	public RootLoggerType getRoot() {
		return root;
	}

	/**
	 * Legt den Wert der root-Eigenschaft fest.
	 * 
	 * @param value allowed object is {@link RootLoggerType }
	 * 
	 */
	public void setRoot(RootLoggerType value) {
		this.root = value;
	}

}
