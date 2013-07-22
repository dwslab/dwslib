package de.uni_mannheim.informatik.dws.dwslib.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import de.uni_mannheim.informatik.dws.dwslib.util.model.OutputType;

/**
 * This class wraps a {@link BufferedWriter} which makes use of an underlying
 * {@link GZIPOutputStream}. Whenever an output file reaches a certain size, a
 * new {@link File} will be created using a defined name-schema and a continues
 * numbering.
 * 
 * @author Robert Meusel
 * 
 */
public class BufferedChunkingWriter {

	private BufferedWriter currentWriter;
	private long maxFileSize;
	private String nameScheme;
	private int nextChunk;
	private File outputDir;
	private File currentOutputFile;
	private OutputType outputType = OutputType.GZIP;

	/**
	 * Initializes the writer. Please not that at this point no file or writer
	 * is initialized internally. This will be done at the first time
	 * {@link #write(String)} is called.
	 * 
	 * @param outputDir
	 *            the directory where to write the output files.
	 * @param nameScheme
	 *            the prefix-namescheme to be used.
	 * @param maxFileSize
	 *            the maximal size of the output file in MB. Will internally by
	 *            converted to Bytes (*1024*1024).
	 * @param type
	 *            the Type of the output files.
	 */
	public BufferedChunkingWriter(File outputDir, String nameScheme,
			int maxFileSize, OutputType type) {
		this.maxFileSize = (long) maxFileSize * 1024 * 1024;
		this.nameScheme = nameScheme.trim();
		if (!outputDir.exists()) {
			System.out.println("Output directory does not exist.");
			System.exit(0);
		}
		if (outputDir.isFile()) {
			System.out.println("Output directory is not directoy.");
			System.exit(0);
		}
		this.outputDir = outputDir;
		this.nextChunk = 0;
		this.outputType = type;
	}

	/**
	 * Initializes the writer (default GZIP). Please not that at this point no
	 * file or writer is initialized internally. This will be done at the first
	 * time {@link #write(String)} is called.
	 * 
	 * @param outputDir
	 *            the directory where to write the output files.
	 * @param nameScheme
	 *            the prefix-namescheme to be used.
	 * @param maxFileSize
	 *            the maximal size of the output file in MB. Will internally by
	 *            converted to Bytes (*1024*1024).
	 */
	public BufferedChunkingWriter(File outputDir, String nameScheme,
			int maxFileSize) {
		this(outputDir, nameScheme, maxFileSize,
				OutputType.GZIP);
	}

	/**
	 * Closes the current open writer.
	 * 
	 * @throws IOException
	 *             when something goes wrong.
	 */
	public void close() throws IOException {
		currentWriter.close();
	}

	/**
	 * Writes the given {@link String} into the output file. Makes sure, that
	 * the file does grows to large.
	 * 
	 * @param input
	 *            the String to write.
	 * @throws FileNotFoundException
	 *             If the new file could not be found.
	 * @throws IOException
	 *             If the old stream could not be closed.
	 */
	public void write(String input) throws FileNotFoundException, IOException {
		if (currentOutputFile == null
				|| currentOutputFile.length() > maxFileSize) {
			switchChunk();
		}
		currentWriter.write(input);
	}

	private void switchChunk() throws FileNotFoundException, IOException {
		if (currentWriter != null) {
			try {
				currentWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (outputType == OutputType.GZIP) {
			currentOutputFile = new File(outputDir, nameScheme + "-"
					+ String.format("%05d", nextChunk++) + ".gz");
			currentWriter = new BufferedWriter(new OutputStreamWriter(
					new GZIPOutputStream(
							new FileOutputStream(currentOutputFile))));
		} else if (outputType == OutputType.PLAIN) {
			currentOutputFile = new File(outputDir, nameScheme + "-"
					+ String.format("%05d", nextChunk++));
			currentWriter = new BufferedWriter(
					new FileWriter(currentOutputFile));
		}
	}
}
