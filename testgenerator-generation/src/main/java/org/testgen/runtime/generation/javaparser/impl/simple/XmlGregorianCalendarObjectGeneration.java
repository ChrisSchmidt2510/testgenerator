package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.function.Consumer;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.generation.javaparser.impl.JavaParserHelper;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.XMLGregorianCalendarBluePrint;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class XmlGregorianCalendarObjectGeneration
		implements SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private static final Logger LOGGER = LogManager.getLogger(XmlGregorianCalendarObjectGeneration.class);

	private NamingService<BlockStmt> namingService = getNamingService();

	private Consumer<Class<?>> importCallBackHandler = getImportCallBackHandler();

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof XMLGregorianCalendarBluePrint;
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, SimpleBluePrint<?> bluePrint,
			boolean withInitalizer) {

		if (withInitalizer)
			LOGGER.error("cant create initalizer for XMLGregorianCalendarBluePrint");

		String name = namingService.getFieldName(bluePrint);

		FieldDeclaration field = compilationUnit.addFieldWithInitializer(XMLGregorianCalendar.class, name,
				new NullLiteralExpr(), Keyword.PRIVATE);

		XMLGregorianCalendarBluePrint calendar = (XMLGregorianCalendarBluePrint) bluePrint;

		field.addOrphanComment(new JavadocComment(String.format(
				"Can't create XmlGregorianCalendar instance. "
						+ " Year: %s Month: %s Day: %s Hour: %s Minute: %s Second: %s Millisecond: %s TimeZone: %s",
				calendar.getYear(), calendar.getMonth(), calendar.getDay(), calendar.getHour(), calendar.getMinute(),
				calendar.getSecond(), calendar.getMillisecond(), calendar.getTimezone())));

	}

	@Override
	public void createObject(BlockStmt statementTree, SimpleBluePrint<?> bluePrint, boolean isField) {

		String name = isField ? namingService.getFieldName(bluePrint)
				: namingService.getLocalName(statementTree, bluePrint);

		importCallBackHandler.accept(XMLGregorianCalendar.class);
		importCallBackHandler.accept(DatatypeFactory.class);
		importCallBackHandler.accept(DatatypeConfigurationException.class);

		if (!isField) {
			VariableDeclarationExpr localVar = new VariableDeclarationExpr(
					new VariableDeclarator(new ClassOrInterfaceType(null, XMLGregorianCalendar.class.getSimpleName()), //
							name, new NullLiteralExpr()));

			statementTree.addStatement(localVar);
		}

		XMLGregorianCalendarBluePrint calendar = (XMLGregorianCalendarBluePrint) bluePrint;

		NodeList<Expression> arguments = new NodeList<>();

		boolean hasDate = calendar.getYear() > 0 && calendar.getMonth() > 0;
		boolean hasTime = calendar.getHour() > 0;

		if (hasDate) {
			arguments.add(JavaParserHelper.mapIntegerExpression(calendar.getYear()));
			arguments.add(JavaParserHelper.mapIntegerExpression(calendar.getMonth()));
			arguments.add(JavaParserHelper.mapIntegerExpression(calendar.getDay()));
		}

		if (hasTime) {
			arguments.add(JavaParserHelper.mapIntegerExpression(calendar.getHour()));
			arguments.add(JavaParserHelper.mapIntegerExpression(calendar.getMinute()));
			arguments.add(JavaParserHelper.mapIntegerExpression(calendar.getSecond()));
			arguments.add(JavaParserHelper.mapIntegerExpression(calendar.getMillisecond()));
		}

		arguments.add(JavaParserHelper.mapIntegerExpression(calendar.getTimezone()));

		String methodName;

		if (hasDate && !hasTime) {
			methodName = "newXMLGregorianCalendarDate";
		} else if (!hasDate && hasTime) {
			methodName = "newXMLGregorianCalendarTime";
		} else {
			methodName = "newXMLGregorianCalendar";
		}

		MethodCallExpr calendarCreation = new MethodCallExpr(
				new MethodCallExpr(new NameExpr(DatatypeFactory.class.getSimpleName()), "newInstance"), methodName,
				arguments);

		AssignExpr assignExpr = new AssignExpr(isField ? new FieldAccessExpr(new ThisExpr(), name) : new NameExpr(name),
				calendarCreation, AssignExpr.Operator.ASSIGN);

		BlockStmt tryBlock = new BlockStmt(NodeList.nodeList(new ExpressionStmt(assignExpr)));

		MethodCallExpr catchClauseImpl = new MethodCallExpr(new NameExpr("e"), "printStackTrace");

		ExpressionStmt catchClauseExpr = new ExpressionStmt(catchClauseImpl);
		catchClauseExpr.setLineComment("TODO Auto-generated catch block");

		BlockStmt catchBlock = new BlockStmt(NodeList.nodeList(catchClauseExpr));

		Parameter catchType = new Parameter(
				new ClassOrInterfaceType(null, DatatypeConfigurationException.class.getSimpleName()), "e");

		CatchClause catchClause = new CatchClause(catchType, catchBlock);
		TryStmt tryStmt = new TryStmt(tryBlock, NodeList.nodeList(catchClause), null);

		statementTree.addStatement(tryStmt);

	}

	@Override
	public Expression createInlineObject(SimpleBluePrint<?> bluePrint) {
		throw new UnsupportedOperationException("cant create inline object XMLGregorianCalendarBluePrint");
	}

}
