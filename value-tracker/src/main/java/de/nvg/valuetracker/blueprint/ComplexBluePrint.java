package de.nvg.valuetracker.blueprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.nvg.valuetracker.blueprint.simpletypes.SimpleBluePrintFactory;

public class ComplexBluePrint extends AbstractBasicBluePrint<Object> {

	private List<BluePrint> bluePrints = new ArrayList<>();

	public ComplexBluePrint(String fieldName, Object value) {
		super(fieldName, value);
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		return bluePrints.stream().filter(BluePrint::isComplexType).collect(Collectors.toList());
	}

	public BluePrint getBluePrintForName(String fieldName) {
		return bluePrints.stream().filter(bp -> fieldName.equals(bp.getName())).findAny()
				.orElse(SimpleBluePrintFactory.of(fieldName, null));
	}

	public void addBluePrint(BluePrint bluePrint) {
		bluePrints.add(bluePrint);
	}

	public List<BluePrint> getChildBluePrints() {
		return Collections.unmodifiableList(bluePrints);
	}

	public void resetBuildState() {
		if (build) {
			build = false;
			bluePrints.forEach(BluePrint::resetBuildState);
		}
	}

	@Override
	public String toString() {
		return value.getClass().getName() + " " + name;
	}

	@Override
	public boolean isComplexType() {
		return true;
	}
}
