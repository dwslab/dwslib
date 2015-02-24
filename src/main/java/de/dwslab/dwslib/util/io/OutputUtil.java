package de.dwslab.dwslib.util.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

/**
 * This class includes useful functions to produce output.
 * 
 * @author Robert
 * 
 */
public class OutputUtil {

	/**
	 * Returns a {@link BufferedWriter} based on an underlying
	 * {@link GZIPOutputStream} for a given {@link File} using UTF-8 encoding.
	 * For different encodings use {@link #getGZIPBufferedWriter(File, String)}.
	 * 
	 * @param f
	 *            {@link File} which should be written to.
	 * @return {@link BufferedWriter}
	 * @throws FileNotFoundException
	 *             , if the file does not exists.
	 * @throws IOException
	 *             , if the file cannot be opened for writing.
	 */
	public static BufferedWriter getGZIPBufferedWriter(File f)
			throws FileNotFoundException, IOException {
		return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(
				new FileOutputStream(f)), "UTF-8"));
	}

	/**
	 * Returns a {@link BufferedWriter} based on an underlying
	 * {@link GZIPOutputStream} for a given {@link File}.
	 * 
	 * @param f
	 *            {@link File} which should be written to.
	 * @param encoding
	 *            set the encoding
	 * @return {@link BufferedWriter}
	 * @throws FileNotFoundException
	 *             , if the file does not exists.
	 * @throws IOException
	 *             , if the file cannot be opened for writing.
	 */
	public static BufferedWriter getGZIPBufferedWriter(File f, String encoding)
			throws FileNotFoundException, IOException {
		return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(
				new FileOutputStream(f)), encoding));
	}

	public static void printLogo() {
		System.out.println(LOGO);
	}

	public static String getLogo() {
		return LOGO;
	}

	private static String LOGO = "		\r\n"
			+ "		                                                    /\r\n"
			+ "		                                                  .7\r\n"
			+ "		                                       \\       , //\r\n"
			+ "		                                       |\\.--._/|//\r\n"
			+ "		                                      /\\ ) ) ).'/\r\n"
			+ "		                                     /(  \\  // /\r\n"
			+ "		                                    /(   J`((_/ \\\r\n"
			+ "		                                   / ) | _\\     /\r\n"
			+ "		                                  /|)  \\  eJ    L\r\n"
			+ "		                                 |  \\ L \\   L   L\r\n"
			+ "		                                /  \\  J  `. J   L\r\n"
			+ "		                                |  )   L   \\/   \\\r\n"
			+ "		                               /  \\    J   (\\   /\r\n"
			+ "		             _....___         |  \\      \\   \\```\r\n"
			+ "		      ,.._.-'        '''--...-||\\     -. \\   \\\r\n"
			+ "		    .'.=.'                    `         `.\\ [ Y\r\n"
			+ "		   /   /                                  \\]  J\r\n"
			+ "		  Y / Y                                    Y   L\r\n"
			+ "		  | | |          \\                         |   L\r\n"
			+ "		  | | |           Y                        A  J\r\n"
			+ "		  |   I           |                       /I\\ /\r\n"
			+ "		  |    \\          I             \\        ( |]/|\r\n"
			+ "		  J     \\         /._           /        -tI/ |\r\n"
			+ "		   L     )       /   /'-------'J           `'-:.\r\n"
			+ "		   J   .'      ,'  ,' ,     \\   `'-.__          \\\r\n"
			+ "		    \\ T      ,'  ,'   )\\    /|        ';'---7   /\r\n"
			+ "		     \\|    ,'L  Y...-' / _.' /         \\   /   /\r\n"
			+ "		      J   Y  |  J    .'-'   /         ,--.(   /\r\n"
			+ "		       L  |  J   L -'     .'         /  |    /\\\r\n"
			+ "		       |  J.  L  J     .-;.-/       |    \\ .' /\r\n"
			+ "		       J   L`-J   L____,.-'`        |  _.-'   |\r\n"
			+ "		        L  J   L  J                  ``  J    |\r\n"
			+ "		        J   L  |   L                     J    |\r\n"
			+ "		         L  J  L    \\                    L    \\\r\n"
			+ "		         |   L  ) _.'\\                    ) _.'\\\r\n"
			+ "		         L    \\('`    \\                  ('`    \\\r\n"
			+ "		          ) _.'\\`-....'                   `-....'\r\n"
			+ "		         ('`    \\\r\n"
			+ "		          `-.___/   \r\n"
			+ "				  \r\n"
			+ " ________  __      __  _________          .____       _____ __________ \r\n"
			+ " \\______ \\/  \\    /  \\/   _____/          |    |     /  _  \\\\______   \\\r\n"
			+ "  |    |  \\   \\/\\/   /\\_____  \\   ______  |    |    /  /_\\  \\|    |  _/\r\n"
			+ "  |    `   \\        / /        \\ /_____/  |    |___/    |    \\    |   \\\r\n"
			+ " /_______  /\\__/\\  / /_______  /          |_______ \\____|__  /______  /\r\n"
			+ "         \\/      \\/          \\/                   \\/       \\/       \\/ \r\n"
			+ "	";

}
