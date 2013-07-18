package de.uni_mannheim.informatik.dws.dwslib.cli;

import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.dwslib.virtuoso.Query;
import de.uni_mannheim.informatik.dws.dwslib.virtuoso.SPARQLQueryResultSet;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Provides a command-line interface to the Virtuoso RDF store using the JDBC driver instead of the default SPARQL
 * endpoint. This allows the retrieval of more than 1024^2 rows of results.
 *
 * @author Daniel Fleischhacker
 */
public class QueryCLI {
    public static void main(String[] args) throws IOException, SQLException {
        if (args.length != 5 && args.length != 6) {
            System.out.println("Usage: <server> <user> <password> <outfile> <query> [shorten]");
            System.out.println("Example: example.com dba password bla.tsv \"SELECT * WHERE {?a ?b ?c} LIMIT" +
                    " 1500000\"");
            System.exit(1);
        }

        String server = args[0];
        String user = args[1];
        String password = args[2];
        String outfile = args[3];
        String query = args[4];
        boolean shorten = false;
        if (args.length == 6 && args[5].equals("shorten")) {
            shorten = true;
        }
        long startTime = System.currentTimeMillis();

        sparqlQuery(server, user, password, outfile, query, shorten);

        System.out.printf("Total runtime: %d secs\n", (System.currentTimeMillis() - startTime) / 1000);
    }



    public static void sparqlQuery(String server, String user, String password, String outfile, String query,
                                   boolean shorten) throws SQLException, IOException {
        Query q = new Query(server, user, password, shorten);
        SPARQLQueryResultSet res = q.sparqlQuery(query);
        CSVWriter writer = new CSVWriter(new FileWriter(outfile), '\t');

        String[] columnNames = res.getColumnNames();
        writer.writeNext(columnNames);

        for (HashMap<String, String> cur : res) {
            String[] row = new String[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                row[i] = cur.get(columnNames[i]);
            }
            writer.writeNext(row);
        }

        writer.close();
    }
}
