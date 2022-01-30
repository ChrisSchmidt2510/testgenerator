package org.testgen.agent.classdata.testclasses;

import java.io.Serializable;

public class SerializationHelper {

	public interface Test extends Serializable {

	}

	public class A implements Test {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1232271509775484127L;
	}

}
