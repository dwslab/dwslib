package de.uni_mannheim.informatik.dws.dwslib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LodURI {
	
	final static Logger log = LoggerFactory.getLogger(LodURI.class);
	
	private static LodURI instance = null;
	
	public static File f = null;
	public static HashMap<String, String> prefixMap = null;
	public static HashMap<String, String> uriMap = null;
	
	public String toPrefixedUri(String fullURI) {
		int i = fullURI.lastIndexOf("/");
		int j = Math.max(i, fullURI.lastIndexOf("#"));
		int k = Math.max(j, fullURI.lastIndexOf(":"));
		if (k <= 4)
			return fullURI;
		return prefixMap.get(fullURI.substring(0, k+1))+fullURI.substring(k+1);
	}
	
	public String toFullUri(String prefixedURI) {
		int i = prefixedURI.indexOf(":");
		if (i <= 1)
			return prefixedURI;
		return uriMap.get(prefixedURI.substring(0, i))+prefixedURI.substring(i+1);	
	}
	
	private LodURI(String uriList) {
		f = new File(uriList);
		prefixMap = new HashMap<String, String>();
		uriMap = new HashMap<String, String>();
		for (ArrayList<String> line : MyFileReader.readXSVFile(f, "\t", false)) {
			prefixMap.put(line.get(1).trim(), line.get(0).trim() + ":");
			uriMap.put(line.get(0).trim(), line.get(1).trim());
		}
		log.debug("PrefixCC mappings loaded from file {}", f.getName());
	}
	
	
	public static LodURI getInstance(String uriList) {
		if (instance == null)
			instance = new LodURI(uriList);
		return instance;
	}
	
	public static LodURI getInstance() {
		if (instance == null)
			instance = new LodURI("src/main/resources/prefixcc-20130404.tab");
		return instance;
	}

	public static void main(String[] args) {
		LodURI uri = LodURI.getInstance();
		
		System.out.println("toFullUri(\"rdf:type\") = "+uri.toFullUri("rdf:type"));
		
		System.out.println(
				"toPrefixedUri(\"http://dbpedia.org/class/yago/Operas\") = " +
				uri.toPrefixedUri("http://dbpedia.org/class/yago/Operas"));
	}

}
