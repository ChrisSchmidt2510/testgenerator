package de.nvg.proxy;

import java.lang.ref.WeakReference;
import java.util.Set;

import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.testgenerator.MethodHandles;
import de.nvg.testgenerator.TestgeneratorConstants;
import de.nvg.testgenerator.properties.RuntimeProperties;

public abstract class AbstractProxy {
	private final FieldData field;
	private WeakReference<? extends Object> parent;

	public AbstractProxy(Object parent, String fieldName, Class<?> fieldDataType) {
		this.field = new FieldData(fieldName, fieldDataType);
		this.parent = new WeakReference<>(parent);
	}

	protected void trackReadFieldCalls() {
		if (RuntimeProperties.getInstance().isTrackingActive()) {
			Set<FieldData> calledFields = MethodHandles.getFieldValue(parent.get(),
					TestgeneratorConstants.FIELDNAME_CALLED_FIELDS);
			calledFields.add(field);
		}
	}

	public Class<?> getDataType() {
		return field.getDescriptor();
	}

}
