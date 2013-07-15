package de.uni_mannheim.informatik.dws.dwslib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class InputUtil {

	/**
	 * Returns a list of files, which could be the input file itself (if its a
	 * file) or all included files (not directories) if the input is a
	 * directory.
	 * 
	 * @param fileOrDir
	 *            the name of a file or directory.
	 * @return A {@link List} of all {@link File}s
	 * @throws FileExistsException
	 *             if input file/directory does not exist.
	 */
	public static List<File> getFileList(String fileOrDir) {
		List<File> filesToProcess = new ArrayList<File>();
		File f = new File(fileOrDir);
		if (!f.exists()) {
			System.out.println("File " + f.getAbsolutePath()
					+ " does not exit.");
		} else {
			if (f.isDirectory()) {
				for (File file : f.listFiles()) {
					if (file.isFile()) {
						filesToProcess.add(file);
					}
				}
			} else {
				filesToProcess.add(f);
			}
		}
		return filesToProcess;

	}

	/**
	 * Returns a list of file references, which could be the input file itself
	 * (if its a file) or all included files (not directories) if the input is a
	 * directory.
	 * 
	 * @param fileOrDir
	 *            the name of a file or directory.
	 * @return A {@link List} of all {@link File}s
	 * @throws IOException if the file could not be processed/read.
	 * @throws FileNotFoundExceptione
	 *             if input file/directory does not exist.
	 */
	public static List<String> getFileReferenceList(String fileOrFileListOrDir)
			throws IOException {

		File f = new File(fileOrFileListOrDir);
		if (!f.exists()) {
			throw new FileNotFoundException("File " + f.getAbsolutePath()
					+ " does not exist.");
		} else {
			List<String> filesToProcess = new ArrayList<String>();
			if (f.isDirectory()) {
				for (File file : f.listFiles()) {
					if (file.isFile()) {
						filesToProcess.add(file.getAbsolutePath());
					}
				}
			} else if (f.getAbsolutePath().endsWith(".list")) {
				BufferedReader br;
				try {
					br = getBufferedReader(f);
					while (br.ready()) {
						filesToProcess.add(br.readLine());
					}
				} catch (IOException e) {
					throw new IOException("File " + f.getAbsolutePath()
							+ " could not be processed.");
				}

			} else {
				filesToProcess.add(f.getAbsolutePath());
			}
			return filesToProcess;
		}

	}

	/**
	 * Returns the most likely fitting {@link BufferedReader} for a given file.
	 * 
	 * @param File
	 *            which should be read
	 * @return {@link BufferedReader}
	 * @throws IOException
	 *             If file was not found or Stream could not be opened.
	 */
	public static BufferedReader getBufferedReader(File f) throws IOException {
		BufferedReader br;
		if (f.getName().endsWith("gz")) {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(
					new FileInputStream(f))));
		} else {
			br = new BufferedReader(new FileReader(f));
		}

		return br;
	}
}

