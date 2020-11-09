package org.testgen.logging.formatter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.testgen.logging.config.Configuration;

public class PackageFormatter extends Formatter {
	/* Format yyyy-MM-dd hh:mm:ss [Thread-name] [package] level message linebreak */
	private static final String PATTERN = "%tF %tT [%s] [%s] %s %s %n";

	@Override
	public String format(LogRecord record) {
		LocalDateTime ldt = LocalDateTime.now();
		Configuration config = (Configuration) record.getParameters()[0];

		if (record.getThrown() != null) {
			String message = null;

			try (StringWriter strWriter = new StringWriter(); //
					PrintWriter pw = new PrintWriter(strWriter)) {
				pw.println();
				record.getThrown().printStackTrace(pw);

				message = strWriter.toString();
			} catch (IOException e) {
				// nothing to do cause no exception can be thrown
			}
			return String.format(PATTERN, ldt, ldt, Thread.currentThread().getName(), config.getPackageName(),
					config.getLevel(), message);
		} else {
			return String.format(PATTERN, ldt, ldt, Thread.currentThread().getName(), config.getPackageName(),
					config.getLevel(), record.getMessage());
		}
	}

}
