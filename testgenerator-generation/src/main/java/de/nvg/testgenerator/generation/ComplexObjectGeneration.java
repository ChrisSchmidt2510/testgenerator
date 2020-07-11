package de.nvg.testgenerator.generation;

import java.util.Set;

import com.squareup.javapoet.CodeBlock.Builder;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.testgenerator.generation.naming.NamingService;
import de.nvg.valuetracker.blueprint.ComplexBluePrint;

public interface ComplexObjectGeneration {
	/* init */
	void setContainerGeneration(ContainerGeneration containerGeneration);

	void setNamingService(NamingService namingService);

	void createObject(Builder builder, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields);

}
