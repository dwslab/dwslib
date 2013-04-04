package de.uni_mannheim.informatik.dws.dwslib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MyFileReader {
	
	final static Logger log = LoggerFactory.getLogger(MyFileReader.class);
	
	public static ArrayList<ArrayList<String>> readXSVFile(File inputFile, String valueSeperator, boolean hasHeader) {
		Scanner scan = null;
		try {
			scan = new Scanner(inputFile, "UTF-8");
			if (hasHeader) {scan.nextLine();}
		} catch (FileNotFoundException e) {
			log.warn(e.toString());
		}
		ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();
		while (scan.hasNextLine()) {
			ArrayList<String> tokens = new ArrayList<String>();
			String line = scan.nextLine();
			StringTokenizer st = new StringTokenizer(line,valueSeperator);
			while (st.hasMoreTokens()) {
				tokens.add(st.nextToken());
				}
			lines.add(tokens);
			}
		scan.close();
		return lines;
	}
	
	public static ArrayList<String> readLinesFile(File inputFile) {
		
		BufferedReader br = null;
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), Charset.forName("UTF-8")));
			while (br.ready()){
				lines.add(br.readLine());
			}
			br.close();
		} catch (IOException e) {
			log.error(e.toString());
		}
		return lines;
	}

}
