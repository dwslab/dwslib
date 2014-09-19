package de.dwslab.dwslib.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

/**
 * This class includes useful functions to get Streams/Readers from files
 * without taking care of the compression type. Currently ZIP, GZIP, XZ and BZ2 are
 * supported beside plain text files.
 * 
 * @author Robert Meusel (robert@informatik.uni-mannheim.de)
 * 
 */
public class InputUtil {

	/**
	 * Default encoding of the InputUtil.class
	 */
	public static String DEFAULT_ENCODING = "utf-8";

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
	 * @throws IOException
	 *             if the file could not be processed/read.
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
	 * Returns the BufferedReader for a given file. Internally
	 * {@link InputUtil#getBufferedReader(File, String)} is used with the
	 * {@link InputUtil#DEFAULT_ENCODING}.
	 * 
	 * @param File
	 *            which should be read
	 * @return {@link BufferedReader} for the given File
	 * @throws IOException
	 *             If file was not found, is a directory or the stream could not
	 *             be opened.
	 */
	public static BufferedReader getBufferedReader(File f) throws IOException {

		return getBufferedReader(f, DEFAULT_ENCODING);
	}

	/**
	 * Returns an {@link InputStream} for a given {@link File}. Depending on the
	 * ending of the file the corresponding {@link InputStream} is chosen.
	 * Currently the compression types GZIP, ZIP and BZ2 are supported. All
	 * other files will be read as plain files.
	 * 
	 * @param f
	 *            the {@link File} which should be read.
	 * @return {@link InputStream} for the given {@link File}
	 * @throws IOException
	 *             if the file could not be found, is a directory or is no
	 *             stream could not be opened.
	 */
	public static InputStream getInputStream(File f) throws IOException {
		InputStream is;
		if (!f.isFile()) {
			throw new IOException("Inputfile is not a file but a directory.");
		}
		if (f.getName().endsWith(".gz")) {
			is = new GZIPInputStream(new FileInputStream(f));
		} else if (f.getName().endsWith(".zip")) {
			is = new ZipInputStream(new FileInputStream(f));
		} else if (f.getName().endsWith(".bz2")) {
			is = new BZip2CompressorInputStream(new FileInputStream(f));
		} else if (f.getName().endsWith(".xz")) {
			is = new XZCompressorInputStream(new FileInputStream(f));
		} else {
			is = new FileInputStream(f);
		}
		return is;
	}

	/**
	 * Returns a {@link BufferedReader} for a given {@link File}. Depending on
	 * the compression of the file, the corresponding {@link InputStream} is
	 * used. Internally {@link InputUtil#getInputStream(File)} is used.
	 * 
	 * @param File
	 *            which should be read
	 * @param encoding
	 *            the encoding to be used, if NULL the
	 *            {@link InputUtil#DEFAULT_ENCODING} is used.s
	 * @return {@link BufferedReader} for the given file
	 * @throws IOException
	 *             if the file could not be found, is a directory or is no
	 *             stream could not be opened.
	 */
	public static BufferedReader getBufferedReader(File f, String encoding)
			throws IOException {
		if (encoding == null) {
			encoding = DEFAULT_ENCODING;
		}
		return new BufferedReader(new InputStreamReader(getInputStream(f),
				encoding));
	}

	/**
	 * Returns a {@link BufferedReader} for a {@link Collection} of {@link File}
	 * s. Each file is opened using an {@link InputStream} obtained internally
	 * from {@link InputUtil#getInputStream(File)} which are all combined using
	 * a {@link SequenceInputStream} packed into a {@link BufferedReader}.
	 * Please note, that the streams in the single {@link Reader} do not
	 * necessary need to be sorted.
	 * 
	 * @param files
	 *            {@link Collection} of {@link File}s which should be read using
	 *            one {@link BufferedReader}.
	 * @return {@link BufferedReader} combining {@link InputStream} from all
	 *         {@link File}s in the {@link Collection}
	 * @throws IOException
	 *             if for any given file the file could not be found, the file
	 *             is a directory or the stream could not be opend.
	 */
	public static BufferedReader getBufferedReader(Collection<File> files)
			throws IOException {
		List<InputStream> streamsToProcess = new ArrayList<InputStream>(
				files.size());
		for (File f : files) {
			streamsToProcess.add(getInputStream(f));
		}
		return new BufferedReader(new InputStreamReader(
				new SequenceInputStream(
						Collections.enumeration(streamsToProcess))));
	}
}
