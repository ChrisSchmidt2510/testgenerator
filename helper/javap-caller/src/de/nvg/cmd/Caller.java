package de.nvg.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Caller {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
				// D:\\git\\testgenerator\\helper\\javaagent-sample-app\\DUMP_CLASS_FILES\\java\\lang\\invoke\\LambdaForm$MH036.class
				"D:\\JDK\\jdk-13.0.2+8\\bin\\javap.exe -c -v -s -p D:\\git\\testgenerator\\testgenerator-agent\\target\\test-classes\\org\\testgen\\agent\\classdata\\testclasses\\Adresse.class");
		Process p = builder.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while (true) {
			line = r.readLine();
			if (line == null) {
				break;
			}
			System.out.println(line);
		}
	}

}
