package de.dwslab.dwslib.util.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Collections2;

/**
 * Tools for manipulating tokens in strings
 * @author Michael Schuhmacher
 *
 */
public class TokenManipulator {
	
	/**
	 * Generates all permutation for the tokens, eg. <br>
	 * Input: Red Blood Cells <br>
	 * Output: <br>
	 * Red Blood Cells <br>
	 * Red Cells Blood <br>
	 * Cells Red Blood <br>
	 * Cells Blood Red <br>
	 * Blood Cells Red <br>
	 * Blood Red Cells <br>
	 * @param string
	 * @return
	 */
	public static List<String> getAllTokenPermutations(String string, String tokenSeparator) {
		return getAllTokenPermutations(string.split(tokenSeparator));
	}
	
	/**
	 * Generates all permutation for the tokens, eg. <br>
	 * Input: Red Blood Cells <br>
	 * Output: <br>
	 * Red Blood Cells <br>
	 * Red Cells Blood <br>
	 * Cells Red Blood <br>
	 * Cells Blood Red <br>
	 * Blood Cells Red <br>
	 * Blood Red Cells <br>
	 * @param stringArray
	 * @return List of combined strings
	 */
	public static List<String> getAllTokenPermutations(String[] stringArray) {
		List<String> list = new ArrayList<String>(Arrays.asList(stringArray));
		 Collection<List<String>> res = Collections2.permutations(list);
		 List<String> output = new ArrayList<String>(res.size());
		 for (List<String> l : res)
			 output.add(StringBuilder.combine(l, " ").toString());
		 return output;
	}
	
	/**
	 * Generates all sliding windows of all sizes for given string, eg. <br>
	 * Input: Red Blood Cells <br>
	 * Output: <br>
	 * Red Blood Cells  <br>
	 * Red Blood  <br>
	 * Blood Cells  <br>
	 * Red  <br> 
	 * Blood  <br>
	 * Cells  <br>
	 * @param s
	 * @param tokenSeparator
	 * @return List of all windows by decreasing window size.
	 */
	public static List<String> slidingWindowDecreasingSize(String s, String tokenSeparator) {
		List<String> windows = new ArrayList<String>();
		String[] token = s.split(tokenSeparator);
		for (int win = token.length; win > 0; win--) {
			for (int offset = 0; offset <= token.length-win; offset++) {
				String[] window = Arrays.copyOfRange(token, offset, offset+win);
				String str = StringBuilder.combine(window, tokenSeparator).toString();
				windows.add(str);
			}
		}
		return windows;
	}
	
	public static void main(String[] args) {
		
		String s2 = "Red Blood Cells";
		
		System.out.println("Input '" + s2 +"'");
		for(String s : TokenManipulator.getAllTokenPermutations(s2, "\\W"))
			System.out.println(s);
		
		System.out.println("Input '" + s2 +"'");
		for(String s : TokenManipulator.slidingWindowDecreasingSize(s2, " "))
			System.out.println(s);
	}

}
