package org.testgen.runtime.generation.api.naming;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;

/**
 * Generates the name for fields and localVariables in a Class depending on the
 * delivered {@link BluePrint}. If the name of the BluePrint is already in use
 * the generated name is the original name with a addition.
 *
 * @param <E> Type of the CodeBlock
 */
public interface NamingService<E> {

	/**
	 * Generates a field name for the delivered {@link BluePrint}. If the BluePrint
	 * has already a name this name will be returned.
	 * 
	 * @param bluePrint
	 * @return name of the Field
	 */
	String getFieldName(BluePrint bluePrint);

	/**
	 * Checks that the delivered BluePrint exists a name. For this context it's
	 * irrelevant if the name is a original or not.
	 * 
	 * @param bluePrint
	 * @return
	 */
	boolean existsField(BluePrint bluePrint);

	/**
	 * Deletes all currently stored field names
	 */
	void clearFields();

	/**
	 * Generates the name of a local variable for the delivered {@link BluePrint}.
	 * If the bluePrint has already a name this name will be returned.
	 * 
	 * @param statementTree codeBlock where the local variable will be included
	 * @param bluePrint
	 * @return name of the blueprint
	 */
	String getLocalName(E statementTree, BluePrint bluePrint);

	/**
	 * Checks that the delivered BluePrint exists a name. For this context it's
	 * irrelevant is a original or not.
	 * 
	 * @param statementTree codeBlock where the local variable will be included
	 * @param bluePrint
	 * @return
	 */
	boolean existsLocal(E statementTree, BluePrint bluePrint);

}
