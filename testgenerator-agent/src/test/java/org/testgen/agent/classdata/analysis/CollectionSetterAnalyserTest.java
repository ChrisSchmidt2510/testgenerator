package org.testgen.agent.classdata.analysis;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.SignatureData;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.Person;
import org.testgen.core.Wrapper;

import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

public class CollectionSetterAnalyserTest extends TestHelper {
	private MethodAnalysis analyser;

	private SignatureData signatureAdresse;

	private FieldData strasse;
	private FieldData adressen;

	{
		signatureAdresse = new SignatureData("Ljava/util/List;");
		signatureAdresse.addSubType(new SignatureData("Lorg/testgen/agent/classdata/testclasses/Adresse;"));

		strasse = new FieldData.Builder().withName("strasse").withDataType("java.lang.String").build();

		adressen = new FieldData.Builder().withName("adressen").withDataType("java.util.List")
				.withSignature(signatureAdresse).build();
	}

	@Test
	public void testAnalyseGetter() throws NotFoundException, BadBytecode {
		init(Adresse.class, "getStrasse");

		analyser = new CollectionSetterAnalyser(Arrays.asList(strasse));

		Assert.assertFalse(analyser.analyse(methodInfo.getDescriptor(), instructions, new Wrapper<>()));
	}

	@Test
	public void testAnalyseSetter() throws NotFoundException, BadBytecode {
		init(Adresse.class, "setStrasse");

		analyser = new CollectionSetterAnalyser(Arrays.asList(strasse));

		Assert.assertFalse(analyser.analyse(methodInfo.getDescriptor(), instructions, new Wrapper<>()));
	}

	@Test
	public void testAnalyseCollectionSetter() throws NotFoundException, BadBytecode {
		init(Person.class, "addAdresse");

		analyser = new CollectionSetterAnalyser(Arrays.asList(adressen));

		Wrapper<FieldData> wrapper = new Wrapper<>();

		Assert.assertTrue(analyser.analyse(methodInfo.getDescriptor(), instructions, wrapper));

		FieldData value = wrapper.getValue();
		Assert.assertEquals(adressen.getName(), value.getName());
		Assert.assertEquals(adressen.getDataType(), value.getDataType());
	}

	@Test
	public void testAnalyseImmutableCollectionGetter() throws NotFoundException, BadBytecode {
		init(Person.class, "getAdressen");

		analyser = new CollectionSetterAnalyser(Arrays.asList(adressen));

		Assert.assertFalse(analyser.analyse(methodInfo.getDescriptor(), instructions, new Wrapper<>()));
	}

}
