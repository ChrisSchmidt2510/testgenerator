package de.nvg.testgenerator.logfile.analysis.agent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Test;

public class AgentLogTest {

	@Test
	public void testProcessMethodAnalysisLog() throws IOException, URISyntaxException {
		URL logFile = this.getClass().getResource("/Agent.log");

		AgentLog.processMethodAnalysisLog(Paths.get(logFile.toURI()));
	}

}
