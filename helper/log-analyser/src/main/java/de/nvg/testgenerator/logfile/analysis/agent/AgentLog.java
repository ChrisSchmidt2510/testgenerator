package de.nvg.testgenerator.logfile.analysis.agent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class AgentLog {
	private static final String CLASSNAME = "ClassName:";
	private static final String SUPERCLASS = "Superclass:";

	private static final String ANALYSIS_START = "Starting Analysis of Method:";
	private static final String ANALYSIS_END = "Result of Analysis:";

	private static final String COLON = ":";

	private AgentLog() {
	}

	public static void processMethodAnalysisLog(Path directory) throws IOException {
		List<String> lines = Files.readAllLines(directory, StandardCharsets.ISO_8859_1);

		for (String line : lines) {
			if (line.contains(CLASSNAME)) {
				// Leerzeile um vorherige Eintraege abzugrenzen
				System.out.println("");
				String classname = line.substring(line.lastIndexOf(COLON) + 2);
				System.out.println("Classname " + classname);
			} else if (line.contains(SUPERCLASS)) {
				String superclass = line.substring(line.lastIndexOf(COLON) + 2);
				System.out.println("Superclass " + superclass);
			} else if (line.contains(ANALYSIS_START)) {
				String method = line.substring(line.lastIndexOf(COLON) + 2);
				System.out.println("	" + method);
			} else if (line.contains(ANALYSIS_END)) {
				String result = line.substring(line.lastIndexOf(COLON) + 2);
				System.out.println("		" + result);
			}
		}
	}

}
