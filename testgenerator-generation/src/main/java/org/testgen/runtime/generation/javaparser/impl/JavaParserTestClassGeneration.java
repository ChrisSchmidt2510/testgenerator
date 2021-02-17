package org.testgen.runtime.generation.javaparser.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.ReflectionUtil;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.DescriptorType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.GenerationHelper;
import org.testgen.runtime.generation.api.TestClassGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.javaparser.impl.collection.JavaParserCollectionGenerationFactory;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ProxyBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.printer.ConcreteSyntaxModel;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.github.javaparser.printer.concretesyntaxmodel.CsmElement;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

public class JavaParserTestClassGeneration
		implements TestClassGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private static final Logger LOGGER = LogManager.getLogger(JavaParserTestClassGeneration.class);
	private static final String TEST = "Test";

	private ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexObjectGeneration = new JavaParserComplexObjectGeneration();

	private CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory = new JavaParserCollectionGenerationFactory();

	private ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration = new JavaParserArrayGeneration();

	private SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration = new JavaParserSimpleObjectGenerationFactory();

	private NamingService<BlockStmt> namingService = getNamingService();

	private CompilationUnit cu;

	private BlockStmt codeBlock = new BlockStmt();

	private Set<Class<?>> imports = new TreeSet<>((class1, class2) -> class1.getName().compareTo(class2.getName()));
	private boolean useLexicalPrinter = false;

	private NameExpr testObjectAccess;

	private NodeList<Expression> methodParameterNames = new NodeList<>();
	{
		complexObjectGeneration.setSimpleObjectGenerationFactory(simpleObjectGeneration);
		collectionGenerationFactory.setSimpleObjectGenerationFactory(simpleObjectGeneration);
		arrayGeneration.setSimpleObjectGenerationFactory(simpleObjectGeneration);

		collectionGenerationFactory.setComplexObjectGeneration(complexObjectGeneration);
		arrayGeneration.setComplexObjectGeneration(complexObjectGeneration);

		complexObjectGeneration.setCollectionGenerationFactory(collectionGenerationFactory);
		arrayGeneration.setCollectionGenerationFactory(collectionGenerationFactory);

		complexObjectGeneration.setArrayGeneration(arrayGeneration);
		collectionGenerationFactory.setArrayGeneration(arrayGeneration);

		Consumer<Class<?>> importCallBackHandler = imports::add;

		complexObjectGeneration.setImportCallBackHandler(importCallBackHandler);
		collectionGenerationFactory.setImportCallBackHandler(importCallBackHandler);
		arrayGeneration.setImportCallBackHandler(importCallBackHandler);
		simpleObjectGeneration.setImportCallBackHandler(importCallBackHandler);
	}

	@Override
	public ClassOrInterfaceDeclaration createTestClass(Class<?> testClass) {
		Path path = Paths.get(TestgeneratorConfig.getPathToTestclass());

		if (Files.exists(path)) {
			try {
				CompilationUnit compilationUnit = StaticJavaParser.parse(path);

				cu = LexicalPreservingPrinter.setup(compilationUnit);

				useLexicalPrinter = true;
			} catch (IOException e) {
				LOGGER.error("cant parse CompilationUnit at Path " + path);
			}

			Optional<TypeDeclaration<?>> primaryType = cu.getPrimaryType();

			if (primaryType.isPresent() && primaryType.get().isClassOrInterfaceDeclaration())
				return primaryType.get().toClassOrInterfaceDeclaration().get();

			else
				return cu.addClass(testClass.getSimpleName() + TEST);

		} else {
			try {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			} catch (IOException e) {
				LOGGER.error("cant create Path" + path, e);
			}

			cu = new CompilationUnit(testClass.getPackage().getName());
			return cu.addClass(testClass.getSimpleName() + TEST);
		}

	}

	@Override
	public void prepareTestObject(ClassOrInterfaceDeclaration compilationUnit, BluePrint testObject,
			ClassData classData, Set<FieldData> calledFields) {

		LOGGER.debug("generate Testobject" + testObject);

		complexObjectGeneration.createObject(codeBlock, testObject.castToComplexBluePrint(), false, classData,
				calledFields);

		testObjectAccess = new NameExpr(namingService.getLocalName(codeBlock, testObject));

	}

	@Override
	public void prepareMethodParameters(ClassOrInterfaceDeclaration compilationUnit, List<BluePrint> methodParameters,
			List<DescriptorType> methodTypeTable) {
		LOGGER.debug("start generating MethodParameters");

		for (int i = 0; i < methodParameters.size(); i++) {
			BluePrint param = methodParameters.get(i);

			LOGGER.debug("generate Methodparameter " + param);

			methodParameterNames.add(new NameExpr(namingService.getLocalName(codeBlock, param)));

			DescriptorType descriptor = methodTypeTable.get(i);

			generateBluePrint(param, descriptor.isSignatureType() ? descriptor.castToSignatureType() : null);
		}

	}

	@Override
	public void prepareProxyObjects(ClassOrInterfaceDeclaration compilationUnit,
			Map<ProxyBluePrint, List<BluePrint>> proxyObjects) {
		LOGGER.debug("start generating Proxies");

		for (Entry<ProxyBluePrint, List<BluePrint>> proxyPair : proxyObjects.entrySet()) {

			ProxyBluePrint proxy = proxyPair.getKey();

			LOGGER.debug("generate ProxyBluePrint " + proxy);

			generateBluePrint(proxy, null);

			LOGGER.debug("start generating proxy childs");

			for (BluePrint child : proxyPair.getValue()) {

				LOGGER.debug("generate proxy child " + child);

				if (child.isCollectionBluePrint() || child.isArrayBluePrint()) {
					Method proxyMethod = Arrays.stream(proxy.getInterfaceClass().getMethods())
							.filter(method -> child.getName().equals(method.getName())).findAny().get();

					SignatureType signature = GenerationHelper
							.mapGenericTypeToSignature(proxyMethod.getGenericReturnType());

					generateBluePrint(child, signature);

				} else
					generateBluePrint(child, null);

			}
		}
	}

	@Override
	public void generateTestMethod(ClassOrInterfaceDeclaration compilationUnit, String methodName,
			boolean withProxyObjects) {

		ExpressionStmt statement = new ExpressionStmt(
				new MethodCallExpr(testObjectAccess, methodName, methodParameterNames));
		statement.setLineComment("TODO add Assertion");

		codeBlock.addStatement(statement);

		cu.addImport("org.junit.Test");

		imports.forEach(cu::addImport);

		String testMethodName = JavaParserHelper.getMethodName(compilationUnit, methodName);

		MethodDeclaration testMethod = compilationUnit.addMethod(testMethodName, Keyword.PUBLIC);
		testMethod.addAnnotation(new MarkerAnnotationExpr("Test"));
		testMethod.setType(new VoidType());
		testMethod.setBody(codeBlock);

	}

	@Override
	public void toFile(ClassOrInterfaceDeclaration compilationUnit) {
		Path path = Paths.get(TestgeneratorConfig.getPathToTestclass());

		if (useLexicalPrinter) {

			Field field = ReflectionUtil.getField(ConcreteSyntaxModel.class, "concreteSyntaxModelByClass");
			field.setAccessible(true);

			Map<Class<?>, CsmElement> concreteSyntaxModel = ReflectionUtil.accessStaticField(field);
			concreteSyntaxModel.replace(EmptyStmt.class, CsmElement.sequence(CsmElement.comment()));

			try (FileOutputStream outputStream = new FileOutputStream(path.toFile());
					OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream)) {
				LexicalPreservingPrinter.print(cu, streamWriter);
			} catch (IOException e) {
				LOGGER.error("cant write modified Class to File", e);
				LOGGER.error("generated Test:");
				LOGGER.error(LexicalPreservingPrinter.print(cu));
			}

		} else {
			PrettyPrinterConfiguration printerConfig = new PrettyPrinterConfiguration()
					.setVisitorFactory(TestgeneratorPrettyPrinter::new);

			try {
				Files.write(path, cu.toString(printerConfig).getBytes());
			} catch (IOException e) {
				LOGGER.error("cant write modified Class to File", e);
				LOGGER.error("generated Test:");
				LOGGER.error(cu.toString(printerConfig));
			}
		}

	}

	private void generateBluePrint(BluePrint bluePrint, SignatureType signature) {
		if (bluePrint.isComplexBluePrint()) {

			ClassData classData = GenerationHelper.getClassData(bluePrint.getReference());

			Set<FieldData> calledFields = Collections.emptySet();
			if (TestgeneratorConfig.traceReadFieldAccess()) {
				calledFields = GenerationHelper.getCalledFields(bluePrint.getReference());
			}

			complexObjectGeneration.createObject(codeBlock, bluePrint.castToComplexBluePrint(), false, classData,
					calledFields);

		} else if (bluePrint.isCollectionBluePrint()) {
			AbstractBasicCollectionBluePrint<?> collection = bluePrint.castToCollectionBluePrint();
			collectionGenerationFactory.createCollection(codeBlock, collection, signature, false);

		} else if (bluePrint.isArrayBluePrint()) {
			ArrayBluePrint array = bluePrint.castToArrayBluePrint();
			arrayGeneration.createArray(codeBlock, array, signature, false);

		} else if (bluePrint.isSimpleBluePrint()) {
			SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

			simpleObjectGeneration.createObject(codeBlock, simpleBluePrint, false);

		} else if (bluePrint instanceof ProxyBluePrint) {
			ProxyBluePrint proxy = (ProxyBluePrint) bluePrint;

			if (bluePrint.isNotBuild()) {
				ClassOrInterfaceType proxyType = new ClassOrInterfaceType(null,
						proxy.getInterfaceClass().getSimpleName());

				ExpressionStmt expression = new ExpressionStmt(new VariableDeclarationExpr(new VariableDeclarator(
						proxyType, namingService.getLocalName(codeBlock, proxy), new NullLiteralExpr())));

				expression.setLineComment("TODO add initalization for proxy " + proxy.getInterfaceClass());

				codeBlock.addStatement(expression);
				codeBlock.addStatement(new EmptyStmt());
			}
		}
	}

}
