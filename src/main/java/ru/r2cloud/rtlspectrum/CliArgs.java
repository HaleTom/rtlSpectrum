package ru.r2cloud.rtlspectrum;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CliArgs {

	public enum OpType {
		ADD, SUBTRACT
	}

	public static class Operation {
		private final OpType type;
		private final File file;

		public Operation(OpType type, File file) {
			this.type = type;
			this.file = file;
		}

		public OpType getType() {
			return type;
		}

		public File getFile() {
			return file;
		}
	}

	public static class Parsed {
		private File baseFile;
		private final List<Operation> operations = new ArrayList<>();
		private boolean helpRequested;
		private String error;

		public File getBaseFile() {
			return baseFile;
		}

		public List<Operation> getOperations() {
			return Collections.unmodifiableList(operations);
		}

		public boolean isHelpRequested() {
			return helpRequested;
		}

		public String getError() {
			return error;
		}

		public boolean hasError() {
			return error != null;
		}
	}

	public static Parsed parse(String[] args) {
		Parsed result = new Parsed();
		List<String> naked = new ArrayList<>();
		int i = 0;
		while (i < args.length) {
			String arg = args[i];
			if (arg.equals("--help") || arg.equals("-h")) {
				result.helpRequested = true;
				return result;
			} else if (arg.equals("--add")) {
				if (i + 1 >= args.length || args[i + 1].charAt(0) == '-') {
					result.error = "--add requires a file argument";
					return result;
				}
				result.operations.add(new Operation(OpType.ADD, new File(args[i + 1])));
				i += 2;
			} else if (arg.equals("--subtract")) {
				if (i + 1 >= args.length || args[i + 1].charAt(0) == '-') {
					result.error = "--subtract requires a file argument";
					return result;
				}
				result.operations.add(new Operation(OpType.SUBTRACT, new File(args[i + 1])));
				i += 2;
			} else if (arg.startsWith("-")) {
				result.error = "Unknown option: " + arg + "\n" + usage();
				return result;
			} else {
				naked.add(arg);
				i++;
			}
		}
		if (naked.size() > 1) {
			result.error = "More than one file argument is not allowed\n" + usage();
			return result;
		}
		if (!naked.isEmpty()) {
			result.baseFile = new File(naked.get(0));
		}
		if (!result.operations.isEmpty() && result.baseFile == null) {
			result.error = "A base file argument is required when using --add or --subtract\n" + usage();
			return result;
		}
		return result;
	}

	public static String usage() {
		return "Usage: rtlSpectrum [file] [--add file]... [--subtract file]... [--help]\n"
				+ "  file              CSV file to open as if Load File was pressed\n"
				+ "  --add file        Add file as if Add menu was used (requires base file, repeatable, applied in order)\n"
				+ "  --subtract file   Subtract file as if Subtract menu was used (requires base file, repeatable, applied in order)\n"
				+ "  --help, -h        Show this usage and exit";
	}
}
