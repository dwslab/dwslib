package de.dwslab.dwslib.util.text;

import java.util.List;

/**
 * Build one string from other data structures
 * @author Michael Schuhmacher
 *
 */
public class StringManipulator {
	
	/**
	 * 
	 * @param stringList Like ArrayList<String>
	 * @param seperator Usually "\t"
	 * @return
	 */
	public static StringBuffer combine(List<String> stringList, String seperator) {
		StringBuffer bf = new StringBuffer();
		for (String s : stringList) {
			bf.append(s);
			bf.append(seperator);
		}
		return bf;
	}
	
	/**
	 * 
	 * @param stringList Like ArrayList<String>
	 * @param seperator Usually "\t"
	 * @return
	 */
	public static StringBuffer combine(String[] stringList, String seperator) {
		StringBuffer bf = new StringBuffer();
		for (String s : stringList) {
			bf.append(s);
			bf.append(seperator);
		}
		return bf;
	}

}
