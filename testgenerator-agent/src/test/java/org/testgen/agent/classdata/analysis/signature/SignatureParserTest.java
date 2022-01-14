package org.testgen.agent.classdata.analysis.signature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
			fail();
		}

		System.out.println(signature);

		SignatureData list = new SignatureData("Ljava/util/List;");
		list.addSubType(adresse);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(integer);
		map.addSubType(list);

		assertEquals(map, signature);
	}

	@Test
	public void testParseMultipleNestedSignatures() {
		SignatureData signature = null;

		try {
			signature = SignatureParser.parse(
					"Ljava/util/Map<Ljava/lang/Integer;Ljava/util/List<Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;>;");
		} catch (SignatureParserException e) {
			e.printStackTrace();
			fail();
		}

		System.out.println(signature);

		SignatureData nestedList = new SignatureData("Ljava/util/List;");
		nestedList.addSubType(adresse);

		SignatureData list = new SignatureData("Ljava/util/List;");
		list.addSubType(nestedList);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(integer);
		map.addSubType(list);

		assertEquals(map, signature);
	}

	@Test
	public void testParseMultipleNestedSignaturesAtStart() {
		SignatureData signature = null;

		try {
			signature = SignatureParser.parse(
					"Ljava/util/Map<Ljava/util/List<Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;Ljava/lang/Integer;>;");
		} catch (SignatureParserException e) {
			e.printStackTrace();
			fail();
		}

		System.out.println(signature);

		SignatureData nestedList = new SignatureData("Ljava/util/List;");
		nestedList.addSubType(adresse);

		SignatureData list = new SignatureData("Ljava/util/List;");
		list.addSubType(nestedList);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(list);
		map.addSubType(integer);

		assertEquals(map, signature);
	}

	@Test
	public void testParseMultipleSignatures() {
		SignatureData signature = null;

		try {
			signature = SignatureParser.parse(
					"Ljava/util/Map<Ljava/util/List<Ljava/lang/Integer;>;Ljava/util/List<Lde/nvg/bl/partner/Adresse;>;>;");
		} catch (SignatureParserException e) {
			fail(e);
		}

		System.out.println(signature);

		SignatureData integerList = new SignatureData("Ljava/util/List;");
		integerList.addSubType(integer);

		SignatureData adressList = new SignatureData("Ljava/util/List;");
		adressList.addSubType(adresse);

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(integerList);
		map.addSubType(adressList);

		assertEquals(map, signature);
	}

	@Test
	public void testParseNestedMaps() {
		SignatureData signature = null;

		try {
			signature = SignatureParser.parse(
					"Ljava/util/Map<Ljava/time/LocalDate;Ljava/util/Map<Ljava/lang/Integer;Ljava/math/BigDecimal;>;>;");
		} catch (SignatureParserException e) {
			fail(e);
		}

		System.out.println(signature);

		SignatureData nestedMap = new SignatureData("Ljava/util/Map;");
		nestedMap.addSubType(new SignatureData("Ljava/lang/Integer;"));
		nestedMap.addSubType(new SignatureData("Ljava/math/BigDecimal;"));

		SignatureData map = new SignatureData("Ljava/util/Map;");
		map.addSubType(new SignatureData("Ljava/time/LocalDate;"));
		map.addSubType(nestedMap);

		assertEquals(map, signature);
	}

	private static Stream<Arguments> testNonParsableSignature() {
		return Stream.of(
				Arguments.of("TE<Ljava/util/List<Ljava/lang/Integer;>;>;",
						"TE; cant't be parsed into a valid Signature"),
				Arguments.of("Ljava/util/Map<Ljava/util/List<Ljava/lang/Integer;>;TK;>;",
						"TK; cant't be parsed into a valid Signature"));
	}

	@ParameterizedTest
	@MethodSource
	public void testNonParsableSignature(String signature, String message) throws SignatureParserException {
		assertThrows(SignatureParserException.class, () -> SignatureParser.parse(signature), message);
	}

}
