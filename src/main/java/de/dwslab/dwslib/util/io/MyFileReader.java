package de.dwslab.dwslib.util.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


public class MyFileReader {

    final static Logger log = LoggerFactory.getLogger(MyFileReader.class);

    /**
     * Read from Resource: MyFileReader.class.getResourceAsStream("filename")
     * @param inputStream
     * @param valueSeperator
     * @param hasHeader
     * @return
     */
    public static ArrayList<ArrayList<String>> readXSVFile(
            InputStream inputStream, String valueSeperator, boolean hasHeader) {
        Scanner scan;
        scan = new Scanner(inputStream, "UTF-8");

        if (hasHeader) {
            scan.nextLine();
        }
        ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();

        while (scan.hasNextLine()) {

            ArrayList<String> tokens = new ArrayList<String>();

            String line = scan.nextLine();
            StringTokenizer st = new StringTokenizer(line, valueSeperator);

            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken());
            }
            lines.add(tokens);
        }

        scan.close();

        return lines;
    }

    public static ArrayList<ArrayList<String>> readXSVFile(
            File inputFile, String valueSeperator, boolean hasHeader) {

        try {
            return readXSVFile(
                    new FileInputStream(inputFile),
                    valueSeperator, hasHeader);
        }
        catch (FileNotFoundException e) {
            log.error(e.toString());
            return null;
        }
    }

    public static ArrayList<String> readLinesFile(InputStream inputStream) {

        BufferedReader br = null;
        ArrayList<String> lines = new ArrayList<String>();

        try {
            br = new BufferedReader(new InputStreamReader(
                    inputStream, Charset.forName("UTF-8")));

            while (br.ready()) {
                lines.add(br.readLine());
            }
            br.close();

        }
        catch (IOException e) {
            log.error(e.toString());
        }
        return lines;
    }

    public static ArrayList<String> readLinesFile(File inputFile) {

        try {
            return readLinesFile(
                    new FileInputStream(inputFile));
        }
        catch (FileNotFoundException e) {
            log.error(e.toString());
            return null;
        }

    }
}
