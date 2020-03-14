package de.nvg.agent.classdata.analysis;

import org.junit.Assert;
import org.junit.Test;

import de.nvg.agent.classdata.model.SignatureData;

public class SignatureParserTest {
	private final SignatureData adresse = new SignatureData("Lde/nvg/bl/partner/Adresse;");
	private final SignatureData integer = new SignatureData("Ljava/lang/Integer;");

	@Test
	public void testParseNestedSignature() {

		SignatureData signature = SignatureParser
				.parse("Ljava/util/Map<Ljava/lang/Integer;Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;");

		System.out.println(signature);

		SignatureData list = new SignatureData("Ljava/util/List;");
		list.addSubType(adresse);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(integer);
		map.addSubType(list);

		Assert.assertEquals(map, signature);
	}

	@Test
	public void testParseMultipleNestedSignatures() {
		SignatureData signature = SignatureParser.parse(
				"Ljava/util/Map<Ljava/lang/Integer;Ljava/util/List<Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;>;");
		System.out.println(signature);

		SignatureData nestedList = new SignatureData("Ljava/util/List;");
		nestedList.addSubType(adresse);

		SignatureData list = new SignatureData("Ljava/util/List;");
		list.addSubType(nestedList);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(integer);
		map.addSubType(list);

		Assert.assertEquals(map, signature);
	}

	@Test
	public void testParseMultipleNestedSignaturesAtStart() {
		SignatureData signature = SignatureParser.parse(
				"Ljava/util/Map<Ljava/util/List<Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;Ljava/lang/Integer;>;");

		System.out.println(signature);

		SignatureData nestedList = new SignatureData("Ljava/util/List;");
		nestedList.addSubType(adresse);

		SignatureData list = new SignatureData("Ljava/util/List;");
		list.addSubType(nestedList);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(list);
		map.addSubType(integer);

		Assert.assertEquals(map, signature);
	}

	@Test
	public void testParseMultipleSignatures() {
		SignatureData signature = SignatureParser.parse(
				"Ljava/util/Map<Ljava/util/List<Ljava/lang/Integer;>;Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;");

		System.out.println(signature);

		SignatureData integerList = new SignatureData("Ljava/util/List;");
		integerList.addSubType(integer);

		SignatureData adressList = new SignatureData("Ljava/util/List;");
		adressList.addSubType(adresse);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(integerList);
		map.addSubType(adressList);

		Assert.assertEquals(map, signature);
	}

}
