package org.testgen.compiler.adapter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

import org.testgen.compiler.adapter.AbstractAdapter;
import org.testgen.compiler.classdata.BasicClassData;
import org.testgen.compiler.classdata.FieldData;
import org.testgen.compiler.classdata.FieldData.Builder;
import org.testgen.compiler.classdata.InnerClassData;
import org.testgen.compiler.classdata.SignatureData;
import org.testgen.compiler.constants.ProxyConstants;
import org.testgen.compiler.tree.TreeHelper;
import org.testgen.compiler.util.ClassUtils;
import org.testgen.compiler.util.JDKClasses;
import org.testgen.compiler.util.Utils;
import org.testgen.core.MapBuilder;

import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.ListBuffer;

public class FieldAdapter extends AbstractAdapter<VariableTree> {
	private static final Map<TypeKind, String> PROXY_MAPPER = //
			MapBuilder.<TypeKind, String>hashMapBuilder()//
					.add(TypeKind.BOOLEAN, ProxyConstants.BOOLEAN_PROXY)//
					.add(TypeKind.BYTE, ProxyConstants.INTEGER_PROXY)//
					.add(TypeKind.SHORT, ProxyConstants.INTEGER_PROXY)//
					.add(TypeKind.CHAR, ProxyConstants.INTEGER_PROXY)//
					.add(TypeKind.INT, ProxyConstants.INTEGER_PROXY)//
					.add(TypeKind.FLOAT, ProxyConstants.FLOAT_PROXY)//
					.add(TypeKind.DOUBLE, ProxyConstants.DOUBLE_PROXY)//
					.add(TypeKind.LONG, ProxyConstants.LONG_PROXY)//
					.toUnmodifiableMap();

	private static final Map<TypeKind, TypeTag> TYPE_MAPPER = //
			MapBuilder.<TypeKind, TypeTag>hashMapBuilder()//
					.add(TypeKind.BOOLEAN, TypeTag.BOOLEAN)//
					.add(TypeKind.BYTE, TypeTag.BYTE)//
					.add(TypeKind.SHORT, TypeTag.SHORT)//
					.add(TypeKind.CHAR, TypeTag.CHAR)//
					.add(TypeKind.INT, TypeTag.INT)//
					.add(TypeKind.FLOAT, TypeTag.FLOAT)//
					.add(TypeKind.DOUBLE, TypeTag.DOUBLE)//
					.add(TypeKind.LONG, TypeTag.LONG)//
					.toUnmodifiableMap();

	private final List<String> imports;

	public FieldAdapter(BasicClassData classData, List<String> imports) {
		super(classData);
		this.imports = imports;
	}

	@Override
	public void visit(VariableTree node) {
		System.out.println("FieldTree");
		System.out.println(node);
		FieldData field = analyse(node);

		if (field != null) {
			modify((JCVariableDecl) node, field);
			System.out.println(node);
		}

	}

	private FieldData analyse(VariableTree field) {
		Set<Modifier> modifiers = field.getModifiers().getFlags();

		boolean isPublic = modifiers.contains(Modifier.PUBLIC);
		boolean isFinal = modifiers.contains(Modifier.FINAL);
		boolean isStatic = modifiers.contains(Modifier.STATIC);

		// ignore Constants
		if (!isPublic && !isFinal && !isStatic) {
			Builder fieldBuilder = new FieldData.Builder().withName(Utils.cast(field.getName()))//
					.isPublic(isPublic).isMutable(!isFinal).isStatic(isStatic);

			if (Kind.PARAMETERIZED_TYPE == field.getType().getKind()) {
				ParameterizedTypeTree parameterizedType = (ParameterizedTypeTree) field.getType();
				fieldBuilder.withDataType(getQualifiedDataType(parameterizedType.getType().toString()))
						.withSignature(analyseGenericTypes(parameterizedType));
			} else if (Kind.PRIMITIVE_TYPE == field.getType().getKind()) {
				PrimitiveTypeTree primitiveType = (PrimitiveTypeTree) field.getType();
				fieldBuilder.withPrimitiveDataType(primitiveType.getPrimitiveTypeKind());
			} else {
				fieldBuilder.withDataType(getQualifiedDataType(field.getType().toString()));
			}

			FieldData fieldData = fieldBuilder.build();

			classData.addField(fieldData);

			return fieldData;
		}

		return null;
	}

	private void modify(JCVariableDecl field, FieldData fieldData) {

		if (fieldData.isPrimitive()) {
			JCFieldAccess type = TreeHelper.createType(PROXY_MAPPER.get(fieldData.getPrimitiveDataType()));
			field.vartype = type;

			createConstructorForProxy(field, fieldData, type);
		} else {
			JCFieldAccess baseType = TreeHelper.createType(ProxyConstants.REFERENCE_PROXY);
			com.sun.tools.javac.util.List<JCExpression> arguments = com.sun.tools.javac.util.List.of(//
					TreeHelper.createType(fieldData.getDataType()));

			field.vartype = treeMaker.TypeApply(baseType, arguments);

			createConstructorForProxy(field, fieldData,
					treeMaker.TypeApply(baseType, com.sun.tools.javac.util.List.nil()));
		}

	}

	private void createConstructorForProxy(JCVariableDecl field, FieldData fieldData, JCExpression type) {
		ListBuffer<JCExpression> args = new ListBuffer<>();
		if (field.init != null) {
			args.add(field.init);
		}
		args.add(treeMaker.Ident(symbolTable._this));
		args.add(treeMaker.Literal(fieldData.getName().toString()));
		args.add(treeMaker
				.Select(fieldData.isPrimitive() ? treeMaker.TypeIdent(TYPE_MAPPER.get(fieldData.getPrimitiveDataType()))
						: TreeHelper.createType(fieldData.getDataType()), symbolTable.fromString("class")));

		field.init = treeMaker.NewClass(null, null, type, args.toList(), null);
	}

	private String getQualifiedDataType(String dataType) {
		Optional<String> importDataType = imports.stream()
				.filter(type -> ClassUtils.removePackageFromClass(type).equals(dataType)).findAny();

		if (importDataType.isPresent()) {
			return importDataType.get();
		} else {
			Optional<String> jdkType = JDKClasses.getInstance().getQualifiedName(dataType);

			if (jdkType.isPresent()) {
				return jdkType.get();
			} else {
				Optional<InnerClassData> innerClassOptional = classData.getInnerClasses().stream()
						.filter(type -> type.getName().equals(dataType)).findAny();

				if (innerClassOptional.isPresent()) {
					InnerClassData innerClass = innerClassOptional.get();
					return innerClass.getPackageName() + "." + innerClass.getName();
				} else {
					return classData.getPackageName() + "." + dataType;
				}
			}
		}
	}

	private SignatureData analyseGenericTypes(ParameterizedTypeTree parameterizedType) {
		List<SignatureData> childs = new ArrayList<>();

		for (Tree child : parameterizedType.getTypeArguments()) {
			if (Kind.PARAMETERIZED_TYPE == child.getKind()) {
				childs.add(analyseGenericTypes((ParameterizedTypeTree) child));
			} else {
				childs.add(new SignatureData(child.toString()));
			}
		}

		SignatureData parent = new SignatureData(parameterizedType.getType().toString());
		parent.addSubTypes(childs);

		return parent;
	}

}
