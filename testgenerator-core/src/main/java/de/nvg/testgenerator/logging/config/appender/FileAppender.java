package de.nvg.testgenerator.logging.config.appender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Consumer;

public class FileAppender implements Appender {
	private static final String FILE_ENDING = ".log";

	private final String name;
	private final String path;
	private final int maxFileSize;
	private final Appender parent;
	private FileOutputStream outputStream;
	private PrintStream printStream;

	private long currentFileSize;

	private int logFileNumber = 1;

	public FileAppender(String name, int maxFileSize, String path) {
		this(name, maxFileSize, path, null);
	}

	public FileAppender(String name, int maxFileSize, String path, Appender parent) {
		this.name = name;
		this.path = path;
		this.maxFileSize = maxFileSize;
		this.parent = parent;

		File logFile = createLogDirectory(path, name);

		currentFileSize = logFile.length();

		try {
			outputStream = new FileOutputStream(logFile, true);
			printStream = new PrintStream(outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(String message) {
		try {
			if (maxFileSize < currentFileSize) {
				outputStream.close();

				File newLogFile = createLogDirectory(path, name + String.valueOf(logFileNumber++));
				outputStream = new FileOutputStream(newLogFile, true);
				currentFileSize = 0;
			}

			byte[] bytes = message.getBytes();

			currentFileSize += bytes.length;

			outputStream.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (parent != null) {
			parent.write(message);
		}

	}

	@Override
	public void write(Throwable throwable) {
		throwable.printStackTrace(printStream);

		if (parent != null) {
			parent.write(throwable);
		}
	}

	@Override
	public void write(Consumer<PrintStream> message) {
		message.accept(printStream);

		if (parent != null) {
			parent.write(message);
		}

	}

	@Override
	public void close() throws IOException {
		outputStream.close();
	}

	private File createLogDirectory(String path, String logFileName) {
		File file = new File(path + File.separator + logFileName + FILE_ENDING);

		if (!file.exists()) {
			file.getParentFile().mkdirs();
		} else if (maxFileSize < file.length()) {
			file = createLogDirectory(path, name + String.valueOf(logFileNumber++));
		}

		return file;
	}

}
