package de.nvg.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Caller
{

  public static void main(String[] args)
    throws IOException
  {
    // D:\\workspace\\javaagent-sample-app\\target\\classes\\de\\nvg\\bl\\partner\\Person.class
    // D:\\git\\testgenerator\\testgenerator-agent\\target\\test-classes\\de\\nvg\\javaagent\\classdata\\modify\\testclasses\\Adresse.class
    ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
        "%JAVA_HOME%\\bin\\javap.exe -c -v -p D:\\Schaden.class");
    Process p = builder.start();
    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line;
    while (true)
    {
      line = r.readLine();
      if (line == null)
      {
        break;
      }
      System.out.println(line);
    }
  }

}
