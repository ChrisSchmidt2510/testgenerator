package org.testgen.runtime.valuetracker.blueprint.complextypes.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.CollectionBluePrint.CollectionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

public class CollectionBluePrintTest {

	private CollectionBluePrintFactory factory = new CollectionBluePrintFactory();

	private StringBluePrintFactory strFactory = new StringBluePrintFactory();

	private NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();

	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(new LinkedList<>()));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertFalse(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testTrackList() {
		List<String> list = new ArrayList<>(Arrays.asList("Christoph", "Schmidt", "Word"));

		BluePrint bluePrint = factory.createBluePrint("collection", list, currentlyBuildedBluePrints,
				(name, value) -> strFactory.createBluePrint(name, (String) value));

		assertTrue(bluePrint.isCollectionBluePrint());

		CollectionBluePrint collection = (CollectionBluePrint) bluePrint;
		assertEquals("collection", collection.getName());
		assertEquals(List.class, collection.getInterfaceClass());
		assertEquals(ArrayList.class, collection.getImplementationClass());
		assertTrue(collection.getPreExecuteBluePrints().isEmpty());

		List<BluePrint> compareList = new ArrayList<>();
		compareList.add(strFactory.createBluePrint("collectionElement", "Christoph"));
		compareList.add(strFactory.createBluePrint("collectionElement", "Schmidt"));
		compareList.add(strFactory.createBluePrint("collectionElement", "Word"));

		assertEquals(compareList, collection.getBluePrints());
	}

	@Test
	public void testTrackSet() {
		Set<Integer> set = new LinkedHashSet<>();
		set.add(1);
		set.add(8);
		set.add(7);

		BluePrint bluePrint = factory.createBluePrint("set", set, currentlyBuildedBluePrints,
				(name, value) -> numFactory.createBluePrint(name, (Number) value));

		assertTrue(bluePrint.isCollectionBluePrint());

		CollectionBluePrint collection = (CollectionBluePrint) bluePrint;
		assertEquals("set", collection.getName());
		assertEquals(Set.class, collection.getInterfaceClass());
		assertEquals(LinkedHashSet.class, collection.getImplementationClass());
		assertTrue(collection.getPreExecuteBluePrints().isEmpty());

		List<BluePrint> compareList = new ArrayList<>();
		compareList.add(numFactory.createBluePrint("setElement", 1));
		compareList.add(numFactory.createBluePrint("setElement", 8));
		compareList.add(numFactory.createBluePrint("setElement", 7));

		assertEquals(compareList, collection.getBluePrints());
	}

	@Test
	public void testTrackDeque() {
		Deque<Long> deque = new ArrayDeque<>();
		deque.push(24L);
		deque.push(12L);
		deque.push(0L);

		BluePrint bluePrint = factory.createBluePrint("deque", deque, currentlyBuildedBluePrints,
				(name, value) -> numFactory.createBluePrint(name, (Number) value));

		assertTrue(bluePrint.isCollectionBluePrint());

		CollectionBluePrint collection = (CollectionBluePrint) bluePrint;
		assertEquals("deque", collection.getName());
		assertEquals(Deque.class, collection.getInterfaceClass());
		assertEquals(ArrayDeque.class, collection.getImplementationClass());
		assertTrue(collection.getPreExecuteBluePrints().isEmpty());

		List<BluePrint> compareList = new ArrayList<>();
		compareList.add(numFactory.createBluePrint("dequeElement", 0L));
		compareList.add(numFactory.createBluePrint("dequeElement", 12L));
		compareList.add(numFactory.createBluePrint("dequeElement", 24L));

		assertEquals(compareList, collection.getBluePrints());
	}

	@Test
	public void testTrackQueue() {
		Queue<String> queue = new PriorityQueue<>();
		queue.offer("Word");
		queue.offer("Exel");
		queue.offer("Powerpoint");

		BluePrint bluePrint = factory.createBluePrint("queue", queue, currentlyBuildedBluePrints,
				(name, value) -> strFactory.createBluePrint(name, (String) value));

		assertTrue(bluePrint.isCollectionBluePrint());

		CollectionBluePrint collection = (CollectionBluePrint) bluePrint;
		assertEquals("queue", collection.getName());
		assertEquals(Queue.class, collection.getInterfaceClass());
		assertEquals(PriorityQueue.class, collection.getImplementationClass());
		assertTrue(collection.getPreExecuteBluePrints().isEmpty());

		List<BluePrint> compareList = new ArrayList<>();
		compareList.add(strFactory.createBluePrint("queueElement", "Exel"));
		compareList.add(strFactory.createBluePrint("queueElement", "Word"));
		compareList.add(strFactory.createBluePrint("queueElement", "Powerpoint"));

		assertEquals(compareList, collection.getBluePrints());
	}

	@Test
	public void testTrackCollection() {
		Collection<Integer> abstractCollection = new CustomCollection();

		abstractCollection.add(1);
		abstractCollection.add(9);
		abstractCollection.add(8);

		BluePrint bluePrint = factory.createBluePrint("collection", abstractCollection, currentlyBuildedBluePrints,
				(name, value) -> numFactory.createBluePrint(name, (Number) value));

		assertTrue(bluePrint.isCollectionBluePrint());

		CollectionBluePrint collection = (CollectionBluePrint) bluePrint;
		assertEquals("collection", collection.getName());
		assertEquals(Collection.class, collection.getInterfaceClass());
		assertEquals(CustomCollection.class, collection.getImplementationClass());
		assertTrue(collection.getPreExecuteBluePrints().isEmpty());

		List<BluePrint> compareList = new ArrayList<>();
		compareList.add(numFactory.createBluePrint("collectionElement", 1));
		compareList.add(numFactory.createBluePrint("collectionElement", 9));
		compareList.add(numFactory.createBluePrint("collectionElement", 8));

		assertEquals(compareList, collection.getBluePrints());
	}

	@Test
	public void testTrackStackedCollections() {
		List<Integer> child1 = Arrays.asList(1, 2, 3);
		List<Integer> child2 = Arrays.asList(7, 8, 9);

		List<List<Integer>> values = Arrays.asList(child1, child2);

		BiFunction<String, Object, BluePrint> numFunction = (name, value) -> numFactory.createBluePrint(name,
				(Number) value);

		BiFunction<String, Object, BluePrint> function = (name, value) -> factory.createBluePrintForType(value)
				? factory.createBluePrint(name, value, currentlyBuildedBluePrints, numFunction)
				: numFunction.apply(name, value);

		BluePrint bluePrint = factory.createBluePrint("values", values, currentlyBuildedBluePrints, function);

		CollectionBluePrint collection = (CollectionBluePrint) bluePrint;
		assertEquals(2, collection.getPreExecuteBluePrints().size());

		CollectionBluePrint childBp = new CollectionBluePrint("valuesElement", new ArrayList<>(), List.class);
		childBp.addBluePrint(numFactory.createBluePrint("valuesElementElement", 1));
		childBp.addBluePrint(numFactory.createBluePrint("valuesElementElement", 2));
		childBp.addBluePrint(numFactory.createBluePrint("valuesElementElement", 3));
		
		CollectionBluePrint childBp2 = new CollectionBluePrint("valuesElement", new ArrayList<>(), List.class);
		childBp2.addBluePrint(numFactory.createBluePrint("valuesElementElement", 7));
		childBp2.addBluePrint(numFactory.createBluePrint("valuesElementElement", 8));
		childBp2.addBluePrint(numFactory.createBluePrint("valuesElementElement", 9));
		
		assertEquals(Arrays.asList(childBp, childBp2), collection.getBluePrints());
	}

	public class CustomCollection extends AbstractCollection<Integer> {
		private List<Integer> values = new ArrayList<>();

		@Override
		public Iterator<Integer> iterator() {
			return values.iterator();
		}

		@Override
		public int size() {
			return values.size();
		}

		@Override
		public boolean add(Integer e) {
			return values.add(e);
		}

	}
}
