package org.testgen.runtime.generation;

import java.util.Set;

import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.generation.naming.NamingService;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;

import com.squareup.javapoet.CodeBlock.Builder;

public interface ComplexObjectGeneration {
	/* init */
	void setContainerGeneration(ContainerGeneration containerGeneration);

	void setNamingService(NamingService namingService);

	void createObject(Builder builder, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields);

}
