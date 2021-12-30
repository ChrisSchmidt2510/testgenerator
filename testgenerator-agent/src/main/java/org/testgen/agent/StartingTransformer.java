package org.testgen.agent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.WeakHashMap;

import org.testgen.agent.transformer.ClassTransformer;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

public class StartingTransformer implements ClassFileTransformer {

	private static final Logger LOGGER = LogManager.getLogger(StartingTransformer.class);

	private Set<ClassLoader> classLoaders = Collections.newSetFromMap(new WeakHashMap<>());
	private List<ClassTransformer> classTransformers = new ArrayList<>();

	{
		classLoaders.add(this.getClass().getClassLoader());
		ServiceLoader.load(ClassTransformer.class).forEach(classTransformers::add);
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		if (loader != null && !classLoaders.contains(loader)) {
			classLoaders.add(loader);

			ClassPool classPool = ClassPool.getDefault();
			classPool.appendClassPath(new LoaderClassPath(loader));
		}

		CtClass loadingClass = makeClass(classfileBuffer);
		for (ClassTransformer transformer : classTransformers) {

			if (transformer.modifyClassFile(className, loadingClass)) 
				transformer.transformClassFile(className, loadingClass);
			
		}

		try {
			return loadingClass.toBytecode();
		} catch (IOException | CannotCompileException e) {
			LOGGER.error("error while transforming class", e);
			throw new AgentException("error while transforming class", e);
		} finally {
			if (loadingClass != null)
				loadingClass.detach();
		}
	}

	private CtClass makeClass(byte[] classFileBuffer) {

		try (ByteArrayInputStream stream = new ByteArrayInputStream(classFileBuffer)) {
			return ClassPool.getDefault().makeClass(stream);
		} catch (IOException e) {
			LOGGER.error("error while loading class", e);
			throw new AgentException("error while loading class", e);
		}
	}

}
