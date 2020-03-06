package de.nvg.testgenerator.properties.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ArgumentParser {
	private final String args;
	private final Arguments definedArgs;
	private final Map<Model, String> completeArgs = new HashMap<>();

	private static final String EQUAL = "=";
	private static final String GENERALL_ARG_SEPARATUR = "-";
	private static final String LIST_ARG_SEPARATUR = ",";

	public ArgumentParser(String args, Arguments definedArgs) {
		this.args = args;
		this.definedArgs = definedArgs;

		preBuildAllArguments();
	}

	public boolean hasArgument(String argument) {
		Model arg = definedArgs.getArgument(argument);

		String completeArg = completeArgs.get(arg);
		return args.contains(completeArg);
	}

	public String getArgumentValue(String argument) {
		Model arg = definedArgs.getArgument(argument);

		if (arg.getParameter().hasSingleParameter()) {
			String completeArg = completeArgs.get(arg);

			int startIndexArgument = args.indexOf(completeArg);

			if (startIndexArgument >= 0) {
				return args.substring(startIndexArgument + completeArg.length(),
						args.indexOf(GENERALL_ARG_SEPARATUR, startIndexArgument + 1));

			} else if (arg.isRequired()) {
				throw new ParserException("required Argument " + argument + " is missing");
			} else {
				return null;
			}
		}

		throw new ParserException(argument + " has no single-argument");
	}

	public List<String> getArgumentValues(String argument) {
		Model arg = definedArgs.getArgument(argument);

		if (arg.getParameter().hasMultipleParameter()) {
			String completeArg = completeArgs.get(arg);

			int startIndexArgument = args.indexOf(completeArg);

			if (startIndexArgument >= 0) {
				String argParameter = args.substring(startIndexArgument + completeArg.length());

				return getParameters(argParameter);

			} else if (arg.isRequired()) {
				throw new ParserException("required Argument " + argument + " is missing");
			} else {
				return null;
			}
		}

		throw new ParserException(argument + " has no multiple-arguments");
	}

	private List<String> getParameters(String parameters) {
		List<String> parametersList = new ArrayList<>();

		for (String arg : completeArgs.values()) {

			int startIndexArg = parameters.indexOf(arg);
			if (startIndexArg > 0) {
				parameters = parameters.substring(0, startIndexArg);
			}
		}

		StringTokenizer tokenizer = new StringTokenizer(parameters, LIST_ARG_SEPARATUR);

		while (tokenizer.hasMoreTokens()) {
			parametersList.add(tokenizer.nextToken());
		}

		return parametersList;
	}

	private String buildArgument(Model arg) {
		if (arg.getParameter().hasParameter()) {
			return GENERALL_ARG_SEPARATUR + arg.getName() + EQUAL;
		}

		return GENERALL_ARG_SEPARATUR + arg.getName();
	}

	private void preBuildAllArguments() {
		for (Model arg : definedArgs.getAllArguments()) {
			completeArgs.put(arg, buildArgument(arg));
		}
	}

}
