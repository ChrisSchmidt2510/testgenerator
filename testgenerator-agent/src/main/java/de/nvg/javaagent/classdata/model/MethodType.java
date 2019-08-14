package de.nvg.javaagent.classdata.model;

public enum MethodType {
	// TODO doc für maps u.arrays anpassen

	/**
	 * Used for normal setters like<br>
	 * <code>
	 * public void setName(String name)<br>
	 * public void setAge(int age)<br>
	 * public void setList(List<?> list)<br>
	 * public void setList(List<?> list){<br>
	 * 	this.list.addAll(list);<br>
	 * }<br>
	 *  </code>
	 */
	REFERENCE_VALUE_SETTER, //
	/**
	 * Used for setter that adds a single Argument to a Collection<br>
	 * <code>
	 * public void addAdresse(Adresse adresse){<br>
	 *  adressen.add(adresse);<br>
	 *  }<br>
	 * </code>
	 */
	COLLECTION_SETTER, //
	/**
	 * Used for normal getters like<br>
	 * <code>
	 * public String getName()<br>
	 * public int getAge()<br>
	 * public List<?> getList()</code>
	 */
	REFERENCE_VALUE_GETTER, //
	/**
	 * Used for immutable getters like<br>
	 * <code>
	 * private final String name;<br>
	 * public String getName(){<br>
	 * return name;<br>
	 * }<br>
	 * <br>
	 * public List<?> getList{<br>
	 * return Collections.unmodifiableList(list);<br>
	 * }</code>
	 */
	IMMUTABLE_GETTER;

}
