package de.nvg.valuetracker.blueprint.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.nvg.valuetracker.blueprint.BasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;

public class CollectionBluePrint extends BasicCollectionBluePrint<Collection<?>> {

	private List<BluePrint> elementBluePrints = new ArrayList<>();
	private Class<?> elementType = Object.class;

	public CollectionBluePrint(String name, Collection<?> value, Class<?> interfaceClass) {
		super(name, value, interfaceClass);
	}

	public void addBluePrint(BluePrint bluePrint) {
		elementBluePrints.add(bluePrint);

		if (elementType != Object.class) {
			elementType = bluePrint.getReference().getClass();
		}
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		return elementBluePrints.stream().filter(BluePrint::isComplexType).collect(Collectors.toList());
	}

	public List<BluePrint> getBluePrints() {
		return Collections.unmodifiableList(elementBluePrints);
	}

	public Class<?> getElementClass() {
		return elementType;
	}

}
