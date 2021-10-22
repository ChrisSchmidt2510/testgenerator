package org.testgen.runtime.generation.javaparser.impl.spezial;

import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.LambdaExpressionBluePrint;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

public class LambaExpressionSpezialObjectGeneration extends BasicSpezialObjectGeneration<LambdaExpressionBluePrint>{

	@Override
	public boolean canGenerateBluePrint(BluePrint bluePrint) {
		return bluePrint instanceof LambdaExpressionBluePrint;
	}

	@Override
	public void createObject(BlockStmt codeBlock, LambdaExpressionBluePrint bluePrint, SignatureType signature,
			boolean isField) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, LambdaExpressionBluePrint bluePrint,
			SignatureType signature) {
		// TODO Auto-generated method stub
		
	}

}
