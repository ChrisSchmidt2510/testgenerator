package org.testgen.runtime.valuetracker.blueprint.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;

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

	@Override
	public void resetBuildState() {
		if (build) {
			build = false;
			elementBluePrints.forEach(BluePrint::resetBuildState);
		}
	}

	public List<BluePrint> getBluePrints() {
		return Collections.unmodifiableList(elementBluePrints);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, interfaceClass, elementBluePrints);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CollectionBluePrint))
			return false;
		CollectionBluePrint other = (CollectionBluePrint) obj;
		return Objects.equals(name, other.name) && Objects.equals(interfaceClass, other.interfaceClass)
				&& Objects.equals(elementBluePrints, other.elementBluePrints);
	}

	public static class CollectionBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Collection<?>;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				CurrentlyBuildedBluePrints currentlyBuildedBluePrints,
				BiFunction<String, Object, BluePrint> childCallBack) {

			CollectionBluePrint bluePrint;

			Collection<?> collection = (Collection<?>) value;

			if (value instanceof List<?>)
				bluePrint = new CollectionBluePrint(name, collection, List.class);

			else if (value instanceof Set<?>)
				bluePrint = new CollectionBluePrint(name, collection, Set.class);

			else if (value instanceof Deque<?>)
				bluePrint = new CollectionBluePrint(name, collection, Deque.class);

			else if (value instanceof Queue<?>)
				bluePrint = new CollectionBluePrint(name, collection, Queue.class);

			else
				bluePrint = new CollectionBluePrint(name, collection, Collection.class);

			for (Object object : collection) {

				if (currentlyBuildedBluePrints.isCurrentlyBuilded(object))
					currentlyBuildedBluePrints.addFinishedListener(object, bp -> bluePrint.addBluePrint(bp));

				else {
					BluePrint element = childCallBack.apply(name + "Element", object);
					bluePrint.addBluePrint(element);
				}
			}

			return bluePrint;
		}

	}

}
