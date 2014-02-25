package de.uni_mannheim.informatik.dws.dwslib.virtuoso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

/**
 * Implements querying functionality for the Virtuoso RDF store. SPARQL queries are executed using JDBC drivers
 * which allows queries to return more than 1024^2 results in contrast to the HTTP-based query execution.
 */
public class Query {
    private final static Logger log = LoggerFactory.getLogger(Query.class);

    private URIShortener shortener;
    private Connection conn;

    /**
     * Initialize the connection to the given Virtuoso instance using the provided authentication
     * data. IRI shortening is turned off by default.
     *
     * @param server   server address to connect to
     * @param user     username to connect to server
     * @param password password to connect to server
     * @throws java.sql.SQLException on an error initializing the connection to the server
     */
    public Query(String server, String user, String password) throws SQLException {
        this(server, user, password, false);
    }

    /**
     * Initialize the connection to the given Virtuoso instance using the provided authentication
     * data and activate IRI shortening if <code>shorten</code> is set to true. The charset
     * for the created JDBC connection is UTF-8. This default can be influenced by using the alternative
     * constructor {@link #Query(String, String, String, boolean, String)}.
     *
     * @param server   server address to connect to
     * @param user     username to connect to server
     * @param password password to connect to server
     * @param shorten  true if IRIs in result should be shortened, otherwise false
     * @throws java.sql.SQLException on an error initializing the connection to the server
     */
    public Query(String server, String user, String password, boolean shorten) throws SQLException {
        this(server, user, password, shorten, "UTF-8");
    }

    /**
     * Initialize the connection to the given Virtuoso instance using the provided authentication
     * data and activate IRI shortening if <code>shorten</code> is set to true.
     *
     *
     * @param server   server address to connect to
     * @param user     username to connect to server
     * @param password password to connect to server
     * @param shorten  true if IRIs in result should be shortened, otherwise false
     * @param charset  charset to set JDBC connection to
     * @throws java.sql.SQLException on an error initializing the connection to the server
     */
    public Query(String server, String user, String password, boolean shorten, String charset) throws SQLException {
        this.shortener = shorten ? new URIShortener.LODShortener() : new URIShortener.DummyShortener();
        DriverManager.registerDriver(new virtuoso.jdbc4.Driver());
        this.conn = DriverManager
                .getConnection(
                        String.format("jdbc:virtuoso://%s/UID=%s/PWD=%s/charset=%s/", server, user, password, charset));
    }

    /**
     * Initialize the connection directly using the given JDBC URI to connect to the database server and activate
     * IRI shortening if <code>shorten</code> is set to true.
     *
     * Note, that the provided URI has to contain everything
     * required to connect to the database, e.g., username, password and charset, as it is not modified for this
     * call.
     *
     * @param jdbcUri full JDBC URI for connecting to the database
     * @param shorten true if IRIs in result should be shortened, otherwise false
     */
    public Query(String jdbcUri, boolean shorten) throws SQLException {
        this.shortener = shorten ? new URIShortener.LODShortener() : new URIShortener.DummyShortener();
        DriverManager.registerDriver(new virtuoso.jdbc4.Driver());
        this.conn = DriverManager.getConnection(jdbcUri);
    }

    /**
     * Determines whether this Query instance has IRI shortening enabled.
     *
     * @return true if IRI shortening is enabled, otherwise false.
     */
    public boolean isShortening() {
        return !(shortener instanceof URIShortener.DummyShortener);
    }

    /**
     * Controls the shortening if IRIs.
     *
     * @param shorten if true IRI shortening is activated, otherwise no IRIs are shortened
     */
    public void setShortening(boolean shorten) {
        if (shorten) {
            this.shortener = new URIShortener.LODShortener();
        }
        else {
            this.shortener = new URIShortener.DummyShortener();
        }
    }

    /**
     * Sends the given SPARQL query to the server and returns the result as a raw {@link ResultSet}. In particular,
     * no IRI shortening is done not even when shortening for this query instance is enabled. The result set
     * must be closed when it is no longer in use.
     *
     * @param query SPARQL query to execute
     * @return ResultSet for the given query
     * @throws SQLException
     * @throws IOException
     */
    public ResultSet sparqlQueryRaw(String query) throws SQLException, IOException {
        return queryRaw("sparql " + query);
    }

    /**
     * Sends the given SPARQL query to the server and returns the result as a raw {@link ResultSet}. In particular,
     * no IRI shortening is done not even when shortening for this query instance is enabled. The result set
     * must be closed when it is no longer in use.
     *
     * @param query SPARQL query to execute
     * @return ResultSet for the given query
     * @throws SQLException
     * @throws IOException
     */
    public ResultSet queryRaw(String query)
            throws SQLException, IOException {

        Statement stmt = conn.createStatement();
        log.info("Query: '{}'\n", query);
        return stmt.executeQuery(query);
    }

    /**
     * Sends the given SPARQL query to the server and returns the result as a
     * {@link de.uni_mannheim.informatik.dws.dwslib.virtuoso.SPARQLQueryResultSet}.
     *
     * @param query SPARQL query to execute
     * @return SPARQLQueryResultSet for the given query
     * @throws SQLException
     * @throws IOException
     */
    public SPARQLQueryResultSet sparqlQuery(String query) throws IOException, SQLException {
        ResultSet res = sparqlQueryRaw(query);

        String[] colNames = new String[res.getMetaData().getColumnCount()];
        for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
            colNames[i - 1] = res.getMetaData().getColumnName(i);
        }

        SPARQLQueryResultSet ret = new SPARQLQueryResultSet(colNames);

        while (res.next()) {
            HashMap<String, String> row = new HashMap<String, String>();
            for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
                row.put(colNames[i - 1], shortener.shorten(res.getString(i)));
            }
            ret.add(row);
        }

        res.getStatement().close();
        res.close();

        return ret;
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
