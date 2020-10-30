package org.testgen.agent.classdata.modification.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.Person;
import org.testgen.agent.classdata.testclasses.Switch;
import org.testgen.agent.classdata.testclasses.Value;

import javassist.CannotCompileException;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.SignatureAttribute;

public class FieldTypeChangerTest extends TestHelper {

	private static final String REFERENCE_PROXY = "Lorg/testgen/runtime/proxy/impl/ReferenceProxy;";
	private static final String REFERENCE_PROXY_CLASSNAME = "org.testgen.runtime.proxy.impl.ReferenceProxy";
	private static final String INTEGER_PROXY = "Lorg/testgen/runtime/proxy/impl/IntegerProxy;";
	private static final String INTEGER_PROXY_CLASSNAME = "org.testgen.runtime.proxy.impl.IntegerProxy";

	@Test
	public void testOverrideFieldAccessWithSimpleGetter() throws NotFoundException, BadBytecode {
		init(Value.class, "getValueID", Arrays.asList(Opcode.GETFIELD, Opcode.PUTFIELD));

		FieldData valueID = new FieldData.Builder().withName("valueID")//
				.withDataType("java.lang.Integer").build();

		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Value");
		classData.addFields(Collections.singletonList(valueID));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);
		fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, codeAttribute);

		List<Instruction> modifiedInstructionSet = new ArrayList<>();
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(0)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(1)//
				.withOpcode(Opcode.GETFIELD).withClassRef("org.testgen.agent.classdata.testclasses.Value")
				.withType(REFERENCE_PROXY).withName("valueID").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(4)//
				.withOpcode(Opcode.INVOKEVIRTUAL).withClassRef(REFERENCE_PROXY_CLASSNAME)//
				.withName("getValue").withType("()Ljava/lang/Object;").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(7)//
				.withOpcode(Opcode.CHECKCAST).withClassRef("java.lang.Integer").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(10)//
				.withOpcode(Opcode.ARETURN).build());

		Assert.assertEquals(modifiedInstructionSet, Instructions.getAllInstructions(methodInfo));

	}

	@Test
	public void testOverrideFieldAccessWithSimpleSetter() throws NotFoundException, BadBytecode {
		init(Value.class, "setCalendar", Arrays.asList(Opcode.GETFIELD, Opcode.PUTFIELD));

		FieldData valueID = new FieldData.Builder().withName("calendar")//
				.withDataType("java.util.Calendar").build();

		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Value");
		classData.addFields(Collections.singletonList(valueID));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);
		fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, codeAttribute);

		List<Instruction> modifiedInstructionSet = new ArrayList<>();
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(0)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(1)//
				.withOpcode(Opcode.GETFIELD).withClassRef("org.testgen.agent.classdata.testclasses.Value")
				.withType(REFERENCE_PROXY).withName("calendar").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(4)//
				.withOpcode(Opcode.ALOAD_1).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(5)//
				.withOpcode(Opcode.INVOKEVIRTUAL).withClassRef(REFERENCE_PROXY_CLASSNAME)//
				.withName("setValue").withType("(Ljava/lang/Object;)V").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(8)//
				.withOpcode(Opcode.RETURN).build());

		Assert.assertEquals(modifiedInstructionSet, Instructions.getAllInstructions(methodInfo));
	}

	@Test
	public void testOverrideFieldAccessWithSimpleSetterAndIfInstruction() throws NotFoundException, BadBytecode {
		init(Value.class, "setValueID", Arrays.asList(Opcode.GETFIELD, Opcode.PUTFIELD));

		FieldData valueID = new FieldData.Builder().withName("valueID")//
				.withDataType("java.lang.Integer").build();

		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Value");
		classData.addFields(Collections.singletonList(valueID));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);
		fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, codeAttribute);

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		List<Instruction> modifiedInstructionSet = new ArrayList<>();
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(0)//
				.withOpcode(Opcode.ALOAD_1).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(1)//
				.withOpcode(Opcode.IFNULL).withOffset(11).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(4)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(5)//
				.withOpcode(Opcode.GETFIELD).withClassRef("org.testgen.agent.classdata.testclasses.Value")
				.withType(REFERENCE_PROXY).withName("valueID").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(8)//
				.withOpcode(Opcode.ALOAD_1).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(9)//
				.withOpcode(Opcode.INVOKEVIRTUAL).withClassRef(REFERENCE_PROXY_CLASSNAME)//
				.withName("setValue").withType("(Ljava/lang/Object;)V").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(12)//
				.withOpcode(Opcode.RETURN).build());

		Assert.assertEquals(modifiedInstructionSet, Instructions.getAllInstructions(methodInfo));
	}

	@Test
	public void testChangeFieldInitalizationWithAnotherConstructorCall() throws NotFoundException, BadBytecode {
		init(Value.class, MethodInfo.nameInit, "()V", Arrays.asList(Opcode.GETFIELD, Opcode.PUTFIELD));

		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Value");

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);
		fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, codeAttribute);

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		List<Instruction> modifiedInstructionSet = new ArrayList<>();
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(0)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(1)//
				.withOpcode(Opcode.ACONST_NULL).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(2)//
				.withOpcode(Opcode.ACONST_NULL).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(3)//
				.withOpcode(Opcode.INVOKESPECIAL).withClassRef("org.testgen.agent.classdata.testclasses.Value")//
				.withName("<init>").withType("(Ljava/lang/Integer;Ljava/util/Calendar;)V").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(6)//
				.withOpcode(Opcode.RETURN).build());

		Assert.assertEquals(modifiedInstructionSet, Instructions.getAllInstructions(methodInfo));

	}

	@Test
	public void testChangeFieldInitalization() throws NotFoundException, BadBytecode {
		init(Adresse.class, MethodInfo.nameInit, Arrays.asList(Opcode.GETFIELD, Opcode.PUTFIELD));

		FieldData fieldStrasse = new FieldData.Builder().withName("strasse").withDataType("java.lang.String").build();
		FieldData fieldHausnummer = new FieldData.Builder().withName("hausnummer").withDataType("short").build();
		FieldData fieldOrt = new FieldData.Builder().withName("ort").withDataType("java.lang.String").build();
		FieldData fieldPlz = new FieldData.Builder().withName("plz").withDataType("int").build();

		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Adresse");
		classData.addFields(Arrays.asList(fieldStrasse, fieldHausnummer, fieldOrt, fieldPlz));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);
		fieldTypeChanger.changeFieldInitialization(instructions, filteredInstructions.get(Opcode.PUTFIELD),
				codeAttribute);

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		List<Instruction> modifiedInstructionSet = new ArrayList<>();
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(0)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(1)//
				.withOpcode(Opcode.INVOKESPECIAL).withClassRef("java.lang.Object")//
				.withName(MethodInfo.nameInit).withType("()V").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(4)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(5)//
				.withOpcode(Opcode.NEW).withClassRef(INTEGER_PROXY_CLASSNAME).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(8)//
				.withOpcode(Opcode.DUP).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(9)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(10)//
				.withOpcode(Opcode.LDC).withConstantValue("hausnummer").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(12)//
				.withOpcode(Opcode.GETSTATIC).withClassRef("java.lang.Short")//
				.withName("TYPE").withType("Ljava/lang/Class;").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(15)//
				.withOpcode(Opcode.INVOKESPECIAL).withClassRef(INTEGER_PROXY_CLASSNAME)//
				.withName(MethodInfo.nameInit)//
				.withType("(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(18)//
				.withOpcode(Opcode.PUTFIELD).withClassRef("org.testgen.agent.classdata.testclasses.Adresse")//
				.withName("hausnummer").withType(INTEGER_PROXY).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(21)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(22)//
				.withOpcode(Opcode.NEW).withClassRef(REFERENCE_PROXY_CLASSNAME).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(25)//
				.withOpcode(Opcode.DUP).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(26)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(27)//
				.withOpcode(Opcode.LDC).withConstantValue("strasse").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(29)//
				.withOpcode(Opcode.LDC).withConstantValue("java.lang.String").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(31)//
				.withOpcode(Opcode.INVOKESPECIAL).withClassRef(REFERENCE_PROXY_CLASSNAME)//
				.withName(MethodInfo.nameInit)//
				.withType("(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(34)//
				.withOpcode(Opcode.PUTFIELD).withClassRef("org.testgen.agent.classdata.testclasses.Adresse")//
				.withName("strasse").withType(REFERENCE_PROXY).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(37)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(38)//
				.withOpcode(Opcode.NEW).withClassRef(REFERENCE_PROXY_CLASSNAME).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(41)//
				.withOpcode(Opcode.DUP).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(42)//
				.withOpcode(Opcode.ALOAD_1).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(43)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(44)//
				.withOpcode(Opcode.LDC).withConstantValue("ort").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(46)//
				.withOpcode(Opcode.LDC).withConstantValue("java.lang.String").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(48)//
				.withOpcode(Opcode.INVOKESPECIAL).withClassRef(REFERENCE_PROXY_CLASSNAME)//
				.withName(MethodInfo.nameInit)//
				.withType("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(51)//
				.withOpcode(Opcode.PUTFIELD).withClassRef("org.testgen.agent.classdata.testclasses.Adresse")//
				.withName("ort").withType(REFERENCE_PROXY).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(54)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(55)//
				.withOpcode(Opcode.NEW).withClassRef(INTEGER_PROXY_CLASSNAME).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(58)//
				.withOpcode(Opcode.DUP).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(59)//
				.withOpcode(Opcode.ILOAD_2).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(60)//
				.withOpcode(Opcode.ALOAD_0).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(61)//
				.withOpcode(Opcode.LDC).withConstantValue("plz").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(63)//
				.withOpcode(Opcode.GETSTATIC).withClassRef("java.lang.Integer")//
				.withName("TYPE").withType("Ljava/lang/Class;").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(66)//
				.withOpcode(Opcode.INVOKESPECIAL).withClassRef(INTEGER_PROXY_CLASSNAME)//
				.withName(MethodInfo.nameInit)//
				.withType("(ILjava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V").build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(69)//
				.withOpcode(Opcode.PUTFIELD).withClassRef("org.testgen.agent.classdata.testclasses.Adresse")//
				.withName("plz").withType(INTEGER_PROXY).build());
		modifiedInstructionSet.add(new Instruction.Builder().withCodeArrayIndex(72)//
				.withOpcode(Opcode.RETURN).build());
		Assert.assertEquals(Instructions.getAllInstructions(methodInfo), modifiedInstructionSet);
	}

	/*
	 * If this test doesn't throw an exception it was successful
	 */
	@Test
	public void testOverrideFieldAccessTableSwitch() throws NotFoundException, BadBytecode {
		init(Switch.class, "tableSwitch", Arrays.asList(Opcode.GETFIELD));

		FieldData constants = new FieldData.Builder().withName("switchConstants")//
				.withDataType("java.lang.String[]").build();
		FieldData list = new FieldData.Builder().withName("list")//
				.withDataType("java.util.List").build();

		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Switch");
		classData.addFields(Arrays.asList(constants, list));

		System.out.println("Before");
		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, ctClass);
		fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, codeAttribute);

		System.out.println("After");
		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));
	}

	@Test
	public void testChangeFieldDataTypeToProxy() throws NotFoundException, BadBytecode, CannotCompileException {
		init(Person.class);

		for (CtField ctField : ctClass.getDeclaredFields()) {

			FieldTypeChanger.changeFieldDataTypeToProxy(classFile, ctField.getFieldInfo());

			if (ctField.getName().equals("name")) {

				FieldInfo fieldInfo = classFile.getFields().stream().filter(field -> field.getName().equals("name"))
						.findAny().orElse(null);

				Assert.assertEquals(REFERENCE_PROXY, fieldInfo.getDescriptor());

				SignatureAttribute signature = (SignatureAttribute) fieldInfo.getAttribute(SignatureAttribute.tag);
				Assert.assertEquals("Lorg/testgen/runtime/proxy/impl/ReferenceProxy<Ljava/lang/String;>;",
						signature.getSignature());
			} else if (ctField.getName().equals("firstName")) {
				FieldInfo fieldInfo = classFile.getFields().stream()
						.filter(field -> field.getName().equals("firstName")).findAny().orElse(null);

				Assert.assertEquals(REFERENCE_PROXY, fieldInfo.getDescriptor());

				SignatureAttribute signature = (SignatureAttribute) fieldInfo.getAttribute(SignatureAttribute.tag);
				Assert.assertEquals("Lorg/testgen/runtime/proxy/impl/ReferenceProxy<Ljava/lang/String;>;",
						signature.getSignature());
			} else if (ctField.getName().equals("dateOfBirth")) {
				FieldInfo fieldInfo = classFile.getFields().stream()
						.filter(field -> field.getName().equals("dateOfBirth")).findAny().orElse(null);

				Assert.assertEquals(REFERENCE_PROXY, fieldInfo.getDescriptor());

				SignatureAttribute signature = (SignatureAttribute) fieldInfo.getAttribute(SignatureAttribute.tag);
				Assert.assertEquals("Lorg/testgen/runtime/proxy/impl/ReferenceProxy<Ljava/time/LocalDate;>;",
						signature.getSignature());
			} else if (ctField.getName().equals("geschlecht")) {
				FieldInfo fieldInfo = classFile.getFields().stream()
						.filter(field -> field.getName().equals("geschlecht")).findAny().orElse(null);

				Assert.assertEquals(REFERENCE_PROXY, fieldInfo.getDescriptor());

				SignatureAttribute signature = (SignatureAttribute) fieldInfo.getAttribute(SignatureAttribute.tag);
				Assert.assertEquals(
						"Lorg/testgen/runtime/proxy/impl/ReferenceProxy<Lorg/testgen/agent/classdata/testclasses/Person$Geschlecht;>;",
						signature.getSignature());
			} else if (ctField.getName().equals("adressen")) {
				FieldInfo fieldInfo = classFile.getFields().stream().filter(field -> field.getName().equals("adressen"))
						.findAny().orElse(null);

				Assert.assertEquals(REFERENCE_PROXY, fieldInfo.getDescriptor());

				SignatureAttribute signature = (SignatureAttribute) fieldInfo.getAttribute(SignatureAttribute.tag);
				Assert.assertEquals(
						"Lorg/testgen/runtime/proxy/impl/ReferenceProxy<Ljava/util/List<Lorg/testgen/agent/classdata/testclasses/Adresse;>;>;",
						signature.getSignature());
			}
		}

	}

}
