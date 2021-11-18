package org.testgen.runtime.generation.javaparser.impl.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.generation.javaparser.impl.TestgeneratorPrettyPrinter;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.XMLGregorianCalendarBluePrint.XMLGregorianCalendarBluePrintFactory;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class XmlGregorianCalendarGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration = new XmlGregorianCalendarObjectGeneration();

	private XMLGregorianCalendarBluePrintFactory factory = new XMLGregorianCalendarBluePrintFactory();

	@BeforeEach
	public void init() {
		simpleObjectGeneration.setImportCallBackHandler(imports::add);
	}

	@AfterEach
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateObject() throws IOException {
		PrettyPrinterConfiguration config = new PrettyPrinterConfiguration();
		config.setPrintComments(false);
		config.setVisitorFactory(TestgeneratorPrettyPrinter::new);

		XMLGregorianCalendar calendar = null;

		try {
			calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(2020, 12 - 1, 31));
		} catch (DatatypeConfigurationException e) {
			fail(e);
		}

		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", calendar);

		BlockStmt block = new BlockStmt();

		String expectedValueField = "try {\r\n"//
				+ "    this.value = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2020, 12, 31, 60);\r\n"//
				+ "} catch (DatatypeConfigurationException e) {\r\n" //
				+ "    e.printStackTrace();\r\n"//
				+ "}";

		simpleObjectGeneration.createObject(block, bluePrint, true);
		assertEquals(expectedValueField, block.getStatement(0).toString(config));

		BlockStmt newBlock = new BlockStmt();

		String expectedValueLocal = "{\r\n"//
				+ "    XMLGregorianCalendar value = null;\r\n"//
				+ "    try {\r\n"//
				+ "        value = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2020, 12, 31, 60);\r\n"//
				+ "    } catch (DatatypeConfigurationException e) {\r\n" //
				+ "        e.printStackTrace();\r\n"//
				+ "    }\r\n"//
				+ "\r\n"//
				+ "}";

		simpleObjectGeneration.createObject(newBlock, bluePrint, false);
		assertEquals(expectedValueLocal, newBlock.toString(config));
	}
}
