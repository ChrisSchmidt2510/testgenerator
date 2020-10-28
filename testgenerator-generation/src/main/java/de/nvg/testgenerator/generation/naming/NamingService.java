package de.nvg.testgenerator.generation.naming;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;

public class NamingService {
	private List<Name> names = new ArrayList<>();

	public String getName(BluePrint bluePrint) {
		String requestedName = bluePrint.getName();

		Optional<Name> baseNameOptional = names.stream()//
				.filter(name -> name.getBaseName().equals(requestedName)).findAny();

		if (baseNameOptional.isPresent()) {
			Name baseName = baseNameOptional.get();

			if (baseName.baseName == bluePrint)
				return bluePrint.getName();

			return baseNameOptional.get().getName(bluePrint);
		} else {
			Name name = new Name(bluePrint);
			names.add(name);
			return name.getBaseName();
		}
	}

	public static class Name {
		final BluePrint baseName;
		final List<BluePrint> sharedNames = new ArrayList<>();

		public Name(BluePrint baseName) {
			this.baseName = baseName;
		}

		public String getBaseName() {
			return baseName.getName();
		}

		public String getName(BluePrint bluePrint) {
			int length = sharedNames.size();

			for (int i = 0; i < length; i++) {
				BluePrint name = sharedNames.get(i);
				if (name == bluePrint) {
					return name.getName() + (i + 1);
				}
			}

			String name = bluePrint.getName() + (length + 1);
			sharedNames.add(bluePrint);

			return name;
		}
	}
}
