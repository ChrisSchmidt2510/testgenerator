package de.nvg.agent.classdata.modification.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeArrayModificator {
	private final List<CodeArrayModificatorModel> codeArrayModificator = new ArrayList<>();

	public int getModificator(int startIndex) {
		return codeArrayModificator.stream().filter(model -> model.codeArrayStartIndex <= startIndex)
				.collect(Collectors.summingInt(model -> model.modificator));
	}

	public int getMaxModificator() {
		return codeArrayModificator.stream().collect(Collectors.summingInt(model -> model.modificator));
	}

	public void addCodeArrayModificator(int startIndex, int modificator) {
		codeArrayModificator.add(new CodeArrayModificatorModel(startIndex, modificator));
	}

	class CodeArrayModificatorModel {
		/** Punkt in dem urspr�nglichen Codearray ab dem der Modificator gilt */
		final int codeArrayStartIndex;
		/**
		 * Wert der zudem urspruenglichen Index addiert werden muss, um gueltige
		 * Eintraege in das Codearray hinzuzufuegen
		 */
		final int modificator;

		public CodeArrayModificatorModel(int codeArrayStartIndex, int modificator) {
			this.codeArrayStartIndex = codeArrayStartIndex;
			this.modificator = modificator;
		}

		@Override
		public String toString() {
			return "ModificatorModel: StartIndex: " + codeArrayStartIndex + " Modificator: " + codeArrayModificator;
		}
	}

}
