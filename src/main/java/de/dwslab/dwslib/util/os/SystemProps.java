package de.dwslab.dwslib.util.os;

/**
 * Some infos about the operating system. <br>
 * Run {@link SystemProps#main(String[])} for short demo.
 * @author Michael Schuhmacher
 *
 */
public class SystemProps {

	public static String getOsName() {
		return System.getProperty("os.name", "unknown");
	}

	public static boolean isWindows() {
		return getOsName().toLowerCase().contains("windows");
	}

	public static boolean isLinux() {
		return getOsName().toLowerCase().contains("linux");
	}
	
	public static boolean is64Bit() {
		return System.getProperty("sun.arch.data.model").contains("64");
	}
	
	public static boolean is32Bit() {
		return System.getProperty("sun.arch.data.model").contains("32");
	}
	
	public static String getBits() {
		return System.getProperty("sun.arch.data.model");
	}

	/**
	 * Run for short demo.
	 * @param args
	 */
	public static void main(String[] args) {
		System.getProperties().list(System.out);
		
		System.out.println(SystemProps.getOsName());
		System.out.println("Is Windows: " + SystemProps.isWindows());
		
		System.out.println(SystemProps.getBits());
		System.out.println("Is 64Bit: " + SystemProps.is64Bit());
	}

}
