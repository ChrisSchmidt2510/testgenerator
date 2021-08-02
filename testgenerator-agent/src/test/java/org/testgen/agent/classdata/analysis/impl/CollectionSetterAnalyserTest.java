package org.testgen.agent.classdata.analysis.impl;

import java.util.Map.Entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.analysis.MethodAnalysis2;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.agent.classdata.model.SignatureData;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.Collections;
import org.testgen.agent.classdata.testclasses.Person;

import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

public class CollectionSetterAnalyserTest extends TestHelper {

	private MethodAnalysis2 analyser = new CollectionSetterAnalyser();

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "getStrasse");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertFalse(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseImmutableGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "getHausnummer");

		analyser.setClassData(classData);

		Assertions.assertFalse(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseSetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "setStrasse");

		analyser.setClassData(classData);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getCollectionsClassData")
	public void testAnalyseCollectionSetterCollection(ClassData classData) throws NotFoundException, BadBytecode {
		init(Collections.class, "addCollection");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		String fieldName = "collection";
		String fieldType = "java.util.Collection";

		SignatureData signature = new SignatureData("Ljava/util/Collection;");
		signature.addSubType(new SignatureData("Ljava/lang/String;"));

		Entry<MethodData, FieldData> methodAddCollection = classData.getMethod("addCollection",
				"(Ljava/lang/String;)V");

		compareResult(methodAddCollection, fieldName, fieldType, signature);

		init(Collections.class, "addAllCollection");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> methodAddAllCollection = classData.getMethod("addAllCollection",
				"(Ljava/util/Collection;)V");

		compareResult(methodAddAllCollection, fieldName, fieldType, signature);
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getCollectionsClassData")
	public void testAnalyseCollectionSetterList(ClassData classData) throws NotFoundException, BadBytecode {
		init(Collections.class, "addList");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		String fieldName = "list";
		String fieldType = "java.util.List";

		SignatureData signature = new SignatureData("Ljava/util/List;");
		signature.addSubType(new SignatureData("Ljava/lang/String;"));

		Entry<MethodData, FieldData> addListEntry = classData.getMethod("addList", "(Ljava/lang/String;)V");
		compareResult(addListEntry, fieldName, fieldType, signature);

		init(Collections.class, "addListWithIndex");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> addListWithIndexEntry = classData.getMethod("addListWithIndex",
				"(Ljava/lang/String;)V");
		compareResult(addListWithIndexEntry, fieldName, fieldType, signature);

		init(Collections.class, "addAllList");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> addAllListEntry = classData.getMethod("addAllList", "(Ljava/util/Collection;)V");
		compareResult(addAllListEntry, fieldName, fieldType, signature);

		init(Collections.class, "addAllListWithIndex");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> addAllListWithIndexEntry = classData.getMethod("addAllListWithIndex",
				"(Ljava/util/Collection;)V");
		compareResult(addAllListWithIndexEntry, fieldName, fieldType, signature);

		init(Collections.class, "addArrayList");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		SignatureData signatureArrayList = new SignatureData("Ljava/util/ArrayList;");
		signatureArrayList.addSubType(new SignatureData("Ljava/lang/String;"));

		Entry<MethodData, FieldData> addArrayListEntry = classData.getMethod("addArrayList", "(Ljava/lang/String;)V");
		compareResult(addArrayListEntry, "arrayList", "java.util.ArrayList", signatureArrayList);
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getCollectionsClassData")
	public void testAnalyseCollectionSetterSet(ClassData classData) throws NotFoundException, BadBytecode {
		init(Collections.class, "addSet");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		String fieldName = "set";
		String fieldType = "java.util.Set";

		SignatureData signature = new SignatureData("Ljava/util/Set;");
		signature.addSubType(new SignatureData("Ljava/lang/String;"));

		Entry<MethodData, FieldData> addSetEntry = classData.getMethod("addSet", "(Ljava/lang/String;)V");
		compareResult(addSetEntry, fieldName, fieldType, signature);

		init(Collections.class, "addAllSet");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> addAllSetEntry = classData.getMethod("addAllSet", "(Ljava/util/Set;)V");
		compareResult(addAllSetEntry, fieldName, fieldType, signature);
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getCollectionsClassData")
	public void testAnalyseCollectionSetterQueue(ClassData classData) throws NotFoundException, BadBytecode {
		init(Collections.class, "addQueue");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		String fieldName = "queue";
		String fieldType = "java.util.Queue";

		SignatureData signature = new SignatureData("Ljava/util/Queue;");
		signature.addSubType(new SignatureData("Ljava/lang/String;"));

		Entry<MethodData, FieldData> addQueueEntry = classData.getMethod("addQueue", "(Ljava/lang/String;)V");
		compareResult(addQueueEntry, fieldName, fieldType, signature);

		init(Collections.class, "offerQueue");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> offerQueueEntry = classData.getMethod("offerQueue", "(Ljava/lang/String;)V");
		compareResult(offerQueueEntry, fieldName, fieldType, signature);

	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getCollectionsClassData")
	public void testAnalyseCollectionSetterDeque(ClassData classData) throws NotFoundException, BadBytecode {
		init(Collections.class, "addDeque");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		String fieldName = "deque";
		String fieldType = "java.util.Deque";

		SignatureData signature = new SignatureData("Ljava/util/Deque;");
		signature.addSubType(new SignatureData("Ljava/lang/String;"));

		Entry<MethodData, FieldData> addDequeEntry = classData.getMethod("addDeque", "(Ljava/lang/String;)V");
		compareResult(addDequeEntry, fieldName, fieldType, signature);

		init(Collections.class, "addFirstDeque");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> addFirstDequeEntry = classData.getMethod("addFirstDeque", "(Ljava/lang/String;)V");
		compareResult(addFirstDequeEntry, fieldName, fieldType, signature);

		init(Collections.class, "addLastDeque");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> addLastDequeEntry = classData.getMethod("addLastDeque", "(Ljava/lang/String;)V");
		compareResult(addLastDequeEntry, fieldName, fieldType, signature);

		init(Collections.class, "addAllDeque");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> addAllDequeEntry = classData.getMethod("addAllDeque", "(Ljava/util/Collection;)V");
		compareResult(addAllDequeEntry, fieldName, fieldType, signature);

		init(Collections.class, "offerFirstDeque");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> offerFirstDequeEntry = classData.getMethod("offerFirstDeque",
				"(Ljava/lang/String;)V");
		compareResult(offerFirstDequeEntry, fieldName, fieldType, signature);

		init(Collections.class, "offerLastDeque");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> offerLastDequeEntry = classData.getMethod("offerLastDeque",
				"(Ljava/lang/String;)V");
		compareResult(offerLastDequeEntry, fieldName, fieldType, signature);

		init(Collections.class, "pushDeque");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> pushDequeEntry = classData.getMethod("pushDeque", "(Ljava/lang/String;)V");
		compareResult(pushDequeEntry, fieldName, fieldType, signature);
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getCollectionsClassData")
	public void testAnalyseCollectionSetterMap(ClassData classData) throws NotFoundException, BadBytecode {
		init(Collections.class, "putMap");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		String fieldName = "map";
		String fieldType = "java.util.Map";

		SignatureData signature = new SignatureData("Ljava/util/Map;");
		signature.addSubType(new SignatureData("Ljava/lang/String;"));
		signature.addSubType(new SignatureData("Ljava/lang/Integer;"));

		Entry<MethodData, FieldData> putMapEntry = classData.getMethod("putMap",
				"(Ljava/lang/String;Ljava/lang/Integer;)V");
		compareResult(putMapEntry, fieldName, fieldType, signature);

		init(Collections.class, "putAllMap");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> putAllMapEntry = classData.getMethod("putAllMap", "(Ljava/util/Map;)V");
		compareResult(putAllMapEntry, fieldName, fieldType, signature);

		init(Collections.class, "putIfAbsentMap");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> putIfAbsentMapEntry = classData.getMethod("putIfAbsentMap",
				"(Ljava/lang/String;Ljava/lang/Integer;)V");
		compareResult(putIfAbsentMapEntry, fieldName, fieldType, signature);

		init(Collections.class, "computeIfAbsentMap");

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> computeIfAbsentMapEntry = classData.getMethod("computeIfAbsentMap",
				"(Ljava/lang/String;Ljava/lang/Integer;)V");
		compareResult(computeIfAbsentMapEntry, fieldName, fieldType, signature);
	}

	private void compareResult(Entry<MethodData, FieldData> methodEntry, String fieldName, String fieldType,
			SignatureData signature) {
		Assertions.assertNotNull(methodEntry);

		MethodData method = methodEntry.getKey();
		Assertions.assertEquals(MethodType.COLLECTION_SETTER, method.getMethodType());
		Assertions.assertFalse(method.isStatic());

		FieldData field = methodEntry.getValue();
		Assertions.assertEquals(fieldName, field.getName());
		Assertions.assertEquals(fieldType, field.getDataType());

		Assertions.assertEquals(signature, field.getSignature());
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getPersonClassData")
	public void testAnalyseImmutableCollectionGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Person.class, "getAdressen");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertFalse(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

}
