package de.uni_mannheim.informatik.dws.dwslib.virtuoso;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * Disk-based cache. Does not store empty sparql results.
 * Always call @link shutdown() after using the cache!
 * Created by mschuhma on 2/21/14.
 */
public class CachedQuery {

    private final static Logger log = LoggerFactory.getLogger(Query.class);

    private static Query queryObj;
    private static Ehcache cache;
    private static CacheManager cacheManager;

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
        queryObj = new Query(server, user, password);
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
        queryObj = new Query(server, user, password, shorten);
        createCache();
    }

    /**
     * Shutdown the underlying Ehcache. Always call this method at the end.
     */
    public void shutdown() {
        cache.flush();
        cacheManager.shutdown();
    }

    /**
     *
     * @param query Sparql query
     * @return The sparql result from cache or from virtuoso server.
     * @throws IOException
     * @throws SQLException
     */
    public SPARQLQueryResultSet sparqlQuery(String query) throws IOException, SQLException {

        Element element;
        if ((element = cache.get(query)) != null) {
            return (SPARQLQueryResultSet) element.getObjectValue();
        }
        else {
            SPARQLQueryResultSet res = queryObj.sparqlQuery(query);
            if (res.size() > 0)
                cache.put(new Element (res, res));
            return res;
        }
    }

    private void createCache() {

        Configuration cacheManagerConfig = new Configuration()
                .diskStore(new DiskStoreConfiguration()
                        .path("sparqlcache"));

        CacheConfiguration cacheConfig =
                new CacheConfiguration()
                .name("sparqlcache")
                .maxBytesLocalHeap(50, MemoryUnit.MEGABYTES)
                .persistence(new PersistenceConfiguration().
                        strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP));

        cacheManagerConfig.addCache(cacheConfig);

        cacheManager = new CacheManager(cacheManagerConfig);
        cache = cacheManager.getEhcache("sparqlcache");

        log.debug("Ehcache for sparql queries created");
    }

}
