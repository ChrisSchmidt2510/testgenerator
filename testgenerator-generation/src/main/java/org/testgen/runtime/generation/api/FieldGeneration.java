package org.testgen.runtime.generation.api;

import org.testgen.runtime.classdata.model.descriptor.SignatureType;

public interface FieldGeneration<T, B> {

	/**
	 * Adds a field to a compilationUnit.
	 * 
	 * @param compilationUnit class where the field is added to
	 * @param bluePrint       for the field
	 * @param signature       signature of the field
	 */
	void createField(T compilationUnit, B bluePrint, SignatureType signature);

}
