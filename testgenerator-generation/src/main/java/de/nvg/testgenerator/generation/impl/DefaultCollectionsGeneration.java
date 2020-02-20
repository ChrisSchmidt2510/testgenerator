package de.nvg.testgenerator.generation.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.runtime.classdatamodel.SetterType;
import de.nvg.testgenerator.generation.CollectionsGeneration;
import de.nvg.testgenerator.generation.ComplexObjectGeneration;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import de.nvg.valuetracker.blueprint.BasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.SimpleBluePrint;
import de.nvg.valuetracker.blueprint.collections.CollectionBluePrint;
import de.nvg.valuetracker.blueprint.collections.MapBluePrint;

public class DefaultCollectionsGeneration implements CollectionsGeneration {

	private static final Logger LOGGER = LogManager.getLogger(DefaultCollectionsGeneration.class);

	private ComplexObjectGeneration objectGeneration;

	@Override
	public void setComplexObjectGeneration(ComplexObjectGeneration objectGeneration) {
		this.objectGeneration = objectGeneration;
	}

	@Override
	public void createCollection(Builder code, BasicCollectionBluePrint<?> basicCollectionBP,
			boolean onlyCreateCollectionElements, boolean isField) {

		LOGGER.info("starting generation of Collection :" + basicCollectionBP);

		if (basicCollectionBP.isNotBuild()) {
			if (basicCollectionBP instanceof CollectionBluePrint) {
				createCollection(code, (CollectionBluePrint) basicCollectionBP, onlyCreateCollectionElements, isField);

			} else if (basicCollectionBP instanceof MapBluePrint) {
				createMap(code, (MapBluePrint) basicCollectionBP, onlyCreateCollectionElements, isField);
			}
		}

	}

	@Override
	public void addCollectionToObject(Builder code, BasicCollectionBluePrint<?> basicCollectionBP,
			SetterMethodData setter, String objectName) {

		LOGGER.info("add Collection " + basicCollectionBP + " to Object " + objectName);

		if (basicCollectionBP instanceof CollectionBluePrint) {
			addCollectionToObject(code, (CollectionBluePrint) basicCollectionBP, setter, objectName);

		} else if (basicCollectionBP instanceof MapBluePrint) {
			addMapToObject(code, (MapBluePrint) basicCollectionBP, setter, objectName);
		}
	}

	@Override
	public void addFieldToClass(TypeSpec.Builder typeSpec, BasicCollectionBluePrint<?> bluePrint) {

		if (bluePrint instanceof CollectionBluePrint) {
			addFieldToClass(typeSpec, (CollectionBluePrint) bluePrint);

		} else if (bluePrint instanceof MapBluePrint) {
			addFieldToClass(typeSpec, (MapBluePrint) bluePrint);
		}
	}

	private void addFieldToClass(TypeSpec.Builder typeSpec, CollectionBluePrint collection) {
		ParameterizedTypeName type = ParameterizedTypeName.get(collection.getInterfaceClass(),
				collection.getElementClass());

		typeSpec.addField(type, collection.getName(), Modifier.PRIVATE);
	}

	private void addFieldToClass(TypeSpec.Builder typeSpec, MapBluePrint map) {
		ParameterizedTypeName type = ParameterizedTypeName.get(map.getInterfaceClass(), map.getKeyType(),
				map.getValueType());

		typeSpec.addField(type, map.getName(), Modifier.PRIVATE);
	}

	private void addCollectionToObject(Builder code, CollectionBluePrint collection, SetterMethodData setter,
			String objectName) {
		if (SetterType.COLLECTION_SETTER == setter.getType()) {
			for (BluePrint bluePrint : collection.getBluePrints()) {

				if (bluePrint.isComplexType()) {
					code.addStatement(objectName + "." + setter.getName() + "(" + bluePrint.getName() + ")");
				} else if (bluePrint.isSimpleBluePrint()) {
					SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

					code.addStatement(objectName + "." + setter.getName() + "(" + simpleBluePrint.valueCreation() + ")",
							simpleBluePrint.getReferenceClasses());
				}
			}

		} else if (SetterType.VALUE_SETTER == setter.getType()) {
			code.addStatement(objectName + "." + setter.getName() + "(" + collection.getName() + ")");
		} else if (SetterType.VALUE_GETTER == setter.getType()) {
			code.addStatement(objectName + "." + setter.getName() + "().addAll(" + collection.getName() + ")");
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
			code.addStatement(objectName + "." + setter.getName() + "(" + map.getName() + ")");
		} else if (SetterType.VALUE_GETTER == setter.getType()) {
			code.addStatement(objectName + "." + setter.getName() + "().putAll(" + map.getName() + ")");
		}
	}

	private void createCollection(Builder code, CollectionBluePrint collection, boolean onlyCreateCollectionElements,
			boolean isField) {
		LOGGER.info("generating Collection for BluePrint: " + collection.getName());

		for (BluePrint bluePrint : collection.getBluePrints()) {
			createComplexCollectionElement(code, bluePrint);
		}

		if (!onlyCreateCollectionElements) {
			if (isField) {
				code.addStatement(collection.getName() + " = new $T<>()", collection.getImplementationClass());
			} else {
				ParameterizedTypeName collectionType = ParameterizedTypeName.get(collection.getInterfaceClass(),
						collection.getElementClass());

				code.addStatement("$T " + collection.getName() + " = new $T<>()", collectionType,
						collection.getImplementationClass());
			}

			for (BluePrint bluePrint : collection.getBluePrints()) {
				if (bluePrint.isComplexType()) {
					code.addStatement(collection.getName() + ".add(" + bluePrint.getName() + ")");
				} else if (bluePrint.isSimpleBluePrint()) {
					SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

					code.addStatement(collection.getName() + ".add(" + simpleBluePrint.valueCreation() + ")",
							simpleBluePrint.getReferenceClasses());
				}
			}

			code.add("\n");
			collection.setBuild();
		}
	}

	private void createMap(Builder code, MapBluePrint map, boolean onlyCreateCollectionElements, boolean isField) {
		for (Entry<BluePrint, BluePrint> pair : map.getBluePrints()) {
			createComplexCollectionElement(code, pair.getKey());
			createComplexCollectionElement(code, pair.getValue());
		}

		if (!onlyCreateCollectionElements) {

			if (isField) {
				code.addStatement(map.getName() + " = new $T<>()", map.getImplementationClass());
			} else {
				ParameterizedTypeName mapType = ParameterizedTypeName.get(Map.class, map.getKeyType(),
						map.getValueType());

				code.addStatement("$T " + map.getName() + " = new $T<>()", mapType, map.getImplementationClass());
			}

			for (Entry<BluePrint, BluePrint> entry : map.getBluePrints()) {
				StringBuilder statement = new StringBuilder();
				statement.append(map.getName() + ".put(");

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
			statement.append(bluePrint.getName() + end);

			return Collections.emptyList();
		}

		if (bluePrint.isSimpleBluePrint()) {
			SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

			statement.append(simpleBluePrint.valueCreation() + end);

			return simpleBluePrint.getReferenceClasses();
		}

		throw new IllegalArgumentException(bluePrint + "is not a valid BluePrinttype");
	}

	private void createComplexCollectionElement(Builder code, BluePrint bluePrint) {
		if (bluePrint.isComplexBluePrint() && bluePrint.isNotBuild()) {
			ClassData classData = TestGenerationHelper.getClassData(bluePrint.getReference());
			Set<FieldData> calledFields = TestGenerationHelper.getCalledFields(bluePrint.getReference());

			objectGeneration.createObject(code, bluePrint.castToComplexBluePrint(), false, classData, calledFields);

		} else if (bluePrint.isCollectionBluePrint() && bluePrint.isNotBuild()) {
			createCollection(code, bluePrint.castToCollectionBluePrint(), false, false);
		}
	}

}
