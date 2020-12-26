package org.testgen.runtime.generation;

import org.testgen.runtime.classdata.model.descriptor.SignatureType;

public interface FieldGeneration<T, B> {

	void createField(T compilationUnit, B bluePrint, SignatureType signature);

}
