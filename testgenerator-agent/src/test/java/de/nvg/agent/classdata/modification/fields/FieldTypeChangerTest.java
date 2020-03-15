package de.nvg.agent.classdata.modification.fields;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import de.nvg.agent.classdata.TestHelper;
import de.nvg.agent.classdata.model.ClassData;
import de.nvg.agent.classdata.model.FieldData;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class FieldTypeChangerTest extends TestHelper {

	@Test
	public void testOverrideFieldAccess() throws BadBytecode, NotFoundException {
		// Probleme beim setzen der GetField Instructions
		init("de.nvg.agent.classdata.testclasses.PartnerEigenschaft", "setPartnerZuordnung",
				Arrays.asList(Opcode.GETFIELD, Opcode.PUTFIELD, Opcode.RETURN));

		FieldData partnerZuordnung = new FieldData.Builder().withName("partnerZuordnung")
				.withDataType("de.nvg.agent.classdata.testclasses.PartnerZuordnung").build();

		ClassData classData = new ClassData("de.nvg.agent.classdata.testclasses.PartnerEigenschaft");
		classData.addFields(Collections.singletonList(partnerZuordnung));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);

		fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, codeAttribute);
	}

	@Test
	public void testOverrideFieldAccess2() throws NotFoundException, BadBytecode {
		// Probleme beim setzen der PutField Instructions
		init("de.nvg.agent.classdata.testclasses.TeilDatum", "setzeDatum", "(Ljava/util/Date;)V",
				Arrays.asList(Opcode.GETFIELD, Opcode.PUTFIELD, Opcode.RETURN));

		FieldData tag = new FieldData.Builder().withName("tag").withDataType("java.lang.Integer").build();
		FieldData monat = new FieldData.Builder().withName("monat").withDataType("java.lang.Integer").build();
		FieldData jahr = new FieldData.Builder().withName("jahr").withDataType("java.lang.Integer").build();
		FieldData stunde = new FieldData.Builder().withName("stunde").withDataType("java.lang.Integer").build();
		FieldData minute = new FieldData.Builder().withName("minute").withDataType("java.lang.Integer").build();

		ClassData classData = new ClassData("de.nvg.agent.classdata.testclasses.TeilDatum");
		classData.addFields(Arrays.asList(tag, monat, jahr, stunde, minute));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);

		fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, codeAttribute);
	}

	@Test
	public void testChangeFieldInitialization() throws NotFoundException, BadBytecode {
		init("de.nvg.agent.classdata.testclasses.Sparte", MethodInfo.nameInit,
				Arrays.asList(Opcode.ALOAD_0, Opcode.PUTFIELD));

		FieldData sparteID = new FieldData.Builder().withName("sparteID").withDataType("java.lang.Integer").build();
		FieldData bezeichner = new FieldData.Builder().withName("bezeichner").withDataType("java.lang.String").build();

		ClassData classData = new ClassData("de.nvg.agent.classdata.testclasses.Sparte");
		classData.setSuperClass(new ClassData("de.nvg.agent.classdata.testclasses.BlObject"));
		classData.addFields(Arrays.asList(sparteID, bezeichner));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);

		fieldTypeChanger.changeFieldInitialization(instructions, filteredInstructions.get(Opcode.PUTFIELD),
				codeAttribute);
	}

}