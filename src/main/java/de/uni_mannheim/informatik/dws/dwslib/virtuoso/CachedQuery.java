package de.uni_mannheim.informatik.dws.dwslib.virtuoso;

import com.google.common.collect.ImmutableCollection;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.*;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 *
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
    private ConcurrentNavigableMap<String,SPARQLQueryResultSet> cache;
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
     * @throws IOException
     * @throws SQLException
     */
    public synchronized SPARQLQueryResultSet sparqlQuery(String query) throws IOException, SQLException {

        SPARQLQueryResultSet cacheValue;
        cacheValue = cache.get(query);
        if (cacheValue != null)
            return cacheValue;

        else {
            try {
            if (queryObj == null) {
                queryObj = new Query(server,user,password,shorten);
            }
            SPARQLQueryResultSet res = queryObj.sparqlQuery(query);
            if (res.size() > 0) {
                cache.put(query, res);
                db.commit();
            }
            return res;

            }
            catch (Exception e) {
                System.out.println("Sparql query failed: Could neither find cache nor connect to server.");
                return null;
            }

        }
    }

    private void createCache() {

        File dbFile = new File("SparqlCache.mapdb");
        if (!dbFile.exists())
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                //TOOD better exception handling
                e.printStackTrace();
            }
        db = DBMaker.newFileDB(dbFile).closeOnJvmShutdown().make();

        cache = db.getTreeMap("sparqlcache");

//        Configuration cacheManagerConfig = new Configuration();
//
//        CacheConfiguration cacheConfig =
//                new CacheConfiguration()
//                .name("sparqlcache")
//                .maxBytesLocalHeap(256, MemoryUnit.MEGABYTES)
//                .persistence(new PersistenceConfiguration().
//                        strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP));
//
//        DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
//        diskStoreConfiguration.setPath("sparqlCache");
//        cacheManagerConfig.addDiskStore(diskStoreConfiguration);
//
//        cacheManager = new CacheManager(cacheManagerConfig);
//        cache = cacheManager.getEhcache("sparqlcache");
//
//        log.debug("Ehcache for sparql queries created");
    }

    public static void main(String[] args) throws Exception {

        //Test methods

        CachedQuery main = new CachedQuery(
                "wifo5-32.informatik.uni-mannheim.de:1112", "dba", "test1234", false);

        String q = "select * where {<http://dbpedia.org/resource/Berlin> ?p ?o}";

        for (int i = 0; i <10; i++) {
            System.out.println("Results " + i);
            SPARQLQueryResultSet res = main.sparqlQuery(q);
            //das hier geht
            for (int j = 0; j < res.getColumnNames().length; j++)
                System.out.println(res.getColumnNames()[j]);
        }


        for (int i = 0; i <10; i++) {
            System.out.println("Results " + i);
            SPARQLQueryResultSet res = main.sparqlQuery(q);
            //das hier aber nicht. Liegt das daran, dann die ArrayList of HashMaps nicht serilaziable ist?
            for (HashMap<String,String> m : res) {
                for(Map.Entry<String,String> e : m.entrySet()) {
                    System.out.println(e.getKey() + "\t"+ e.getValue());
                }
            }
        }

        System.out.println("Done");

        main.shutdown();

    }

}
