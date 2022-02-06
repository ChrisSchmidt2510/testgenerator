package org.testgen.agent.classdata.analysis.method;

import java.util.List;

import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.model.ClassData;

import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;

/**
 * Interface for applying an Analysis for a single method. 
 * For the execution of the analysis is some extra data stored in the
 * objects {@link ClassData} and {@link ClassFile} necessary. To initialize an
 * analyzer properly call{@link MethodAnalysis#setClassData(ClassData)} and
 * {@link MethodAnalysis#setClassFile(ClassFile)}.
 * 
 * Before the analysis is executed on the instructions of the method, a
 * validation is applied implemented in the method
 * {@link MethodAnalysis#canAnalysisBeApplied(MethodInfo)}. If the validation is
 * passed successfully the analysis gets executed
 * {@link MethodAnalysis#hasAnalysisMatched(MethodInfo, List)}.
 * 
 * Custom analyzers can easy included, because this interface is used with the
 * Java SPI technology.
 * 
 * @see <a href=
 *      "https://docs.oracle.com/javase/tutorial/ext/basics/spi.html">Java
 *      SPI</a>
 *
 */
public interface MethodAnalysis {

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

	/**
	 * if the analyzer need to store information for all methods that will be
	 * analyzed with this analyzer of a class, the collected data can be reseted
	 * within this method.
	 * This method is called after the analysis process of a class is finished.
	 */
	default void reset() {
	}

}
