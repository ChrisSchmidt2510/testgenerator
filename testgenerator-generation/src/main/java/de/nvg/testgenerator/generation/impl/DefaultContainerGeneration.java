package de.nvg.testgenerator.generation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.lang.model.element.Modifier;

import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.core.properties.RuntimeProperties;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.runtime.classdatamodel.SetterType;
import de.nvg.runtime.classdatamodel.SignatureData;
import de.nvg.testgenerator.generation.ComplexObjectGeneration;
import de.nvg.testgenerator.generation.ContainerGeneration;
import de.nvg.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.ArrayBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.SimpleBluePrint;
import de.nvg.valuetracker.blueprint.collections.CollectionBluePrint;
import de.nvg.valuetracker.blueprint.collections.MapBluePrint;
import de.nvg.valuetracker.storage.ValueStorage;

public class DefaultContainerGeneration implements ContainerGeneration {

	private static final Logger LOGGER = LogManager.getLogger(DefaultContainerGeneration.class);

	private static final String GENERIC_CONSTRUCTOR = " = new $T<>()";

	private ComplexObjectGeneration objectGeneration;

	private RuntimeProperties properties = RuntimeProperties.getInstance();

	@Override
	public void setComplexObjectGeneration(ComplexObjectGeneration objectGeneration) {
		this.objectGeneration = objectGeneration;
	}

	@Override
	public void createCollection(Builder code, AbstractBasicCollectionBluePrint<?> basicCollectionBP, //
			SignatureData signature, boolean onlyCreateCollectionElements, boolean isField) {

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
	public void createArray(Builder code, ArrayBluePrint array, boolean onlyCreateElements, boolean isField) {
		LOGGER.info("starting generation of Array: " + array);

		if (array.isNotBuild()) {

			for (BluePrint bluePrint : array.getElements()) {
				createComplexContainerElement(code, bluePrint, null);
			}

			if (!onlyCreateElements) {
				if (isField) {
					StringBuilder arrayCreation = new StringBuilder(array.getName() + " =");
					createArrayConstructor(array, arrayCreation, true);
					code.addStatement(arrayCreation.toString(), array.getBaseType());

				} else {

					StringBuilder arrayCreation = new StringBuilder("$T " + array.getName() + " =");
					createArrayConstructor(array, arrayCreation, false);

					List<Class<?>> types = new ArrayList<>();
					types.add(array.getType());
					types.add(array.getBaseType());

					String[] arrayElements = new String[array.size()];

					BluePrint[] elements = array.getElements();

					for (int i = 0; i < elements.length; i++) {
						BluePrint bluePrint = elements[i];

						if (bluePrint.isComplexType()) {
							arrayElements[i] = bluePrint.getName();
						} else if (bluePrint.isSimpleBluePrint()) {
							SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
							arrayElements[i] = simpleBluePrint.valueCreation();
							types.addAll(simpleBluePrint.getReferenceClasses());
						}
					}

					arrayCreation.append("{" + String.join(", ", arrayElements) + "}");

					code.addStatement(arrayCreation.toString(), types.toArray());
				}
			}
		}

		array.setBuild();
	}

	private void createArrayConstructor(ArrayBluePrint array, StringBuilder code, boolean withDimensionInit) {
		code.append(" new $T");

		int dimensions = array.getDimensions();

		if (withDimensionInit) {
			code.append("[" + array.size() + "]");
			dimensions--;
		}

		while (dimensions > 0) {
			code.append("[]");
			dimensions--;
		}
	}

	@Override
	public void addContainerToObject(Builder code, BluePrint containerBP, SetterMethodData setter, String objectName) {

		LOGGER.info("add Collection " + containerBP + " to Object " + objectName);

		if (containerBP instanceof CollectionBluePrint) {
			addCollectionToObject(code, (CollectionBluePrint) containerBP, setter, objectName);
		} else if (containerBP instanceof MapBluePrint) {
			addMapToObject(code, (MapBluePrint) containerBP, setter, objectName);
		} else if (containerBP instanceof ArrayBluePrint) {
			addArrayToObject(code, (ArrayBluePrint) containerBP, setter, objectName);
		}

	}

	@Override
	public void addContainerToObject(Builder code, BluePrint containerBP, FieldData field, String objectName) {

		LOGGER.info("add Collection " + containerBP + " to Object " + objectName);

		code.addStatement(objectName + "." + field.getName() + "=" + containerBP.getName());

	}

	@Override
	public void addFieldToClass(TypeSpec.Builder typeSpec, BluePrint containerBP, SignatureData signature) {

		if (containerBP instanceof CollectionBluePrint) {
			addFieldToClass(typeSpec, (CollectionBluePrint) containerBP, signature);
		} else if (containerBP instanceof MapBluePrint) {
			addFieldToClass(typeSpec, (MapBluePrint) containerBP, signature);
		} else if (containerBP instanceof ArrayBluePrint) {
			addFieldToClass(typeSpec, (ArrayBluePrint) containerBP);
		}
	}

	private void addFieldToClass(TypeSpec.Builder typeSpec, CollectionBluePrint collection, SignatureData signature) {
		TypeName collectionType;

		if (signature != null) {
			collectionType = getParameterizedTypeName(signature);
		} else {
			collectionType = ParameterizedTypeName.get(collection.getInterfaceClass(), Object.class);
		}

		typeSpec.addField(collectionType, collection.getName(), Modifier.PRIVATE);
	}

	private void addFieldToClass(TypeSpec.Builder typeSpec, MapBluePrint map, SignatureData signature) {
		TypeName mapType;

		if (signature != null) {
			mapType = getParameterizedTypeName(signature);
		} else {
			mapType = ParameterizedTypeName.get(map.getInterfaceClass(), Object.class, Object.class);
		}

		typeSpec.addField(mapType, map.getName(), Modifier.PRIVATE);
	}

	private void addFieldToClass(TypeSpec.Builder typeSpec, ArrayBluePrint array) {
		typeSpec.addField(array.getType(), array.getName(), Modifier.PRIVATE);
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

	private void createCollection(Builder code, CollectionBluePrint collection, SignatureData signature,
			boolean onlyCreateCollectionElements, boolean isField) {
		LOGGER.info("generating Collection for BluePrint: " + collection.getName());

		SignatureData genericType = signature != null ? signature.getSubTypes().get(0) : null;

		for (BluePrint bluePrint : collection.getBluePrints()) {
			createComplexContainerElement(code, bluePrint, //
					genericType != null && genericType.isSimpleSignature() ? null : genericType);
		}

		if (!onlyCreateCollectionElements) {
			if (isField) {
				code.addStatement(collection.getName() + GENERIC_CONSTRUCTOR, collection.getImplementationClass());
			} else {

				TypeName collectionType;
				if (signature != null) {
					collectionType = getParameterizedTypeName(signature);
				} else {
					collectionType = ParameterizedTypeName.get(collection.getInterfaceClass(), Object.class);
				}

				code.addStatement("$T " + collection.getName() + GENERIC_CONSTRUCTOR, collectionType,
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

	private void createMap(Builder code, MapBluePrint map, SignatureData signature,
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

			if (isField) {
				code.addStatement(map.getName() + GENERIC_CONSTRUCTOR, map.getImplementationClass());
			} else {
				TypeName mapType;

				if (signature != null) {
					mapType = getParameterizedTypeName(signature);
				} else {
					mapType = ParameterizedTypeName.get(Map.class, Object.class, Object.class);
				}

				code.addStatement("$T " + map.getName() + GENERIC_CONSTRUCTOR, mapType, map.getImplementationClass());
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

	TypeName getParameterizedTypeName(SignatureData signature) {

		if (signature.isSimpleSignature()) {
			return TypeName.get(signature.getType());

		} else {
			TypeName[] subTypes = new TypeName[signature.getSubTypes().size()];

			for (int i = 0; i < signature.getSubTypes().size(); i++) {
				SignatureData subSignature = signature.getSubTypes().get(i);

				subTypes[i] = getParameterizedTypeName(subSignature);
			}

			return ParameterizedTypeName.get(ClassName.get(signature.getType()), subTypes);
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

	private void createComplexContainerElement(Builder code, BluePrint bluePrint, SignatureData signature) {
		if (bluePrint.isComplexBluePrint() && bluePrint.isNotBuild()) {
			ClassData classData = TestGenerationHelper.getClassData(bluePrint.getReference());

			Set<FieldData> calledFields = Collections.emptySet();
			if (properties.wasFieldTrackingActivated()) {
				calledFields = TestGenerationHelper.getCalledFields(bluePrint.getReference());
			}

			objectGeneration.createObject(code, bluePrint.castToComplexBluePrint(),
					ValueStorage.getInstance().getMethodParameters().contains(bluePrint), classData, calledFields);

		} else if (bluePrint.isCollectionBluePrint() && bluePrint.isNotBuild()) {
			createCollection(code, bluePrint.castToCollectionBluePrint(), signature, //
					false, ValueStorage.getInstance().getMethodParameters().contains(bluePrint));
		} else if (bluePrint.isArrayBluePrint() && bluePrint.isNotBuild()) {
			createArray(code, bluePrint.castToArrayBluePrint(), false,
					ValueStorage.getInstance().getMethodParameters().contains(bluePrint));
		}
	}

	private void addArrayToObject(Builder code, ArrayBluePrint array, SetterMethodData setter, String objectName) {
		if (SetterType.VALUE_GETTER == setter.getType()) {
			BluePrint[] elements = array.getElements();
			for (int i = 0; i < elements.length; i++) {
				BluePrint bluePrint = elements[i];

				if (bluePrint.isComplexType()) {
					code.addStatement(objectName + "." + setter.getName() + "()[" + i + "] = " + bluePrint.getName());
				} else if (bluePrint.isSimpleBluePrint()) {
					SimpleBluePrint<?> simpleBp = bluePrint.castToSimpleBluePrint();

					code.addStatement(
							objectName + "." + setter.getName() + "()[" + i + "] = " + simpleBp.valueCreation(),
							simpleBp.getReferenceClasses().toArray());
				}
			}

		} else if (SetterType.VALUE_SETTER == setter.getType()) {
			code.addStatement(objectName + "." + setter.getName() + "(" + array.getName() + ")");
		}
	}

}
