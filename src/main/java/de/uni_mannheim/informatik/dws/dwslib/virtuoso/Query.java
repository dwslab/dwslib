package de.uni_mannheim.informatik.dws.dwslib.virtuoso;

import au.com.bytecode.opencsv.CSVWriter;

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
    	query(server, user, password, outfile, "sparql " + query, shorten);
    }

    public static void query(String server, String user, String password, String outfile, String query,
                                    boolean shorten)
            throws SQLException, IOException {
    	
    	URIShortener shortener = 
    			shorten ? new URIShortener.LODShortener() : new URIShortener.DummyShortener();

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


    public abstract static class URIShortener {
        public abstract String shorten(String uri);


        public static class LODShortener extends URIShortener {
        	public LodURI lodUri = LodURI.getInstance();
            @Override
            public String shorten(String uri) {
                return lodUri.toPrefixedUri(uri); 
            }
        }


        public static class DummyShortener extends URIShortener {
            @Override
            public String shorten(String uri) {
                return uri;
            }
        }
    }
}
