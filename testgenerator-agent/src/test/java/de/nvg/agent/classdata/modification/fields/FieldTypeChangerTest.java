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
		init("de.nvg.agent.classdata.testclasses.PartnerProperty", "setPartner",
				Arrays.asList(Opcode.GETFIELD, Opcode.PUTFIELD, Opcode.RETURN));

		FieldData partnerZuordnung = new FieldData.Builder().withName("partner")
				.withDataType("de.nvg.agent.classdata.testclasses.Partner").build();

		ClassData classData = new ClassData("de.nvg.agent.classdata.testclasses.PartnerProperty");
		classData.addFields(Collections.singletonList(partnerZuordnung));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);

		fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, codeAttribute);
	}

	@Test
	public void testOverrideFieldAccess2() throws NotFoundException, BadBytecode {
		// Probleme beim setzen der PutField Instructions
		init("de.nvg.agent.classdata.testclasses.FragmentDate", "setDate", "(Ljava/util/Date;)V",
				Arrays.asList(Opcode.GETFIELD, Opcode.PUTFIELD, Opcode.RETURN));

		FieldData tag = new FieldData.Builder().withName("day").withDataType("java.lang.Integer").build();
		FieldData monat = new FieldData.Builder().withName("month").withDataType("java.lang.Integer").build();
		FieldData jahr = new FieldData.Builder().withName("year").withDataType("java.lang.Integer").build();
		FieldData stunde = new FieldData.Builder().withName("hour").withDataType("java.lang.Integer").build();
		FieldData minute = new FieldData.Builder().withName("minute").withDataType("java.lang.Integer").build();

		ClassData classData = new ClassData("de.nvg.agent.classdata.testclasses.FragmentDate");
		classData.addFields(Arrays.asList(tag, monat, jahr, stunde, minute));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);

		fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, codeAttribute);
	}

	@Test
	public void testChangeFieldInitialization() throws NotFoundException, BadBytecode {
		init("de.nvg.agent.classdata.testclasses.Person", MethodInfo.nameInit, Arrays.asList(Opcode.PUTFIELD));

		FieldData fieldName = new FieldData.Builder().withName("name").withDataType("java.lang.String").build();
		FieldData fieldVorname = new FieldData.Builder().withName("firstName").withDataType("java.lang.String").build();
		FieldData fieldOrt = new FieldData.Builder().withName("dateOfBirth").withDataType("java.time.LocalDate")
				.build();
		FieldData fieldPlz = new FieldData.Builder().withName("geschlecht")
				.withDataType("de.nvg.agent.classdata.testclasses.Person$Geschlecht").build();
		FieldData fieldAdressen = new FieldData.Builder().withName("adressen").withDataType("java.util.List").build();

		ClassData classData = new ClassData("de.nvg.agent.classdata.testclasses.Person");
		classData.setSuperClass(new ClassData("de.nvg.agent.classdata.testclasses.BlObject"));
		classData.addFields(Arrays.asList(fieldName, fieldVorname, fieldOrt, fieldPlz, fieldAdressen));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);

		fieldTypeChanger.changeFieldInitialization(instructions, filteredInstructions.get(Opcode.PUTFIELD),
				codeAttribute);
	}

}
