package de.dwslab.dwslib.cli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import de.dwslab.dwslib.util.io.InputUtil;

/**
 * Provides methods to split a GZIP file or all GZIP files in a directory into
 * several GZIP files without decompressing the input file first. The split is
 * done on a line base.
 * 
 * @author Daniel Fleischhacker <daniel@informatik.uni-mannheim.de>
 * @author Robert Meusel (robert@dwslab.de)
 */
public class SplitGZIPFile {
	@Option(name = "-l", usage = "Number of lines per file", required = true)
	private Integer numberOfLines;

	@Option(name = "-i", usage = "GZIPed input file or folder", required = true)
	private File inputFile;

	@Option(name = "-p", usage = "Prefix for output files")
	private String prefix = "out_";

	@Option(name = "-x", usage = "File extension for output files")
	private String fileExtension = ".gz";

	@Option(name = "-o", usage = "Output directory for generated files", required = true)
	private File outputDir;

	/**
	 * Splits the file defined by {@code inputFile} into files having the given
	 * number of lines or a files size. Returns the number of files generated.
	 * 
	 * @return number of files generated
	 * @throws IOException
	 */
	public int splitFiles() throws IOException {
		List<File> files = new ArrayList<File>();
		if (inputFile.isDirectory()) {
			files.addAll(Arrays.asList(inputFile
					.listFiles(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {
							if (name.endsWith(".gz")) {
								return true;
							} else {
								return false;
							}
						}
					})));
		} else {
			files.add(inputFile);
		}

		String line;
		int curNumOfLines = 0;
		int fileCounter = 0;

		BufferedWriter writer = null;

		for (File f : files) {
			BufferedReader reader = InputUtil.getBufferedReader(f, "UTF-8");

			try {
				while ((line = reader.readLine()) != null) {
					if (writer == null) {
						writer = new BufferedWriter(new OutputStreamWriter(
								new GZIPOutputStream(new FileOutputStream(
										outputDir.getAbsolutePath()
												+ File.separator
												+ prefix
												+ String.format("%03d",
														fileCounter)
												+ fileExtension)), "UTF-8"));
						fileCounter++;
						curNumOfLines = 0;
					}

					writer.write(line);
					writer.newLine();
					curNumOfLines++;

					if (curNumOfLines == numberOfLines) {
						writer.close();
						writer = null;
					}
				}

			} catch (Exception e) {
				System.out.println("Could not finish parsing file "
						+ f.getName() + ": " + e.getMessage());
			}
		}

		if (writer != null) {
			writer.close();
		}

		return fileCounter;
	}

	public static void main(String[] args) throws IOException {
		SplitGZIPFile splitter = new SplitGZIPFile();
		CmdLineParser parser = new CmdLineParser(splitter);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("java " + SplitGZIPFile.class.getCanonicalName()
					+ " options...");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();

			return;
		}

		int numOfFiles = splitter.splitFiles();

		System.out.println("Split into " + numOfFiles + " files");
	}

}
