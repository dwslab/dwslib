package de.uni_mannheim.informatik.dws.dwslib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LodURI {
	
	final static Logger log = LoggerFactory.getLogger(LodURI.class);
	
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
	
	public LodURI(String uriList) {
		f = new File(uriList);
		if (prefixMap == null && uriMap == null) {
			prefixMap = new HashMap<String, String>();
			uriMap = new HashMap<String, String>();
			for (ArrayList<String> line : MyFileReader.readXSVFile(f, "\t", false)) {
				prefixMap.put(line.get(1).trim(), line.get(0).trim() + ":");
				uriMap.put(line.get(0).trim(), line.get(1).trim());
			}
		}
		log.debug("PrefixCC Mappings loaded from file {}", f.getName());
	}
	
	public LodURI() {
		this("src/main/java/de/uni_mannheim/informatik/dws/dwslib/prefixcc-20130404.tab");
	}


	public static void main(String[] args) {
		LodURI uri = new LodURI();
		System.out.println(uri.toFullUri("rdf:type"));
		System.out.println(uri.toPrefixedUri("http://dbpedia.org/class/yago/Operas"));
	}

}
