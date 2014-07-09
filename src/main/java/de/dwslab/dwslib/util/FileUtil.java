package de.dwslab.dwslib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class FileUtil {

	/**
	 * ZCat like function reading a file and printing the first lines. Currently
	 * supports plain textfile and gzip files. Internally
	 * {@link InputUtil#getBufferedReader(File)} is used to read from the file.
	 * 
	 * @param filename
	 *            the full qualified name of the file.
	 * @param num
	 *            the number of lines to be printed out.
	 * @throws IOException
	 *             if reading the file failes.
	 */
	public static void readFirstLinesOfFile(String filename, int num)
			throws IOException {
		BufferedReader br = InputUtil.getBufferedReader(new File(filename));
		int cnt = 0;
		while (br.ready() && cnt < num) {
			System.out.println(br.readLine());
			cnt++;
		}
		if (cnt == num) {
			System.out.println("Printed out first " + num + " lines.");
		} else {
			System.out.println("File has less than " + num
					+ " lines. Printed first " + cnt + " lines.");
		}
		br.close();
	}

}
