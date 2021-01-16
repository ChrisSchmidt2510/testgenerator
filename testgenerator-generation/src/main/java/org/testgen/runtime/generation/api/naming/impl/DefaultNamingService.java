package org.testgen.runtime.generation.api.naming.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;

public class DefaultNamingService<E> implements NamingService<E> {

	private List<Name> fieldNames = new ArrayList<>();

	private Map<E, List<Name>> localNames = new HashMap<>();

	@Override
	public String getFieldName(BluePrint bluePrint) {
		return getName(bluePrint, fieldNames);
	}

	@Override
	public boolean existsField(BluePrint bluePrint) {
		return existsName(bluePrint, fieldNames);
	}

	@Override
	public void clearFields() {
		fieldNames.clear();
	}

	@Override
	public String getLocalName(E statementTree, BluePrint bluePrint) {
		if (localNames.containsKey(statementTree)) {
			List<Name> names = localNames.get(statementTree);

			return getName(bluePrint, names);
		} else {
			List<Name> names = new ArrayList<>();

			String name = getName(bluePrint, names);
			localNames.put(statementTree, names);

			return name;
		}
	}

	@Override
	public boolean existsLocal(E statementTree, BluePrint bluePrint) {
		if (localNames.containsKey(statementTree))
			return existsName(bluePrint, localNames.get(statementTree));

		return false;
	}

	private String getName(BluePrint bluePrint, List<Name> names) {
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

	private boolean existsName(BluePrint bluePrint, List<Name> names) {
		String requestedName = bluePrint.getName();

		Optional<Name> baseNameOptional = names.stream().filter(name -> name.getBaseName().equals(requestedName))
				.findAny();

		if (baseNameOptional.isPresent()) {
			Name name = baseNameOptional.get();
			return name.baseName == bluePrint || name.sharedNames.contains(bluePrint);
		}

		return false;
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
