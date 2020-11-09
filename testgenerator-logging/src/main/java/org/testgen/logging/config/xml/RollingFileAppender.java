//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.11.09 um 07:31:43 PM CET 
//


package org.testgen.logging.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RollingFileAppender complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RollingFileAppender"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SizeBasedTriggeringPolicy" type="{http://www.testgenerator.org/logging}SizeBasedTriggeringPolicyType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="fileName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RollingFileAppender", propOrder = {
    "sizeBasedTriggeringPolicy"
})
public class RollingFileAppender {

    @XmlElement(name = "SizeBasedTriggeringPolicy")
    protected SizeBasedTriggeringPolicyType sizeBasedTriggeringPolicy;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "fileName", required = true)
    protected String fileName;

    /**
     * Ruft den Wert der sizeBasedTriggeringPolicy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SizeBasedTriggeringPolicyType }
     *     
     */
    public SizeBasedTriggeringPolicyType getSizeBasedTriggeringPolicy() {
        return sizeBasedTriggeringPolicy;
    }

    /**
     * Legt den Wert der sizeBasedTriggeringPolicy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SizeBasedTriggeringPolicyType }
     *     
     */
    public void setSizeBasedTriggeringPolicy(SizeBasedTriggeringPolicyType value) {
        this.sizeBasedTriggeringPolicy = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der fileName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Legt den Wert der fileName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

}
