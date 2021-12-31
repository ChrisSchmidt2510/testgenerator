package org.testgen.agent.classdata.analysis;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.SignatureData;

public class AnalysisTestDataFactory {

	static Stream<ClassData> getAdresseClassData() {
		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Adresse");

		FieldData fieldStrasse = new FieldData.Builder().withModifier(Modifier.PRIVATE).withName("strasse")
				.withDataType("java.lang.String").build();

		FieldData fieldHausnummer = new FieldData.Builder().withModifier(Modifier.PRIVATE | Modifier.FINAL)
				.withName("hausnummer").withDataType(Primitives.JAVA_SHORT).build();

		FieldData fieldOrt = new FieldData.Builder().withModifier(Modifier.PRIVATE).withName("ort")
				.withDataType("java.lang.String").build();

		FieldData fieldPlz = new FieldData.Builder().withModifier(Modifier.PRIVATE).withName("plz")
				.withDataType(Primitives.JAVA_INT).build();

		classData.addFields(Arrays.asList(fieldStrasse, fieldHausnummer, fieldOrt, fieldPlz));

		return Stream.of(classData);
	}

	static Stream<ClassData> getPersonClassData() {
		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Person");

		SignatureData signatureAdresse = new SignatureData("Ljava/util/List;");
		signatureAdresse.addSubType(new SignatureData("Lorg/testgen/agent/classdata/testclasses/Adresse;"));

		FieldData adressen = new FieldData.Builder().withName("adressen").withDataType("java.util.List")
				.withSignature(signatureAdresse).build();
		classData.addFields(Arrays.asList(adressen));

		return Stream.of(classData);
	}

	static Stream<ClassData> getCollectionsClassData() {
		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Collections");

		SignatureData signatureString = new SignatureData("Ljava/lang/String;");

		SignatureData signatureCollection = new SignatureData("Ljava/util/Collection;");
		signatureCollection.addSubType(signatureString);

		FieldData collection = new FieldData.Builder().withName("collection").withDataType("java.util.Collection")
				.withSignature(signatureCollection).build();

		SignatureData signatureList = new SignatureData("Ljava/util/List;");
		signatureList.addSubType(signatureString);

		FieldData list = new FieldData.Builder().withName("list").withDataType("java.util.List")
				.withSignature(signatureList).build();

		SignatureData signatureArrayList = new SignatureData("Ljava/util/ArrayList;");
		signatureArrayList.addSubType(signatureString);

		FieldData arrayList = new FieldData.Builder().withName("arrayList").withDataType("java.util.ArrayList")
				.withSignature(signatureArrayList).build();

		SignatureData signatureSet = new SignatureData("Ljava/util/Set;");
		signatureSet.addSubType(signatureString);

		FieldData set = new FieldData.Builder().withName("set").withDataType("java.util.Set")
				.withSignature(signatureSet).build();

		SignatureData signatureQueue = new SignatureData("Ljava/util/Queue;");
		signatureQueue.addSubType(signatureString);

		FieldData queue = new FieldData.Builder().withName("queue").withDataType("java.util.Queue")
				.withSignature(signatureQueue).build();

		SignatureData signatureDeque = new SignatureData("Ljava/util/Deque;");
		signatureDeque.addSubType(signatureString);

		FieldData deque = new FieldData.Builder().withName("deque").withDataType("java.util.Deque")
				.withSignature(signatureDeque).build();

		SignatureData signatureMap = new SignatureData("Ljava/util/Map;");
		signatureMap.addSubType(signatureString);
		signatureMap.addSubType(new SignatureData("Ljava/lang/Integer;"));

		FieldData map = new FieldData.Builder().withName("map").withDataType("java.util.Map")
				.withSignature(signatureMap).build();

		classData.addFields(Arrays.asList(collection, list, arrayList, set, queue, deque, map));

		return Stream.of(classData);
	}

}
