package de.nvg.javaagent.transformer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import de.nvg.javaagent.AgentException;
import de.nvg.javaagent.classdata.FieldTypeChanger;
import de.nvg.javaagent.classdata.Instruction;
import de.nvg.javaagent.classdata.Instructions;
import de.nvg.javaagent.classdata.MethodAnalyser;
import de.nvg.javaagent.classdata.model.ClassData;
import de.nvg.javaagent.classdata.model.ClassDataStorage;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.testgenerator.RuntimeProperties;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BootstrapMethodsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.SignatureAttribute;

public class ClassDataTransformer implements ClassFileTransformer
{

  private static final String EQUALS = "equals";
  private static final String HASHCODE = "hashCode";
  private static final String TO_STRING = "toString";
  private static final String FINALIZE = "finalize";
  private static final String GET_CLASS = "getClass";
  private static final String CLONE = "clone";
  private static final String NOTIFY = "notify";
  private static final String NOTIFY_ALL = "notifyAll";
  private static final String WAIT = "wait";

  private static final String OBJECT = "java.lang.Object";

  private static final List<String> OBJECT_METHODS = Collections.unmodifiableList(
      Arrays.asList(EQUALS, HASHCODE, FINALIZE, TO_STRING, GET_CLASS, CLONE, NOTIFY, NOTIFY_ALL, WAIT));

  // private static final String MAP = "Ljava/util/Map;";

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain, byte[] classfileBuffer)
    throws IllegalClassFormatException
  {

    if (className.startsWith(RuntimeProperties.getInstance().getBlPackage())
        || ClassDataStorage.getInstance().getSuperClassesToLoad() //
            .contains(Descriptor.toJavaName(className)))
    {

      final ClassPool pool = ClassPool.getDefault();

      try
      {
        CtClass loadingClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

        ClassData classData = new ClassData(loadingClass.getName());

        byte[] bytecode = collectAndAlterMetaData(loadingClass, classData);

        ClassDataStorage.getInstance().addClassData(loadingClass.getName(), classData);

        try (FileOutputStream fios = new FileOutputStream(
            new File("D:\\" + className.substring(className.lastIndexOf('/')) + ".class")))
        {
          fios.write(bytecode);
        }

        return bytecode;

      }
      catch (Exception e)
      {
        e.printStackTrace();
        throw new AgentException("Es ist ein Fehler bei der Transfomation aufgetreten", e);
      }
    }

    return classfileBuffer;
  }

  private byte[] collectAndAlterMetaData(CtClass loadingClass, ClassData classData)
    throws Exception
  {
    ClassFile classFile = loadingClass.getClassFile();

    String superClass = classFile.getSuperclass();

    if (!OBJECT.equals(superClass))
    {
      ClassData superClassData = ClassDataStorage.getInstance().getClassData(superClass);

      if (superClassData != null)
      {
        classData.setSuperClass(superClassData);
      }
      else
      {
        ClassDataStorage.getInstance().addSuperClassAfterLoading(superClass,
            superClazz -> classData.setSuperClass(superClazz));
      }
    }

    ConstPool constantPool = classFile.getConstPool();

    if (classFile.getAttribute(BootstrapMethodsAttribute.tag) != null)
    {

      BootstrapMethodsAttribute attribute = (BootstrapMethodsAttribute) classFile
          .getAttribute(BootstrapMethodsAttribute.tag);
      attribute.getMethods();
    }

    if (Modifier.isEnum(loadingClass.getModifiers()))
    {
      classData.setIsEnum(true);
    }
    else
    {
      List<FieldData> fields = getFieldsFromClass(loadingClass, classData);

      // MethodAnalyser methodAnalyser = new MethodAnalyser(fields);

      FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(fields, constantPool, //
          loadingClass);

      fieldTypeChanger.addFieldCalledField();

      checkAndAlterMethods(loadingClass, classFile.getMethods(), null, //
          fieldTypeChanger, classData);

      classData.addFields(fields);
    }

    byte[] bytecode = loadingClass.toBytecode();

    loadingClass.detach();

    return bytecode;
  }

  private List<FieldData> getFieldsFromClass(CtClass loadedClass, ClassData classData)
    throws CannotCompileException,
    NotFoundException
  {
    List<FieldData> fieldsFromClass = new ArrayList<>();

    for (CtField field : loadedClass.getDeclaredFields())
    {

      if (!Instructions.isConstant(field.getModifiers()))
      {
        FieldInfo fieldInfo = field.getFieldInfo();

        SignatureAttribute signature = (SignatureAttribute) fieldInfo.getAttribute(SignatureAttribute.tag);

        FieldData fieldData = new FieldData.Builder()
            .withDataType(Descriptor.toClassName(fieldInfo.getDescriptor())).withName(field.getName())
            .isMutable(!Modifier.isFinal(fieldInfo.getAccessFlags()))
            .isStatic(Modifier.isStatic(fieldInfo.getAccessFlags())).withSignature(signature != null
                ? signature.getSignature()
                : null)
            .build();

        fieldsFromClass.add(fieldData);

        FieldTypeChanger.changeFieldDataTypeToProxy(loadedClass.getClassFile(), fieldInfo);
      }
    }

    return fieldsFromClass;
  }

  private void checkAndAlterMethods(CtClass loadingClass, List<MethodInfo> methods,
                                    MethodAnalyser methodAnalyser, FieldTypeChanger fieldTypeChanger,
                                    ClassData classData)
    throws Exception
  {

    for (MethodInfo method : methods)
    {
      System.out.println(loadingClass.getName() + "." + method.getName());

      if (MethodInfo.nameInit.equals(method.getName()))
      {

        List<Instruction> instructions = Instructions.getAllInstructions(method);

        Map<Integer, List<Instruction>> filteredInstructions = Instructions.getFilteredInstructions(
            instructions, Arrays.asList(Opcode.ALOAD_0, Opcode.PUTFIELD, Opcode.RETURN));

        Map<Integer, Instruction> aloadPutFieldInstructionPairs = createAload0PutFieldInstructionPairs(
            filteredInstructions.get(Opcode.ALOAD_0), filteredInstructions.get(Opcode.PUTFIELD));

        fieldTypeChanger.changeFieldInitialization(aloadPutFieldInstructionPairs,
            filteredInstructions.get(Opcode.RETURN).get(0).getCodeArrayIndex(), method.getCodeAttribute());

        // Map<Integer, FieldData> constructorInitalizedFields =
        // methodAnalyser.analyseConstructor(
        // method.getDescriptor(), filteredInstructions.get(Opcode.PUTFIELD),
        // instructions);

        // if (constructorInitalizedFields.isEmpty())
        // {
        // classData.setHasDefaultConstructor(true);
        // }
        // else
        // {
        // classData.setConstructorInitalizedFields(constructorInitalizedFields);
        // }

      }
      else
      {
        List<Instruction> instructions = Instructions.getAllInstructions(method);

        Map<Integer, List<Instruction>> filteredInstructions = Instructions
            .getFilteredInstructions(instructions, Arrays.asList(Opcode.PUTFIELD, Opcode.GETFIELD));

        fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, //
            method.getCodeAttribute());

        if (!MethodInfo.nameInit.equals(method.getName()) && !OBJECT_METHODS.contains(method.getName()))
        {

          // Wrapper<FieldData> fieldWrapper = new Wrapper<>();

          // MethodData methodData = methodAnalyser.analyse(method.getName(),
          // method.getDescriptor(),
          // method.getAccessFlags(), instructions, fieldWrapper);
          // if (methodData != null)
          // {
          // classData.addMethod(methodData, fieldWrapper.getValue());
          // }

        }
      }
    }
  }

  private static Map<Integer, Instruction> createAload0PutFieldInstructionPairs(List<Instruction> aloadInstructions,
                                                                                List<Instruction> putFieldInstructions)
  {
    Map<Integer, Instruction> map = new LinkedHashMap<>();

    if (putFieldInstructions != null && !putFieldInstructions.isEmpty())
    {
      for (int i = 0; i < putFieldInstructions.size(); i++)
      {
        Instruction instruction = putFieldInstructions.get(i);

        map.put(aloadInstructions.get(i + 1).getCodeArrayIndex(), instruction);
      }
    }

    return map;

  }
}
