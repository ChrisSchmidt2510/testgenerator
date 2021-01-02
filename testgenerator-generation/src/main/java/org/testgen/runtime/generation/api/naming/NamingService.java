package org.testgen.runtime.generation.api.naming;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;

public interface NamingService<E> {

	String getFieldName(BluePrint bluePrint);

	String getLocalName(E statementTree, BluePrint bluePrint);

	void clearFieldNames();

}
