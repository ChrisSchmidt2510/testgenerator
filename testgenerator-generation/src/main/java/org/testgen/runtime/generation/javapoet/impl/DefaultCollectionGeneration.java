package org.testgen.runtime.generation.javapoet.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.lang.model.element.Modifier;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.ArrayGeneration;
import org.testgen.runtime.generation.CollectionGeneration;
import org.testgen.runtime.generation.ComplexObjectGeneration;
import org.testgen.runtime.generation.naming.NamingService;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.CollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.MapBluePrint;
import org.testgen.runtime.valuetracker.storage.ValueStorage;

import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class DefaultCollectionGeneration implements CollectionGeneration<TypeSpec.Builder, Builder> {

	private static final Logger LOGGER = LogManager.getLogger(DefaultCollectionGeneration.class);

	private static final String GENERIC_CONSTRUCTOR = " = new $T<>()";

	private ComplexObjectGeneration<TypeSpec.Builder, Builder> objectGeneration = getComplexObjectGeneration();

	private ArrayGeneration<TypeSpec.Builder, Builder> arrayGeneration = getArrayGeneration();

	private NamingService namingService = getNamingService();

	@Override
	public void createCollection(Builder code, AbstractBasicCollectionBluePrint<?> basicCollectionBP, //
			SignatureType signature, boolean onlyCreateCollectionElements, boolean isField) {

		LOGGER.info("starting generation of Collection:" + basicCollectionBP);

		if (basicCollectionBP.isNotBuild()) {
			if (basicCollectionBP instanceof CollectionBluePrint) {
				createCollection(code, (CollectionBluePrint) basicCollectionBP, signature, //
						onlyCreateCollectionElements, isField);

			} else if (basicCollectionBP instanceof MapBluePrint) {
				createMap(code, (MapBluePrint) basicCollectionBP, signature, //
						onlyCreateCollectionElements, isField);
			}
		}

	}

	@Override
	public void addContainerToObject(Builder code, AbstractBasicCollectionBluePrint<?> collectionBP,
			SetterMethodData setter, String objectName) {

		LOGGER.info("add Collection " + collectionBP + " to Object " + objectName);

		if (collectionBP instanceof CollectionBluePrint) {
			addCollectionToObject(code, (CollectionBluePrint) collectionBP, setter, objectName);
		} else if (collectionBP instanceof MapBluePrint) {
			addMapToObject(code, (MapBluePrint) collectionBP, setter, objectName);
		}

	}

	@Override
	public void addContainerToObject(Builder code, AbstractBasicCollectionBluePrint<?> containerBP, FieldData field,
			String objectName) {

		LOGGER.info("add Collection " + containerBP + " to Object " + objectName);

		code.addStatement(objectName + "." + field.getName() + "=" + namingService.getName(containerBP));

	}

	@Override
	public void createField(TypeSpec.Builder typeSpec, AbstractBasicCollectionBluePrint<?> collectionBP,
			SignatureType signature) {

		if (collectionBP instanceof CollectionBluePrint) {
			addFieldToClass(typeSpec, (CollectionBluePrint) collectionBP, signature);
		} else if (collectionBP instanceof MapBluePrint) {
			addFieldToClass(typeSpec, (MapBluePrint) collectionBP, signature);
		}
	}

	private void addFieldToClass(TypeSpec.Builder typeSpec, CollectionBluePrint collection, SignatureType signature) {
		TypeName collectionType;

		if (signature != null) {
			collectionType = TestGenerationHelper.getParameterizedTypeName(signature);
		} else {
			collectionType = ParameterizedTypeName.get(collection.getInterfaceClass(), Object.class);
		}

		typeSpec.addField(collectionType, namingService.getName(collection), Modifier.PRIVATE);
	}

	private void addFieldToClass(TypeSpec.Builder typeSpec, MapBluePrint map, SignatureType signature) {
		TypeName mapType;

		if (signature != null) {
			mapType = TestGenerationHelper.getParameterizedTypeName(signature);
		} else {
			mapType = ParameterizedTypeName.get(map.getInterfaceClass(), Object.class, Object.class);
		}

		typeSpec.addField(mapType, namingService.getName(map), Modifier.PRIVATE);
	}

	private void addCollectionToObject(Builder code, CollectionBluePrint collection, SetterMethodData setter,
			String objectName) {
		if (SetterType.COLLECTION_SETTER == setter.getType()) {
			for (BluePrint bluePrint : collection.getBluePrints()) {

				if (bluePrint.isComplexType()) {
					code.addStatement(
							objectName + "." + setter.getName() + "(" + namingService.getName(bluePrint) + ")");
				} else if (bluePrint.isSimpleBluePrint()) {
					SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

					code.addStatement(objectName + "." + setter.getName() + "(" + simpleBluePrint.valueCreation() + ")",
							simpleBluePrint.getReferenceClasses());
				}
			}

		} else if (SetterType.VALUE_SETTER == setter.getType()) {
			code.addStatement(objectName + "." + setter.getName() + "(" + namingService.getName(collection) + ")");
		} else if (SetterType.VALUE_GETTER == setter.getType()) {
			code.addStatement(
					objectName + "." + setter.getName() + "().addAll(" + namingService.getName(collection) + ")");
		}
	}

	private void addMapToObject(Builder code, MapBluePrint map, SetterMethodData setter, String objectName) {
		if (SetterType.COLLECTION_SETTER == setter.getType()) {
			for (Entry<BluePrint, BluePrint> entry : map.getBluePrints()) {
				StringBuilder statement = new StringBuilder();
				statement.append(objectName + "." + setter.getName() + "(");

				List<Class<?>> keyTypes = addElement(statement, entry.getKey(), ",");
				List<Class<?>> valueTypes = addElement(statement, entry.getValue(), ")");

				keyTypes.addAll(valueTypes);
				code.addStatement(statement.toString(), keyTypes.toArray());

			}
		} else if (SetterType.VALUE_SETTER == setter.getType()) {
			code.addStatement(objectName + "." + setter.getName() + "(" + namingService.getName(map) + ")");
		} else if (SetterType.VALUE_GETTER == setter.getType()) {
			code.addStatement(objectName + "." + setter.getName() + "().putAll(" + namingService.getName(map) + ")");
		}
	}

	private void createCollection(Builder code, CollectionBluePrint collection, SignatureType signature,
			boolean onlyCreateCollectionElements, boolean isField) {
		LOGGER.info("generating Collection for BluePrint: " + namingService.getName(collection));

		SignatureType genericType = signature != null ? signature.getSubTypes().get(0) : null;

		for (BluePrint bluePrint : collection.getBluePrints()) {
			createComplexContainerElement(code, bluePrint, //
					genericType != null && genericType.isSimpleSignature() ? null : genericType);
		}

		String collectionName = namingService.getName(collection);

		if (!onlyCreateCollectionElements) {
			if (isField) {
				code.addStatement(collectionName + GENERIC_CONSTRUCTOR, collection.getImplementationClass());
			} else {

				TypeName collectionType;
				if (signature != null) {
					collectionType = TestGenerationHelper.getParameterizedTypeName(signature);
				} else {
					collectionType = ParameterizedTypeName.get(collection.getInterfaceClass(), Object.class);
				}

				code.addStatement("$T " + collectionName + GENERIC_CONSTRUCTOR, collectionType,
						collection.getImplementationClass());
			}

			for (BluePrint bluePrint : collection.getBluePrints()) {
				if (bluePrint.isComplexType()) {
					code.addStatement(collectionName + ".add(" + namingService.getName(bluePrint) + ")");
				} else if (bluePrint.isSimpleBluePrint()) {
					SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

					code.addStatement(collectionName + ".add(" + simpleBluePrint.valueCreation() + ")",
							simpleBluePrint.getReferenceClasses());
				}
			}

			code.add("\n");
			collection.setBuild();
		}
	}

	private void createMap(Builder code, MapBluePrint map, SignatureType signature,
			boolean onlyCreateCollectionElements, boolean isField) {

		for (Entry<BluePrint, BluePrint> pair : map.getBluePrints()) {
			createComplexContainerElement(code, pair.getKey(),
					signature != null && !signature.getSubTypes().get(0).isSimpleSignature()
							? signature.getSubTypes().get(0)
							: null);
			createComplexContainerElement(code, pair.getValue(),
					signature != null && !signature.getSubTypes().get(1).isSimpleSignature()
							? signature.getSubTypes().get(1)
							: null);
		}

		if (!onlyCreateCollectionElements) {

			String mapName = namingService.getName(map);

			if (isField) {
				code.addStatement(mapName + GENERIC_CONSTRUCTOR, map.getImplementationClass());
			} else {
				TypeName mapType;

				if (signature != null) {
					mapType = TestGenerationHelper.getParameterizedTypeName(signature);
				} else {
					mapType = ParameterizedTypeName.get(Map.class, Object.class, Object.class);
				}

				code.addStatement("$T " + mapName + GENERIC_CONSTRUCTOR, mapType, map.getImplementationClass());
			}

			for (Entry<BluePrint, BluePrint> entry : map.getBluePrints()) {
				StringBuilder statement = new StringBuilder();
				statement.append(mapName + ".put(");

				List<Class<?>> keyTypes = addElement(statement, entry.getKey(), ",");
				List<Class<?>> valueTypes = addElement(statement, entry.getValue(), ")");

				keyTypes.addAll(valueTypes);
				code.addStatement(statement.toString(), keyTypes.toArray());
			}

			code.add("\n");
			map.setBuild();
		}
	}

	private List<Class<?>> addElement(StringBuilder statement, BluePrint bluePrint, String end) {
		if (bluePrint.isComplexType()) {
			statement.append(namingService.getName(bluePrint) + end);

			return Collections.emptyList();
		}

		if (bluePrint.isSimpleBluePrint()) {
			SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

			statement.append(simpleBluePrint.valueCreation() + end);

			return simpleBluePrint.getReferenceClasses();
		}

		throw new IllegalArgumentException(bluePrint + "is not a valid BluePrinttype");
	}

	private void createComplexContainerElement(Builder code, BluePrint bluePrint, SignatureType signature) {
		if (bluePrint.isComplexBluePrint() && bluePrint.isNotBuild()) {
			ClassData classData = TestGenerationHelper.getClassData(bluePrint.getReference());

			Set<FieldData> calledFields = Collections.emptySet();
			if (TestgeneratorConfig.traceReadFieldAccess()) {
				calledFields = TestGenerationHelper.getCalledFields(bluePrint.getReference());
			}

			objectGeneration.createObject(code, bluePrint.castToComplexBluePrint(),
					ValueStorage.getInstance().getMethodParameters().contains(bluePrint), classData, calledFields);

		} else if (bluePrint.isCollectionBluePrint() && bluePrint.isNotBuild()) {
			createCollection(code, bluePrint.castToCollectionBluePrint(), signature, //
					false, ValueStorage.getInstance().getMethodParameters().contains(bluePrint));
		} else if (bluePrint.isArrayBluePrint() && bluePrint.isNotBuild()) {
			arrayGeneration.createArray(code, bluePrint.castToArrayBluePrint(), false,
					ValueStorage.getInstance().getMethodParameters().contains(bluePrint));

		}
	}

}
