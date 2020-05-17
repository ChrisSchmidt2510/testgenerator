package org.testgen.compiler.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class JDKClasses {
	private final Set<String> jdkClasses = new HashSet<>();

	private static final JDKClasses INSTANCE = new JDKClasses();

	private JDKClasses() {
		initJDKClasses();
	}

	public static JDKClasses getInstance() {
		return INSTANCE;
	}

	public Optional<String> getQualifiedName(String className) {
		return jdkClasses.stream().filter(jdkClass -> ClassUtils.removePackageFromClass(jdkClass).equals(className))
				.findAny();
	}

	private void initJDKClasses() {
		File classList = new File(System.getProperty("java.home"), "lib/classlist");

		try {
			List<String> rawClassList = Files.readAllLines(classList.toPath());
			// remove Last line cause of the value: # 7b979133406b8b9a
			rawClassList.remove(rawClassList.size() - 1);

			rawClassList.stream().map(this::replaceCharacters).forEach(jdkClasses::add);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String replaceCharacters(String className) {
		return className.replace('/', '.').replace('$', '.');
	}

}
