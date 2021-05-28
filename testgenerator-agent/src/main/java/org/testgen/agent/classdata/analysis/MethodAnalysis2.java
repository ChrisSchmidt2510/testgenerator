package org.testgen.agent.classdata.analysis;

import java.util.List;

import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.model.ClassData;

import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;

/**
 * Interface for applying an Analysis for a single method(constructors
 * excluded).For the execution of the analysis is some extra data stored in the
 * objects {@link ClassData} and {@link ClassFile} necessary. To initialize an
 * analyzer properly call{@link MethodAnalysis2#setClassData(ClassData)} and
 * {@link MethodAnalysis2#setClassFile(ClassFile)}.
 * 
 * Before the analysis is executed on the instructions of the method, a
 * validation is applied implemented in the method
 * {@link MethodAnalysis2#canAnalysisBeApplied(MethodInfo)}. If the validation
 * is passed successfully the analysis gets executed
 * {@link MethodAnalysis2#hasAnalysisMatched(MethodInfo, List)}.
 * 
 * Custom analyzers can easy included, because this interface is used with the
 * Java SPI technology.
 * 
 * @see <a href=
 *      "https://docs.oracle.com/javase/tutorial/ext/basics/spi.html">Java
 *      SPI</a>
 *
 */
public interface MethodAnalysis2 {

	void setClassData(ClassData classData);

	void setClassFile(ClassFile classFile);

	/**
	 * Validates if the method match the criteria of the analyzer, e.g. visibility
	 * of the method, return type of the method or number of method parameters
	 * 
	 * @param method data of the method
	 * @return
	 */
	boolean canAnalysisBeApplied(MethodInfo method);

	/**
	 * executes the analysis on the instruction set of a single method. If the
	 * analysis is successful true will be returned and the result is added to
	 * {@link ClassData}.
	 * 
	 * @param method       data of the method
	 * @param instructions instruction set of the method
	 * @return
	 */
	boolean hasAnalysisMatched(MethodInfo method, List<Instruction> instructions);

}
