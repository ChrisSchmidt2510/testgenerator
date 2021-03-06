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

public class ImmutableCollectionGetterAnalyserTest extends TestHelper {

	private MethodAnalysis analyser = new ImmutableCollectionGetterAnalyser();

	@Test
	public void testAnalyseGetter() throws NotFoundException, BadBytecode {
		init(Adresse.class, "getStrasse");

		Assert.assertFalse(analyser.analyse(methodInfo.getDescriptor(), instructions, new Wrapper<>()));
	}

	@Test
	public void testAnalyseSetter() throws NotFoundException, BadBytecode {
		init(Adresse.class, "setStrasse");

		Assert.assertFalse(analyser.analyse(methodInfo.getDescriptor(), instructions, new Wrapper<>()));
	}

	@Test
	public void testAnalyseCollectionSetter() throws NotFoundException, BadBytecode {
		init(Person.class, "addAdresse");

		Assert.assertFalse(analyser.analyse(methodInfo.getDescriptor(), instructions, new Wrapper<>()));
	}

	@Test
	public void testAnalyseImmutableCollectionGetter() throws NotFoundException, BadBytecode {
		init(Person.class, "getAdressen");

		Wrapper<FieldData> wrapper = new Wrapper<>();

		Assert.assertTrue(analyser.analyse(methodInfo.getDescriptor(), instructions, wrapper));

		FieldData value = wrapper.getValue();
		Assert.assertEquals("adressen", value.getName());
		Assert.assertEquals("java.util.List", value.getDataType());
	}

}
