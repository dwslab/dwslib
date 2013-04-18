package de.uni_mannheim.informatik.dws.dwslib.virtuoso;

import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.dwslib.LodURI;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * Provides a command-line interface to the Virtuoso RDF store using the JDBC driver instead of the default SPARQL
 * endpoint. This allows the retrieval of more than 1024^2 rows of results.
 */
public class Query {
    public static void main(String[] args) throws SQLException, IOException {
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
        String query = "sparql " + args[4];
        URIShortener shortener = new URIShortener.DummyShortener();
        if (args.length == 6 && args[5].equals("shorten")) {
            shortener = new URIShortener.LODShortener();
        }
        long startTime = System.currentTimeMillis();

        sparqlQuery(server, user, password, outfile, query, shortener);

        System.out.printf("Total runtime: %d secs\n", (System.currentTimeMillis() - startTime) / 1000);
    }

    private static void sparqlQuery(String server, String user, String password, String outfile, String query,
                                    URIShortener shortener)
            throws SQLException, IOException {

        DriverManager.registerDriver(new virtuoso.jdbc4.Driver());
        Connection conn = DriverManager
                .getConnection(String.format("jdbc:virtuoso://%s/UID=%s/PWD=%s/", server, user, password));
        Statement stmt = conn.createStatement();
        System.out.printf("Query: '%s'\n", query);
        ResultSet res = stmt.executeQuery(query);

        CSVWriter writer = new CSVWriter(new FileWriter(outfile), '\t');

        String[] colNames = new String[res.getMetaData().getColumnCount()];
        for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
            colNames[i - 1] = res.getMetaData().getColumnName(i);
        }
        writer.writeNext(colNames);

        while (res.next()) {
            String[] row = new String[res.getMetaData().getColumnCount()];
            for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
                row[i - 1] = shortener.shorten(res.getString(i));
            }
            writer.writeNext(row);
        }
        res.close();
        stmt.close();
        conn.close();

        writer.close();
    }

    /**
    *
    */
    abstract static class URIShortener {
        public abstract String shorten(String uri);

        /**
         *
         */
        public static class LODShortener extends URIShortener {
            private LodURI lodUri = LodURI.getInstance();
            @Override
            public String shorten(String uri) {
                return lodUri.toPrefixedUri(uri);  //To change body of implemented methods use File | Settings | File Templates.
            }
        }

        /**
         *
         */
        public static class DummyShortener extends URIShortener {
            @Override
            public String shorten(String uri) {
                return uri;
            }
        }
    }
}
