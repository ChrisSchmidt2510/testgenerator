package org.testgen.runtime.classdata.model;

/** Currently only used for Collections */
public enum SetterType {
	/**
	 * Used for normal setters like<br>
	 * <code>
	 * public void setList(List<?> list){<br>
	 * 		this.list.addAll(list);<br>
	 * }<br>
	 *  </code>
	 */
	VALUE_SETTER, //
	/**
	 * Used for normal getters like<br>
	 * <code>
	 * public List<?> getList()
	 * </code>
	 */
	VALUE_GETTER, //
	/**
	 * Used for setter that adds a single Argument to a Collection<br>
	 * <code>
	 * public void addAdresse(Adresse adresse){<br>
	 *  adressen.add(adresse);<br>
	 *  }<br>
	 * </code>
	 */
	COLLECTION_SETTER;

}
