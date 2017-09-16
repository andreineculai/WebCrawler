package robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	HashMap<String, DomainFrontier> urlFrontier;
	HashMap<String, ArrayList<String>> robotstxt;

	public Crawler(ArrayList<String> seedList) {
		urlFrontier = new HashMap<>();
		for (String seed : seedList) {
			String seedHost = URLParsingUtils.getHost(seed);
			if (!urlFrontier.containsKey(seedHost)) {
				urlFrontier.put(seedHost, new DomainFrontier());
			}
			urlFrontier.get(seedHost).addLink(seed);
		}
		robotstxt = new HashMap<>();
	}

	public void crawl() throws Exception {
		String currentURL;
		while (!urlFrontier.isEmpty()) {
			HashMap<String, DomainFrontier> newDomainsCurrentIterations = new HashMap<>();
			for (Iterator<HashMap.Entry<String, DomainFrontier>> it = urlFrontier.entrySet().iterator(); it
					.hasNext();) {
				HashMap.Entry<String, DomainFrontier> domainFrontier = it.next();
				if (domainFrontier.getValue().canMakeCall()) {
					currentURL = domainFrontier.getValue().getNextLink();
					System.out.println(currentURL + " - Time : " + Calendar.getInstance().getTime().toString());
					String path = URLParsingUtils.createPathToStorePage(currentURL);
					File file = new File(path);
					if (!file.exists()) {
						if (checkRobots(currentURL)) {
							Document currentPage = fetchPage(currentURL);
							if (currentPage != null) {
								MetaTagInfo metaTagInfo = getMetaInfo(currentPage);
								if (metaTagInfo.canIndex()) {
									writeContentsToFile(currentPage, file);
								}
								if (metaTagInfo.canFollow()) {
									extractLinks(currentPage, domainFrontier.getValue(), newDomainsCurrentIterations);
								}
							}
						}
					}
					if (domainFrontier.getValue().isEmpty()) {
						it.remove();
					}
				}
			}
			urlFrontier.putAll(newDomainsCurrentIterations);
		}
	}

	private boolean checkRobots(String url) throws Exception {
		String hostURL = URLParsingUtils.getHost(url);
		String path = URLParsingUtils.getLocalAddress(url);
		boolean robotsRulesBuilt = false;
		if (!robotstxt.containsKey(hostURL)) {
			HTTPClient httpClient = new HTTPClient();
			String response = httpClient.GET(hostURL + "/robots.txt");
			if (response != null) {
				buildRules(hostURL, response);
				robotsRulesBuilt = true;
			}
		}
		if (robotsRulesBuilt) {
			for (String disallowedPath : robotstxt.get(hostURL)) {
				if (path.startsWith(disallowedPath)) {
					return false;
				}
			}
		}
		return true;
	}

	private void buildRules(String hostURL, String response) throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader(response));
		// System.out.println(response);
		String line;
		ArrayList<String> disallows = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("User-agent: *")) {
				break;
			}
		}
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("User-agent")) {
				break;
			}
			if (line.startsWith("Disallow")) {
				int index = line.indexOf('\'');
				if (line.length() > index + 1) {
					String path = line.substring(index + 1, line.length());
					if (!path.trim().isEmpty()) {
						disallows.add(path);
					}
				}
			}
		}
		robotstxt.put(hostURL, disallows);
	}

	public Document fetchPage(String url) throws Exception {
		String baseURI = URLParsingUtils.getParentLink(url);
		HTTPClient httpClient = new HTTPClient();
		String response = httpClient.GET(url);
		if (response != null) {
			return Jsoup.parse(response, baseURI);
		}
		return null;
	}

	public void writeContentsToFile(Document doc, File file) throws Exception {
		String body = doc.body().text();
		body = doc.text();
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(body);
		writer.flush();
		writer.close();
	}

	public void extractLinks(Document doc, DomainFrontier domainFrontier,
			HashMap<String, DomainFrontier> newDomainsCurrentIterations) {
		Elements links = doc.select("a[href]");
		for (Element link : links) {
			String newUrl = link.attr("abs:href");
			if (newUrl.endsWith("/") || newUrl.endsWith("html")) {
				// System.out.println(newUrl);
				if (newUrl.startsWith("/")) {
					newUrl = doc.baseUri() + newUrl.substring(1);
					domainFrontier.addLink(newUrl);
				} else if (newUrl.startsWith("http")) {
					String domain = URLParsingUtils.getHost(newUrl);
					if (!urlFrontier.containsKey(domain)) {
						newDomainsCurrentIterations.put(domain, new DomainFrontier());
						newDomainsCurrentIterations.get(domain).addLink(newUrl);
					} else {
						urlFrontier.get(domain).addLink(newUrl);
					}

				}
			}
		}
	}

	public MetaTagInfo getMetaInfo(Document doc) {
		Elements metalinks = doc.select("meta[name=robots]");
		if (!metalinks.isEmpty()) {
			Element link = metalinks.first();
			String content = link.attr("content");
			return new MetaTagInfo(content);
		}
		return new MetaTagInfo();
	}
}
