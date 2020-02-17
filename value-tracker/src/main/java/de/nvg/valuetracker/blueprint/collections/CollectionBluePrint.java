package de.nvg.valuetracker.blueprint.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.nvg.valuetracker.blueprint.BasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ComplexBluePrint;
import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class CollectionBluePrint extends BasicCollectionBluePrint<Collection<?>> {

	private List<BluePrint> elementBluePrints = new ArrayList<>();

	public CollectionBluePrint(String name, Collection<?> value, Class<?> interfaceClassName) {
		super(name, value, interfaceClassName);
	}

	public void addBluePrint(BluePrint bluePrint) {
		elementBluePrints.add(bluePrint);
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		return elementBluePrints.stream().filter(BluePrint::isComplexType).collect(Collectors.toList());
	}

	public List<BluePrint> getBluePrints() {
		return Collections.unmodifiableList(elementBluePrints);
	}

	@Override
	public String toString() {
		// TODO just temporary
		String complex = getPreExecuteBluePrints().stream().map(el -> !el.isNotBuild() ? el.toString() : "")
				.collect(Collectors.joining());

		String toString = "";
		for (BluePrint bluePrint : elementBluePrints) {
			if (bluePrint instanceof SimpleBluePrint<?>) {
				toString = toString + bluePrint.toString() + "\n";
			} else if (bluePrint instanceof ComplexBluePrint || bluePrint instanceof CollectionBluePrint) {
				toString = toString + "Field: ref " + bluePrint.getName() + "\n";
			}
		}

		return complex + "\n" + getName() + "\n" + toString;
	}

}
