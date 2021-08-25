package org.testgen.logging.formatter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.testgen.logging.config.Level;

public class PackageFormatter extends Formatter {
	/* Format yyyy-MM-dd hh:mm:ss [Thread-name] [className] level message linebreak */
	private static final String PATTERN = "%tF %tT [%s] %s %s %s %n";
	
	private LocalDateTime testDateTime;
	
	public PackageFormatter() {
	}
	
	public PackageFormatter(LocalDateTime ldc) {
		this.testDateTime = ldc;
	}

	@Override
	public String format(LogRecord record) {
		LocalDateTime ldt = getLocalDateTime();
		Level level = (Level)record.getParameters()[0];
		String name = (String) record.getParameters()[1];

		if (record.getThrown() != null) {
			String message = record.getMessage();

			try (StringWriter strWriter = new StringWriter(); //
					PrintWriter pw = new PrintWriter(strWriter)) {
				pw.println();
				record.getThrown().printStackTrace(pw);

				message += strWriter.toString();
			} catch (IOException e) {
				// nothing to do cause no exception can be thrown
			}
			return String.format(PATTERN, ldt, ldt, Thread.currentThread().getName(), name,
					level, message);
		} else {
			return String.format(PATTERN, ldt, ldt, Thread.currentThread().getName(), name,
					level, record.getMessage());
		}
	}
	
	public LocalDateTime getLocalDateTime() {
		
		return testDateTime != null ? testDateTime: LocalDateTime.now();
	}

}
