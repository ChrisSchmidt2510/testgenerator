package org.testgen.runtime.generation.javaparser.impl.simpleGeneration;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.generation.api.GenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.generation.javaparser.impl.simple.XmlGregorianCalendarObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.XMLGregorianCalendarBluePrint.XMLGregorianCalendarBluePrintFactory;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class XmlGregorianCalendarGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration;

	private XMLGregorianCalendarBluePrintFactory factory = new XMLGregorianCalendarBluePrintFactory();

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		simpleObjectGeneration = new XmlGregorianCalendarObjectGeneration();
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFieldNames();
	}

	@Test
	public void testCreateObject() throws IOException {
		PrettyPrinterConfiguration config = new PrettyPrinterConfiguration();
		config.setPrintComments(false);

		XMLGregorianCalendar calendar = null;

		try {
			calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(2020, 12 - 1, 31));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", calendar, null).castToSimpleBluePrint();

		BlockStmt block = new BlockStmt();

		String expectedValueField = "try {\r\n"//
				+ "    this.value = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2020, 12, 31, 60);\r\n"//
				+ "} catch (DatatypeConfigurationException e) {\r\n" //
				+ "    e.printStackTrace();\r\n"//
				+ "}";

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals(expectedValueField, block.getStatement(0).toString(config));

		BlockStmt newBlock = new BlockStmt();

		String expectedValueLocal = "{\r\n"//
				+ "    XMLGregorianCalendar value = null;\r\n"//
				+ "    try {\r\n"//
				+ "        value = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2020, 12, 31, 60);\r\n"//
				+ "    } catch (DatatypeConfigurationException e) {\r\n" //
				+ "        e.printStackTrace();\r\n"//
				+ "    }\r\n"//
				+ "}";

		simpleObjectGeneration.createObject(newBlock, bluePrint, false);
		Assert.assertEquals(expectedValueLocal, newBlock.toString(config));
	}
}
