package de.dwslab.dwslib.wiki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.bliki.api.Page;
import info.bliki.api.User;
import de.tudarmstadt.ukp.wikipedia.parser.Link;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

/**
 * Extract the links from a bunch of Wikipedia pages in order of appearance, grouped by paragraph
 * 
 * @author Simone Paolo Ponzetto
 * @version 0.1
 */
public class WikiLinksExtractor {

	private static final String DEFAULT_EN_WIKI = "http://en.wikipedia.org/w/api.php";

	private static WikiLinksExtractor instance;

	private final User user;

	private final MediaWikiParser parser;

	private WikiLinksExtractor() {
		this.user = new User("", "", DEFAULT_EN_WIKI);
		user.login();
		this.parser = new MediaWikiParserFactory().createParser();
	}
	
	private WikiLinksExtractor(String wikiApiUrl) {
		this.user = new User("", "", DEFAULT_EN_WIKI);
		user.login();
		this.parser = new MediaWikiParserFactory().createParser();
	}

	public static synchronized WikiLinksExtractor getInstance(String wikiApiUrl) {
		if (instance == null)
			instance = new WikiLinksExtractor(wikiApiUrl);
		return instance;
	}
	
	/**
	 * Default getInstance() method uses http://en.wikipedia.org/w/api.php
	 */
	public static synchronized WikiLinksExtractor getInstance() {
		if (instance == null)
			instance = new WikiLinksExtractor();
		return instance;
	}

	/**
	 * Extract the links from a bunch of pages
	 * 
	 * @param pageTitles
	 *            Strings of Wikipedia titles, e.g. Haile Gebrselassie
	 * @return Map of pages of list of paragraphs of list of links (Map<String,
	 *         List<List<String>>> links)
	 */
	public Map<String, List<List<String>>> getLinks(String... pageTitles) {

		// from titles to their links
		Map<String, List<List<String>>> links = new HashMap<String, List<List<String>>>();

		List<Page> listOfPages = user.queryContent(pageTitles);
		for (Page page : listOfPages) {
			// for each page, a list of list of links (ie section separated)
			List<List<String>> pageLinks = new ArrayList<List<String>>();

			String rawText = page.getCurrentContent();
			ParsedPage pp = parser.parse(rawText);

			// get the internal links of each section
			for (Section section : pp.getSections()) {
				List<String> sectionLinks = new ArrayList<String>();
				for (Link link : section.getLinks(Link.type.INTERNAL)) {
					sectionLinks.add(link.getTarget());
				}
				pageLinks.add(sectionLinks);
			}
			links.put(page.getTitle(), pageLinks);
		}

		return links;
	}

	public static void main(String[] args) {

		String[] test = {
				"Abebe Bikila", 
				"Haile Gebrselassie", 
				"Emil Zátopek",
				"Ezekiel Kemboi" };

		WikiLinksExtractor extractor = WikiLinksExtractor.getInstance();

		Map<String, List<List<String>>> links = extractor.getLinks(test);

		// here is how to get links from the first section
		for (String page : links.keySet()) {

			System.out.println("Title: " + page);

			for (List<String> sectionLinks : links.get(page)) {

				for (String sectionLink : sectionLinks) {
					System.out.println("\t" + sectionLink);
				}
				// if you want the first section only
				// break;
			}
		}
	}
}
