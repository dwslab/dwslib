package de.uni_mannheim.informatik.dws.dwslib.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Provides methods to split a GZIP file into several GZIP files without
 * decompressing the input file first. The split is done on a line base.
 *
 * @author Daniel Fleischhacker <daniel@informatik.uni-mannheim.de>
 */
public class SplitGZIPFile {
    @Option(name = "-l", usage = "Number of lines per file", required = true)
    private int numberOfLines;

    @Option(name = "-i", usage = "GZIPed input file", required = true)
    private File inputFile;

    @Option(name = "-p", usage = "Prefix for output files")
    private String prefix = "out_";

    @Option(name = "-x", usage = "File extension for output files")
    private String fileExtension = ".gz";

    @Option(name = "-o", usage = "Output directory for generated files", required = true)
    private File outputDir;

    /**
     * Splits the file defined by {@code inputFile} into files having the given number of lines. Returns the number of
     * files generated.
     *
     * @return number of files generated
     * @throws IOException
     */
    public int splitFiles() throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputFile))));

        String line;
        int curNumOfLines = 0;
        int fileCounter = 0;

        BufferedWriter writer = null;

        while ((line = reader.readLine()) != null) {
            if (writer == null) {
                writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(
                        outputDir.getAbsolutePath() + File.separator + prefix + fileCounter + fileExtension))));
                fileCounter++;
                curNumOfLines = 0;
            }

            writer.write(line);
            writer.newLine();
            curNumOfLines++;

            if (curNumOfLines == numberOfLines) {
                writer.close();
                writer = null;
            }
        }

        if (writer != null) {
            writer.close();
        }

        return fileCounter;
    }

    public static void main(String[] args) throws IOException {
        SplitGZIPFile splitter = new SplitGZIPFile();
        CmdLineParser parser = new CmdLineParser(splitter);

        try {
            parser.parseArgument(args);
        }
        catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java " + SplitGZIPFile.class.getCanonicalName() + " options...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            return;
        }

        int numOfFiles = splitter.splitFiles();

        System.out.println("Split into " + numOfFiles + " files");
    }


}
