package de.uni_mannheim.informatik.dws.dwslib.virtuoso;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Disk-based cache. Does not store empty sparql results.
 * Always call @link shutdown() after using the cache!
 * Created by mschuhma on 2/21/14.
 */
public class CachedQuery {

    private final static Logger log = LoggerFactory.getLogger(Query.class);

    private String server;
    private String user;
    private String password;
    private boolean shorten;

    private Query queryObj;
    private ConcurrentNavigableMap<String, SPARQLQueryResultSet> cache;
    private DB db;

    /**
     * Initialize the connection to the given Virtuoso instance using the provided authentication
     * data. IRI shortening is turned off by default. UTF-8 is default.
     * Remember to call the shutdown() method at the end.
     *
     * @param server   server address to connect to
     * @param user     username to connect to server
     * @param password password to connect to server
     * @throws java.sql.SQLException on an error initializing the connection to the server
     */
    public CachedQuery(String server, String user, String password)
            throws SQLException {
        this.server = server;
        this.user = user;
        this.password = password;
        this.shorten = false;
        createCache();
    }

    /**
     * Initialize the connection to the given Virtuoso instance using the provided authentication
     * data and activate IRI shortening if <code>shorten</code> is set to true. The charset
     * for the created JDBC connection is UTF-8.
     * Remember to call the shutdown() method at the end.
     *
     * @param server   server address to connect to
     * @param user     username to connect to server
     * @param password password to connect to server
     * @param shorten  true if IRIs in result should be shortened, otherwise false
     * @throws java.sql.SQLException on an error initializing the connection to the server
     */
    public CachedQuery(String server, String user, String password, boolean shorten)
            throws SQLException {
        this.server = server;
        this.user = user;
        this.password = password;
        this.shorten = shorten;
        createCache();
    }

    /**
     * Shutdown the underlying Ehcache. Always call this method at the end.
     */
    public void shutdown() {
        db.close();
    }

    /**
     *
     * @param query Sparql query
     * @return The sparql result from cache or from virtuoso server.
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public synchronized SPARQLQueryResultSet sparqlQuery(String query) throws IOException, SQLException {

        SPARQLQueryResultSet cacheValue;
        cacheValue = cache.get(query);
        if (cacheValue != null) {
            log.debug("Found cached entry");
            return cacheValue;
        }
        else {
            log.debug("Not found cached entry");
            try {
                if (queryObj == null) {
                    queryObj = new Query(server, user, password, shorten);
                }
                SPARQLQueryResultSet res = queryObj.sparqlQuery(query);
                cache.put(query, res);
                db.commit();
                return res;
            }
            catch (Exception e) {
                log.error("Sparql query failed: Could neither find cache nor connect to server.");
                System.err.println("Sparql query failed: Could neither find cache nor connect to server.");
                return null;
            }

        }
    }

    private void createCache() {
        File dbFile = new File("SparqlCache.mapdb");
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            }
            catch (IOException e) {
                log.error("Could not created sparql cache file on disk");
                e.printStackTrace();
            }
        }
        db = DBMaker.newFileDB(dbFile).closeOnJvmShutdown().make();

        cache = db.getTreeMap("sparqlCache");
    }

}
