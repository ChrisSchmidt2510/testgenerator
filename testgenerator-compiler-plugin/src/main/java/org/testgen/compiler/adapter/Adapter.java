package org.testgen.compiler.adapter;

import java.util.List;
import java.util.stream.Collectors;

import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;

public interface Adapter<T> {

	void visit(T node);

	default <E> void initiateVisit(List<? extends Tree> members, Kind type, Adapter<E> adapter) {
		@SuppressWarnings("unchecked")
		List<E> results = members.stream().filter(member -> type == member.getKind())//
				.map(member -> (E) member).collect(Collectors.toList());

		for (E result : results) {
			adapter.visit(result);
		}
	}
}
