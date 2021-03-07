package org.testgen.agent.classdata.analysis.signature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testgen.agent.classdata.model.SignatureData;

public class SignatureParserTest {
	private final SignatureData adresse = new SignatureData("Lde/nvg/bl/partner/Adresse;");
	private final SignatureData integer = new SignatureData("Ljava/lang/Integer;");

	@Test
	public void testParseNestedSignature() {

		SignatureData signature = null;
		try {
			signature = SignatureParser
					.parse("Ljava/util/Map<Ljava/lang/Integer;Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;");
		} catch (SignatureParserException e) {
			e.printStackTrace();
			Assertions.fail();
		}

		System.out.println(signature);

		SignatureData list = new SignatureData("Ljava/util/List;");
		list.addSubType(adresse);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(integer);
		map.addSubType(list);

		Assertions.assertEquals(map, signature);
	}

	@Test
	public void testParseMultipleNestedSignatures() {
		SignatureData signature = null;

		try {
			signature = SignatureParser.parse(
					"Ljava/util/Map<Ljava/lang/Integer;Ljava/util/List<Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;>;");
		} catch (SignatureParserException e) {
			e.printStackTrace();
			Assertions.fail();
		}

		System.out.println(signature);

		SignatureData nestedList = new SignatureData("Ljava/util/List;");
		nestedList.addSubType(adresse);

		SignatureData list = new SignatureData("Ljava/util/List;");
		list.addSubType(nestedList);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(integer);
		map.addSubType(list);

		Assertions.assertEquals(map, signature);
	}

	@Test
	public void testParseMultipleNestedSignaturesAtStart() {
		SignatureData signature = null;

		try {
			signature = SignatureParser.parse(
					"Ljava/util/Map<Ljava/util/List<Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;Ljava/lang/Integer;>;");
		} catch (SignatureParserException e) {
			e.printStackTrace();
			Assertions.fail();
		}

		System.out.println(signature);

		SignatureData nestedList = new SignatureData("Ljava/util/List;");
		nestedList.addSubType(adresse);

		SignatureData list = new SignatureData("Ljava/util/List;");
		list.addSubType(nestedList);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(list);
		map.addSubType(integer);

		Assertions.assertEquals(map, signature);
	}

	@Test
	public void testParseMultipleSignatures() {
		SignatureData signature = null;

		try {
			signature = SignatureParser.parse(
					"Ljava/util/Map<Ljava/util/List<Ljava/lang/Integer;>;Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;");
		} catch (SignatureParserException e) {
			e.printStackTrace();
			Assertions.fail();
		}

		System.out.println(signature);

		SignatureData integerList = new SignatureData("Ljava/util/List;");
		integerList.addSubType(integer);

		SignatureData adressList = new SignatureData("Ljava/util/List;");
		adressList.addSubType(adresse);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(integerList);
		map.addSubType(adressList);

		Assertions.assertEquals(map, signature);
	}

	@Test
	public void testParseNestedMaps() {
		SignatureData signature = null;

		try {
			signature = SignatureParser.parse(
					"Ljava/util/Map<Ljava/time/LocalDate;Ljava/util/Map<Ljava/lang/Integer;Ljava/math/BigDecimal;>;>;");
		} catch (SignatureParserException e) {
			e.printStackTrace();
			Assertions.fail();
		}

		System.out.println(signature);

		SignatureData nestedMap = new SignatureData("Ljava/util/Map;");
		nestedMap.addSubType(new SignatureData("Ljava/lang/Integer;"));
		nestedMap.addSubType(new SignatureData("Ljava/math/BigDecimal;"));

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(new SignatureData("Ljava/time/LocalDate;"));
		map.addSubType(nestedMap);

		Assertions.assertEquals(map, signature);
	}

	@Test
	public void testNonParsableSignatureAtTheStart() throws SignatureParserException {
		SignatureParserException signatureException = Assertions.assertThrows(SignatureParserException.class,
				() -> SignatureParser.parse("TE<Ljava/util/List<Ljava/lang/Integer;>;>;"));

		Assertions.assertEquals("TE; cant't be parsed into a valid Signature", signatureException.getMessage());

	}

	@Test
	public void testNonParsableSignatureInTheMiddle() throws SignatureParserException {
		SignatureParserException signatureException = Assertions.assertThrows(SignatureParserException.class,
				() -> SignatureParser.parse("Ljava/util/Map<Ljava/util/List<Ljava/lang/Integer;>;TK;>;"));

		Assertions.assertEquals("TK; cant't be parsed into a valid Signature", signatureException.getMessage());
	}

}
