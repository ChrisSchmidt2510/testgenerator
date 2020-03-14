package de.nvg.proxy;

import java.lang.ref.WeakReference;
import java.util.Set;

import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.testgenerator.MethodHandles;
import de.nvg.testgenerator.TestgeneratorConstants;
import de.nvg.testgenerator.properties.RuntimeProperties;

public abstract class Proxy {
	private final FieldData field;
	private WeakReference<? extends Object> parent;

	public Proxy(Object parent, String fieldName, String fieldDataType) {
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

	public String getDataType() {
		return field.getDescriptor();
	}

}
