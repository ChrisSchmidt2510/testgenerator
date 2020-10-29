package org.testgen.runtime.proxy;

import java.util.Set;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.MethodHandles;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.runtime.classdata.model.FieldData;

public abstract class AbstractProxy {
	private final FieldData field;
	private final Object parent;

	public AbstractProxy(Object parent, String fieldName, Class<?> fieldDataType) {
		this.field = new FieldData(fieldName, fieldDataType);
		this.parent = parent;
	}

	protected void trackReadFieldCalls() {
		if (TestgeneratorConfig.isFieldTrackingActivated() && !TestgeneratorConfig.isProxyFieldTrackingActivated()) {
			Set<FieldData> calledFields = MethodHandles.getFieldValue(parent,
					TestgeneratorConstants.FIELDNAME_CALLED_FIELDS);
			calledFields.add(field);
		}
	}

	public Class<?> getDataType() {
		return field.getDescriptor();
	}

}
