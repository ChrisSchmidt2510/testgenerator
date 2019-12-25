package de.nvg.testgenerator.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import de.nvg.testgenerator.logging.Level;
import de.nvg.testgenerator.logging.Logger;

public class PropertyParser {
	public static final String ARG_CLASS = "Class";
	public static final String ARG_METHOD = "Method";
	public static final String ARG_METHOD_DESC = "MethodDesc";
	public static final String ARG_BL_PACKAGE = "BlPackage";
	public static final String ARG_TRACE_GETTER_CALLS = "TraceGetterCalls";

	private static final String ARG_LOG_LEVEL = "LogLevel";

	private static final String EQUAL = "=";
	private static final String COMMA = ",";

	public static Map<String, String> parseAgentProperties(String arguments) {

		Map<String, String> properties = new HashMap<>();

		StringTokenizer tokenizer = new StringTokenizer(arguments, COMMA);

		while (tokenizer.hasMoreTokens()) {

			String token = tokenizer.nextToken();

			switch (firstHalfOfToken(token)) {
			case ARG_CLASS:
				properties.put(ARG_CLASS, secondHalfOfToken(token));
				break;
			case ARG_METHOD:
				properties.put(ARG_METHOD, secondHalfOfToken(token));
				break;
			case ARG_METHOD_DESC:
				properties.put(ARG_METHOD_DESC, secondHalfOfToken(token));
				break;
			case ARG_BL_PACKAGE:
				properties.put(ARG_BL_PACKAGE, secondHalfOfToken(token));
				break;
			case ARG_TRACE_GETTER_CALLS:
				properties.put(ARG_TRACE_GETTER_CALLS, secondHalfOfToken(token));
				break;
			case ARG_LOG_LEVEL:
				Logger.getInstance().setLevel(Level.valueOf(secondHalfOfToken(token)));
				break;
			default:
				throw new IllegalArgumentException("Ungueltiger Parameter für den ClassTransformerAgent");
			}
		}

		Objects.requireNonNull(properties.get(ARG_CLASS), "Es muss ein " + ARG_CLASS + " Parameter uebergeben werden");
		Objects.requireNonNull(properties.get(ARG_METHOD),
				"Es muss ein " + ARG_METHOD + " Parameter uebergeben werden");
		Objects.requireNonNull(properties.get(ARG_METHOD_DESC),
				"Es muss ein " + ARG_METHOD_DESC + " Parameter uebergeben werden");
		Objects.requireNonNull(properties.get(ARG_BL_PACKAGE),
				"Es muss ein " + ARG_BL_PACKAGE + " Parameter uebergeben werden");

		return properties;

	}

	private static String firstHalfOfToken(String token) {
		return token.substring(0, token.indexOf(EQUAL));
	}

	private static String secondHalfOfToken(String token) {
		return token.substring(token.indexOf(EQUAL) + 1);
	}
}
