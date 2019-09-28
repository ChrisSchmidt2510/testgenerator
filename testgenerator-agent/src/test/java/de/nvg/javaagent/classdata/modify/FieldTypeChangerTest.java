package de.nvg.javaagent.classdata.modify;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import de.nvg.javaagent.classdata.Instruction;
import de.nvg.javaagent.classdata.Instructions;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.javaagent.classdata.modification.fields.FieldTypeChanger;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class FieldTypeChangerTest
{

  private ClassFile classFile;
  private CtClass ctClass;
  private ConstPool constantPool;
  private List<Instruction> instructions;
  private Map<Integer, List<Instruction>> filteredInstructions;
  private CodeAttribute codeAttribute;

  @Before
  public void init()
    throws NotFoundException,
    BadBytecode
  {
    ClassPool classPool = ClassPool.getDefault();

    ctClass = classPool.get("de.nvg.javaagent.classdata.modify.testclasses.PartnerEigenschaft");

    classFile = ctClass.getClassFile();

    MethodInfo method = classFile.getMethod("setPartnerZuordnung");

    codeAttribute = method.getCodeAttribute();

    instructions = Instructions.getAllInstructions(method);

    filteredInstructions = Instructions.getFilteredInstructions(instructions,
        Arrays.asList(Opcode.GETFIELD, Opcode.PUTFIELD, Opcode.RETURN));

    constantPool = method.getConstPool();
  }

  @Test
  public void testOverrideFieldAccess()
    throws BadBytecode
  {
    FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(
        Arrays.asList(new FieldData.Builder().withName("partnerZuordnung")
            .withDataType("de.nvg.javaagent.classdata.modify.testclasses.PartnerZuordnung").build()),
        constantPool, ctClass);

    fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, codeAttribute);
  }

}
