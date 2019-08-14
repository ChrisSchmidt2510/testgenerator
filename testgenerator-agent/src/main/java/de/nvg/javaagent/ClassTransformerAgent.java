package de.nvg.javaagent;

import java.lang.instrument.Instrumentation;
import java.util.Objects;
import java.util.StringTokenizer;

import de.nvg.javaagent.transformer.ClassDataTransformer;
import de.nvg.javaagent.transformer.ValueTrackerTransformer;
import de.nvg.testgenerator.RuntimeProperties;

public class ClassTransformerAgent {

	private static final String AGENT_ARG_CLASS = "Class";
	private static final String AGENT_ARG_METHOD = "Method";
	private static final String AGENT_ARG_METHOD_DESC = "MethodDesc";
	private static final String AGENT_ARG_BL_PACKAGE = "BlPackage";
	private static final String AGENT_ARG_TRACE_GETTER_CALLS = "TraceGetterCalls";

	private static final String EQUAL = "=";

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		StringTokenizer tokenizer = new StringTokenizer(agentArgs, ",");

		RuntimeProperties properties = RuntimeProperties.getInstance();

		while (tokenizer.hasMoreTokens()) {

			String token = tokenizer.nextToken();

			switch (firstHalfOfToken(token)) {
			case AGENT_ARG_CLASS:
				properties.setClassName(secondHalfOfToken(token));
				break;
			case AGENT_ARG_METHOD:
				properties.setMethod(secondHalfOfToken(token));
				break;
			case AGENT_ARG_METHOD_DESC:
				properties.setMethodDescriptor(secondHalfOfToken(token));
				break;
			case AGENT_ARG_BL_PACKAGE:
				properties.setBlPackage(secondHalfOfToken(token));
				break;
			case AGENT_ARG_TRACE_GETTER_CALLS:
				properties.setTraceGetterCalls(Boolean.valueOf(secondHalfOfToken(token)));
				break;
			default:
				throw new IllegalArgumentException("Ungültiger Parameter für den ClassTransformerAgent");
			}
		}

		Objects.requireNonNull(properties.getClassName(),
				"Es muss ein " + AGENT_ARG_CLASS + " Parameter uebergeben werden");
		Objects.requireNonNull(properties.getMethod(),
				"Es muss ein " + AGENT_ARG_METHOD + " Parameter uebergeben werden");
		Objects.requireNonNull(properties.getMethodDescriptor(),
				"Es muss ein " + AGENT_ARG_METHOD_DESC + " Parameter uebergeben werden");
		Objects.requireNonNull(properties.getBlPackage(),
				"Es muss ein " + AGENT_ARG_BL_PACKAGE + " Parameter uebergeben werden");

		ValueTrackerTransformer valueTrackerTransformer = new ValueTrackerTransformer();

		ClassDataTransformer metaDataTransformer = new ClassDataTransformer();

		instrumentation.addTransformer(valueTrackerTransformer);
		instrumentation.addTransformer(metaDataTransformer);
	}

	private static String firstHalfOfToken(String token) {
		return token.substring(0, token.indexOf(EQUAL));
	}

	private static String secondHalfOfToken(String token) {
		return token.substring(token.indexOf(EQUAL) + 1);
	}

}
