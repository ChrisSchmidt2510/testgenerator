package org.testgen.runtime.generation.javaparser.impl.collection;

import java.util.Map;

import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.MapBluePrint;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class MapGeneration extends DefaultCollectionGeneration {

	@Override
	public boolean canGenerateBluePrint(AbstractBasicCollectionBluePrint<?> bluePrint) {
		return bluePrint instanceof MapBluePrint && Map.class.equals(bluePrint.getInterfaceClass());
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, AbstractBasicCollectionBluePrint<?> bluePrint,
			SignatureType signature) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createCollection(BlockStmt statementTree, AbstractBasicCollectionBluePrint<?> bluePrint,
			SignatureType signature, boolean isField) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createComplexElements(BlockStmt statementTree, AbstractBasicCollectionBluePrint<?> bluePrint,
			SignatureType signature) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCollectionToObject(BlockStmt statementTree, AbstractBasicCollectionBluePrint<?> bluePrint,
			boolean isField, SetterMethodData setter, Expression accessExpr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCollectionToObject(BlockStmt statementTree, AbstractBasicCollectionBluePrint<?> bluePrint,
			boolean isField, FieldData field, Expression accessExpr) {
		// TODO Auto-generated method stub

	}

}
