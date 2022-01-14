package org.testgen.agent.classdata.testclasses;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

public class Collections {
	private Collection<String> collection = new Vector<>();
	private List<String> list = new ArrayList<>();
	private ArrayList<String> arrayList = new ArrayList<>();
	private Set<String> set = new HashSet<>();
	private Queue<String> queue = new LinkedList<>();
	private Deque<String> deque = new ArrayDeque<>();
	private Map<String, Integer> map = new HashMap<>();

	public void addCollection(String value) {
		if (!collection.contains(value))
			collection.add(value);
	}

	public void addAllCollection(Collection<String> value) {
		collection.addAll(value);
	}

	public void addList(String value) {
		list.add(value);
	}

	public void addListWithIndex(String value) {
		list.add(list.size(), value);
	}

	public void addAllList(Collection<String> value) {
		list.addAll(value);
	}

	public void addAllListWithIndex(Collection<String> value) {
		list.addAll(0, value);
	}

	public void addArrayList(String value) {
		arrayList.add(value);
	}

	public void addSet(String value) {
		if (!set.contains(value))
			set.add(value);
	}

	public void addAllSet(Set<String> value) {
		set.addAll(value);
	}

	public void addQueue(String value) {
		queue.add(value);
	}

	public void offerQueue(String value) {
		queue.offer(value);
	}

	public void addDeque(String value) {
		deque.add(value);
	}

	public void addFirstDeque(String value) {
		deque.addFirst(value);
	}

	public void addLastDeque(String value) {
		deque.addLast(value);
	}

	public void addAllDeque(Collection<String> value) {
		deque.addAll(value);
	}

	public void offerDeque(String value) {
		deque.offer(value);
	}

	public void offerFirstDeque(String value) {
		deque.offerFirst(value);
	}

	public void offerLastDeque(String value) {
		deque.offerLast(value);
	}

	public void pushDeque(String value) {
		deque.push(value);
	}

	public void putMap(String key, Integer value) {
		map.put(key, value);
	}

	public void putAllMap(Map<String, Integer> value) {
		map.putAll(value);
	}

	public void putIfAbsentMap(String key, Integer value) {
		map.putIfAbsent(key, value);
	}

	public void computeIfAbsentMap(String key, Integer value) {
		map.computeIfAbsent(key, k -> value);
	}

}
