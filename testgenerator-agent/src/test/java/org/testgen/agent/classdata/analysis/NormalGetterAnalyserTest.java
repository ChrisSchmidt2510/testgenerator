package org.testgen.agent.classdata.analysis;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.Person;
import org.testgen.core.Wrapper;

import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

public class NormalGetterAnalyserTest extends TestHelper {

	private MethodAnalysis analyser;

	@Test
	public void testAnalyseGetter() throws NotFoundException, BadBytecode {
		init(Adresse.class, "getStrasse");

		analyser = new NormalGetterAnalyser("org.testgen.agent.classdata.testclasses.Adresse");

		Wrapper<FieldData> wrapper = new Wrapper<>();

		Assert.assertTrue(analyser.analyse(methodInfo.getDescriptor(), instructions, wrapper));

		FieldData fieldData = wrapper.getValue();
		Assert.assertEquals("strasse", fieldData.getName());
		Assert.assertEquals("java.lang.String", fieldData.getDataType());
	}

	@Test
	public void testAnalyseSetter() throws NotFoundException, BadBytecode {
		init(Adresse.class, "setStrasse");

		analyser = new NormalGetterAnalyser("org.testgen.agent.classdata.testclasses.Adresse");

		Assert.assertFalse(analyser.analyse(methodInfo.getDescriptor(), instructions, new Wrapper<>()));
	}

	@Test
	public void testAnalyseCollectionSetter() throws NotFoundException, BadBytecode {
		init(Person.class, "addAdresse");

		analyser = new NormalGetterAnalyser("org.testgen.agent.classdata.testclasses.Person");

		Assert.assertFalse(analyser.analyse(methodInfo.getDescriptor(), instructions, new Wrapper<>()));
	}

	@Test
	public void testAnalyseImmutableCollectionGetter() throws NotFoundException, BadBytecode {
		init(Person.class, "getAdressen");

		analyser = new NormalGetterAnalyser("org.testgen.agent.classdata.testclasses.Person");

		Assert.assertFalse(analyser.analyse(methodInfo.getDescriptor(), instructions, new Wrapper<>()));
	}

}
