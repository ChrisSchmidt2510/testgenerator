package org.testgen.logging;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.logging.config.Configuration;
import org.testgen.logging.config.Level;
import org.testgen.logging.formatter.PackageFormatter;

public class LoggerTest {

	private TestHandler handler;

	private Logger loggerInfo;

	private Logger loggerTrace;

	@Before
	public void init() {
		PackageFormatter packageFormatter = new PackageFormatter(
				LocalDateTime.of(LocalDate.of(2021, Month.AUGUST, 15), LocalTime.of(11, 30)));

		handler = new TestHandler();
		handler.setFormatter(packageFormatter);
		handler.setLevel(java.util.logging.Level.ALL);

		Configuration infoConfig = new Configuration("org.testgen.logging.test", Level.INFO);
		infoConfig.addHandler(handler);

		loggerInfo = new Logger(infoConfig, LoggerTest.class);

		Configuration traceConfig = new Configuration("org.testgent.logging", Level.TRACE);
		traceConfig.addHandler(handler);

		loggerTrace = new Logger(traceConfig, LoggerTest.class);
	}

	@Test
	public void testLogMessage() throws UnsupportedEncodingException {
		loggerInfo.error("this is a test");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest ERROR this is a test \r\n",
				handler.getLoggedMessage());

		handler.reset();

		loggerInfo.warn("log something");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest WARN log something \r\n",
				handler.getLoggedMessage());
		handler.reset();

		loggerInfo.info("another test");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest INFO another test \r\n", handler.getLoggedMessage());
		handler.reset();

		loggerInfo.debug("this wont be logged");
		Assert.assertTrue(handler.getLoggedMessage().isEmpty());
		handler.reset();

		loggerTrace.debug("this will be logged");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest DEBUG this will be logged \r\n", handler.getLoggedMessage());
		handler.reset();
		
		loggerTrace.trace("foo");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest TRACE foo \r\n", handler.getLoggedMessage());
		handler.reset();	
	}
	
	@Test
	public void testLogExpensiveMessage() throws UnsupportedEncodingException {
		loggerInfo.error("some text", () -> "expensiveMessage");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest ERROR some text\r\nexpensiveMessage \r\n", handler.getLoggedMessage());
		handler.reset();
		
		loggerInfo.warn("this is logged", ()-> "also logged");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest WARN this is logged\r\nalso logged \r\n", handler.getLoggedMessage());
		handler.reset();
		
		loggerInfo.info("for your information", () -> "additional Information");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest INFO for your information\r\nadditional Information \r\n",handler.getLoggedMessage());
		handler.reset();
		
		loggerInfo.trace("wont be logged", () -> "very expensive");
		Assert.assertTrue(handler.getLoggedMessage().isEmpty());
		handler.reset();
		
		loggerTrace.debug("very detailed message", () -> "even more details");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest DEBUG very detailed message\r\neven more details \r\n", handler.getLoggedMessage());
		handler.reset();
		
		loggerTrace.trace("detail overload", ()-> "extrem expensive");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest TRACE detail overload\r\nextrem expensive \r\n", handler.getLoggedMessage());
		handler.reset();
		
		loggerTrace.error(null, ()-> "detailed Information");
		Assert.assertEquals("2021-08-15 11:30:00 [main] LoggerTest ERROR detailed Information \r\n", handler.getLoggedMessage());
		
	}
	
	@Test
	public void testLogException() throws UnsupportedEncodingException {
		HelperObject.catchRuntimeException(e -> loggerInfo.error("expected error occured", e));
		
		String message ="2021-08-15 11:30:00 [main] LoggerTest ERROR expected error occured\r\n"
				+"java.lang.RuntimeException: sth bad happend\r\n"
				+"\tat org.testgen.logging.HelperObject.throwRuntimeException(HelperObject.java:18)\r\n"
				+"\tat org.testgen.logging.HelperObject.catchRuntimeException(HelperObject.java:10)\r\n \r\n";
		Assert.assertEquals(message, handler.getLoggedMessage());
		handler.reset();
		
		HelperObject.catchRuntimeException(e -> loggerInfo.warn("expected error", e));
		
		message  ="2021-08-15 11:30:00 [main] LoggerTest WARN expected error\r\n"
				+"java.lang.RuntimeException: sth bad happend\r\n"
				+"\tat org.testgen.logging.HelperObject.throwRuntimeException(HelperObject.java:18)\r\n"
				+"\tat org.testgen.logging.HelperObject.catchRuntimeException(HelperObject.java:10)\r\n \r\n";
		Assert.assertEquals(message, handler.getLoggedMessage());
		handler.reset();
		
		HelperObject.catchRuntimeException(e -> loggerInfo.info("exception",e));
		
		message  ="2021-08-15 11:30:00 [main] LoggerTest INFO exception\r\n"
				+"java.lang.RuntimeException: sth bad happend\r\n"
				+"\tat org.testgen.logging.HelperObject.throwRuntimeException(HelperObject.java:18)\r\n"
				+"\tat org.testgen.logging.HelperObject.catchRuntimeException(HelperObject.java:10)\r\n \r\n";
		Assert.assertEquals(message, handler.getLoggedMessage());
		handler.reset();
		
		HelperObject.catchRuntimeException(e -> loggerInfo.trace("wont be logged",e));
		
		Assert.assertTrue(handler.getLoggedMessage().isEmpty());
		
		HelperObject.catchRuntimeException(e -> loggerTrace.trace("exception occured", e));
		
		message  ="2021-08-15 11:30:00 [main] LoggerTest TRACE exception occured\r\n"
				+"java.lang.RuntimeException: sth bad happend\r\n"
				+"\tat org.testgen.logging.HelperObject.throwRuntimeException(HelperObject.java:18)\r\n"
				+"\tat org.testgen.logging.HelperObject.catchRuntimeException(HelperObject.java:10)\r\n \r\n";
		Assert.assertEquals(message, handler.getLoggedMessage());
		handler.reset();
		
		HelperObject.catchRuntimeException(e -> loggerTrace.debug("any lookers", e));
		
		message  ="2021-08-15 11:30:00 [main] LoggerTest DEBUG any lookers\r\n"
				+"java.lang.RuntimeException: sth bad happend\r\n"
				+"\tat org.testgen.logging.HelperObject.throwRuntimeException(HelperObject.java:18)\r\n"
				+"\tat org.testgen.logging.HelperObject.catchRuntimeException(HelperObject.java:10)\r\n \r\n";
		Assert.assertEquals(message, handler.getLoggedMessage());
		handler.reset();
		
		HelperObject.catchRuntimeException(e -> loggerTrace.error(null, e));
		
		message ="2021-08-15 11:30:00 [main] LoggerTest ERROR \r\n"
				+"java.lang.RuntimeException: sth bad happend\r\n"
				+"\tat org.testgen.logging.HelperObject.throwRuntimeException(HelperObject.java:18)\r\n"
				+"\tat org.testgen.logging.HelperObject.catchRuntimeException(HelperObject.java:10)\r\n \r\n";
		Assert.assertEquals(message, handler.getLoggedMessage());
		handler.reset();		
	}

	public class TestHandler extends StreamHandler {

		private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		public TestHandler() {
			setOutputStream(outputStream);
		}

		@Override
		public synchronized void publish(LogRecord record) {
			super.publish(record);
			flush();
		}

		public String getLoggedMessage() throws UnsupportedEncodingException {
			return outputStream.toString(StandardCharsets.UTF_8.toString());
		}

		public void reset() {
			outputStream.reset();
		}

	}

}
