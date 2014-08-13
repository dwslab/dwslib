package de.dwslab.dwslib.util.uri;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.net.InternetDomainName;

public class DomainUtil {


	/**
	 * URL which is returned if domain could not be extracted
	 */
	public static String INVALID_URL = null;

	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger("DomainUtils.class");

	/**
	 * Undoes all compressions done by {@link #compress(String)}
	 * 
	 * @param url
	 *            the compressed url
	 * @return the original url
	 */
	public static String uncompress(String url) {
		if (url.matches(".*#\\d\\d\\d#.*")) {
			// get abbreviation
			String abb = url.replaceFirst(".*(#\\d\\d\\d#).*", "$1");
			if (invReductionMap.containsKey(abb)) {
				url = url.replace(abb, invReductionMap.get(abb));
			}
		}
		if (url.startsWith("s:w:")) {
			url = url.replaceFirst("s:w:", "https://www");
		} else if (url.startsWith("s:")) {
			url = url.replaceFirst("s:", "https://");
		} else if (url.startsWith("w:")) {
			url = url.replaceFirst("w:", "http://www");
		} else {
			url = "http://" + url;
		}

		return url;
	}

	public static String compress(String url) {
		String pld = DomainUtil.getPayLevelDomainFromWholeURL(url);
		// we first remove the http(s)://
		if (url.startsWith("http://"))
			url = url.replaceFirst("http://", "");
		if (url.startsWith("https://"))
			url = url.replace("https://", "s:");
		if (url.startsWith("www") || url.startsWith("s:www"))
			url = url.replaceFirst("www", "w:");
		if (reductionMap.containsKey(pld)) {
			url = url.replace(pld, reductionMap.get(pld));
		}
		return url;
	}

	public static void main(String[] args) {
		System.out
				.println(getFirstSubDomainFromWholeUrl("http://bbc.co.uk.-.x__________x.x__________x.yourankstuff.com/"));
		System.out
				.println(getFirstSubDomainFromWholeUrl("http://robert.freenet.de"));
		System.out
				.println(getFirstSubDomainFromWholeUrl("http://dws.informatik.uni-mannheim.de/index.html"));
		System.out
				.println(getFirstSubDomainFromWholeUrl("http://www.robme.de"));
		System.out.println(getSubDomainFromWholeUrl("http://freenet.de"));
		System.out
				.println(getSubDomainFromWholeUrl("http://robert.freenet.de"));
		System.out
				.println(getSubDomainFromWholeUrl("http://dws.informatik.uni-mannheim.de/index.html"));
		System.out.println(getSubDomainFromWholeUrl("http://www.robme.de"));

		System.out.println(String.format("#%03d#", 2));
		String s = null;
		System.out.println(s = compress("http://vimeo.com/superduper.html"));
		System.out.println(uncompress(s));
	}

	public static String getSubDomainFromWholeUrl(String url) {
		String domain = getDomain(url);
		try {
			InternetDomainName fullDomainName = InternetDomainName.from(domain);
			if (fullDomainName.toString().startsWith("www.")) {
				return fullDomainName.toString().replaceFirst("www.", "");
			} else {
				return fullDomainName.toString();
			}
		} catch (Exception e) {
			// log.log(Level.WARNING, "Could not get subdomain from " + domain +
			// ".");
		}
		return INVALID_URL;
	}

	public static String getFirstSubDomainFromWholeUrl(String url) {
		String domain = getDomain(url);
		try {
			InternetDomainName fullDomainName = InternetDomainName.from(domain);
			String pld = fullDomainName.topPrivateDomain().toString();
			String firstSubDomain = fullDomainName.toString().replace(pld, "");
			if (firstSubDomain.startsWith("www.")) {
				firstSubDomain = firstSubDomain.replaceFirst("www.", "");
			}
			if (firstSubDomain.endsWith(".")) {
				firstSubDomain = firstSubDomain.substring(0,
						firstSubDomain.length() - 1);
				if (firstSubDomain.contains(".")) {
					firstSubDomain = firstSubDomain.substring(
							firstSubDomain.lastIndexOf(".") + 1,
							firstSubDomain.length());
				}
				return firstSubDomain + "." + pld;
			} else {
				return pld;
			}

		} catch (Exception e) {
			// log.log(Level.WARNING, "Could not get 1subdomain from " + domain
			// + ".");
		}
		return INVALID_URL;
	}

	public static String getPayLevelDomain(String domain) {
		try {
			InternetDomainName fullDomainName = InternetDomainName.from(domain);
			return fullDomainName.topPrivateDomain().toString();
		} catch (Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
		}
		return domain;
	}

	public static String getPayLevelDomainFromWholeURL(String url) {
		String domain = getDomain(url);
		try {
			InternetDomainName fullDomainName = InternetDomainName.from(domain);
			return fullDomainName.topPrivateDomain().toString();
		} catch (Exception e) {
			// log.log(Level.WARNING, "Could not get pld from " + domain + ".");
		}
		return INVALID_URL;
	}

	private static final Pattern DOMAIN_PATTERN = Pattern
			.compile("http(s)?://(([a-zA-Z0-9-_]+(\\.)?)+)");

	public static String getDomain(String uri) {
		try {
			Matcher m = DOMAIN_PATTERN.matcher(uri);
			if (m.find()) {
				return m.group(2);
			}
		} catch (Exception e) {
			log.log(Level.WARNING, uri, e);
		}
		return uri;
	}

	public static String getTopLevelDomainFromWholeURL(String url) {
		String domain = getDomain(url);
		try {
			InternetDomainName fullDomainName = InternetDomainName.from(domain);
			return fullDomainName.publicSuffix().toString();
		} catch (Exception e) {
			log.log(Level.WARNING, "Could not get tld from " + domain + ".");
		}
		return INVALID_URL;
	}

	public static String getTopLevelDomain(String domain) {
		try {
			InternetDomainName fullDomainName = InternetDomainName.from(domain);
			return fullDomainName.publicSuffix().toString();
		} catch (Exception e) {
			log.log(Level.WARNING, domain, e);
		}
		return domain;
	}

	// top 1000 list of urls including pld of ~9% of all vertices
	public static HashMap<String, String> reductionMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						this.getClass().getResourceAsStream(
								"/top1000domains-2012.list")));
				int cnt = 0;
				while (br.ready()) {
					put(br.readLine(), String.format("#%03d#", cnt));
					cnt++;
				}
			} catch (Exception ex) {

			}
		};
	};

	/**
	 * the inverse of the reductionMap
	 */

	public static HashMap<String, String> invReductionMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						this.getClass().getResourceAsStream(
								"/top1000domains-2012.list")));
				int cnt = 0;
				while (br.ready()) {
					put(String.format("#%03d#", cnt), br.readLine());
					cnt++;
				}
			} catch (Exception ex) {

			}
		};
	};

}
