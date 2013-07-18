package de.uni_mannheim.informatik.dws.dwslib.virtuoso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Provides access to the result of a SPARQL query executed using {@link Query}.
 */
public class SPARQLQueryResultSet extends ArrayList<HashMap<String, String>> {
    private String[] columnNames;

    public String[] getColumnNames() {
        return columnNames;
    }

    public SPARQLQueryResultSet(int initialCapacity, String[] columnNames) {
        super(initialCapacity);
        this.columnNames = columnNames;
    }

    public SPARQLQueryResultSet(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public SPARQLQueryResultSet(Collection<? extends HashMap<String, String>> c,
                                String[] columnNames) {
        super(c);
        this.columnNames = columnNames;
    }
}
