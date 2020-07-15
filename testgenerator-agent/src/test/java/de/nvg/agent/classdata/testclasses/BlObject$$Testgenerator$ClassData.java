package de.nvg.agent.classdata.testclasses;

import java.time.LocalDate;
import java.util.List;

import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.ConstructorData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SignatureData;

public class BlObject$$Testgenerator$ClassData {

	private final ClassData testgenerator$classData;

	public BlObject$$Testgenerator$ClassData() {
		testgenerator$classData = new ClassData("de.nvg.BlObject", new ConstructorData(true));
		testgenerator$classData.addFieldSetterPair(new FieldData(false, "erdat", LocalDate.class),
				new SetterMethodData("setErdat", "(Ljava/time/LocalDate;)V", false));

		FieldData ersb = new FieldData(false, "ersb", String.class);
		SignatureData signature = new SignatureData(List.class);
		signature.addSubType(new SignatureData(LocalDate.class));
		testgenerator$classData.addFieldSetterPair(ersb,
				new SetterMethodData("setErsb", "(Ljava/lang/String)V;", false));
	}

}
