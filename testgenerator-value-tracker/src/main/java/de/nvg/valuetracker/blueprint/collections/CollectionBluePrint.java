package de.nvg.valuetracker.blueprint.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.nvg.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;

public class CollectionBluePrint extends AbstractBasicCollectionBluePrint<Collection<?>> {

	private List<BluePrint> elementBluePrints = new ArrayList<>();

	public CollectionBluePrint(String name, Collection<?> value, Class<?> interfaceClass) {
		super(name, value, interfaceClass);
	}

	public void addBluePrint(BluePrint bluePrint) {
		elementBluePrints.add(bluePrint);
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		return elementBluePrints.stream().filter(BluePrint::isComplexType).collect(Collectors.toList());
	}

	public void resetBuildState() {
		if (build) {
			build = false;
			elementBluePrints.forEach(BluePrint::resetBuildState);
		}
	}

	public List<BluePrint> getBluePrints() {
		return Collections.unmodifiableList(elementBluePrints);
	}

}
