package de.dwslab.dwslib.virtuoso;

import junit.framework.Assert;

import org.junit.Test;

public class VirtusoBasicFunctionsTest {

	@Test
	public void lodUriShortenerTest() {
		
		LodURI shortener = LodURI.getInstance();
		
		String uri1Full = "http://dbpedia.org/class/yago/Operas";
		String uri1Short = "yago:Operas";
		
		String uri2Full = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
		String uri2Short = "rdf:type";
		
		Assert.assertEquals(uri1Short, shortener.toPrefixedUri(uri1Full));
		
		Assert.assertEquals(uri2Full, shortener.toFullUri(uri2Short));
		
		
	}

}
