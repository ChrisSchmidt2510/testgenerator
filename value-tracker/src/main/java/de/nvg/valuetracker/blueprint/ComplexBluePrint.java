package de.nvg.valuetracker.blueprint;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.nvg.valuetracker.blueprint.collections.CollectionBluePrint;

public class ComplexBluePrint extends BasicBluePrint<Object> {

	private List<BluePrint> bluePrints = new ArrayList<>();

	public ComplexBluePrint(String fieldName, Object value) {
		super(fieldName, value);
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		return bluePrints.stream()
				.filter(bp -> bp instanceof ComplexBluePrint || bp instanceof BasicCollectionBluePrint<?>)
				.collect(Collectors.toList());
	}

	public void addBluePrint(BluePrint bluePrint) {
		bluePrints.add(bluePrint);
	}

	@Override
	public String toString() {
		// TODO just temporary
		build = true;
		String complex = getPreExecuteBluePrints().stream().map(el -> !el.isBuild() ? el.toString() : "")
				.collect(Collectors.joining());

		String toString = "";
		for (BluePrint bluePrint : bluePrints) {
			if (bluePrint instanceof SimpleBluePrint<?>) {
				toString = toString + bluePrint.toString() + "\n";
			} else if (bluePrint instanceof ComplexBluePrint || bluePrint instanceof CollectionBluePrint) {
				toString = toString + "Field: ref " + bluePrint.getName() + "\n";
			}
		}

		return complex + "\n" + getName() + "\n" + toString;
	}
}
