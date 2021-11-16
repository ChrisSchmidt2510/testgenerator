package org.testgen.runtime.valuetracker.blueprint.factories;

import java.util.HashSet;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

public class BluePrintsFactory {

	private Set<BluePrintFactory> factories = new HashSet<>();

	public BluePrintsFactory() {
		ServiceLoader<BluePrintFactory> serviceLoader = ServiceLoader.load(BluePrintFactory.class);
		serviceLoader.forEach(factories::add);
	}

	public Optional<BluePrintFactory> getBluePrintFactory(Object value) {
		return factories.stream().filter(factory -> factory.createBluePrintForType(value))
				.max((fac1, fac2) -> Integer.compare(fac1.getPriority(), fac2.getPriority()));
	}

}
