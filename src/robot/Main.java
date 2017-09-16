package robot;

import java.util.ArrayList;
import java.util.HashSet;

public class Main {

	public static void main(String[] args) throws Exception {
		ArrayList<String> seeds = new ArrayList<>();
		seeds.add("http://riweb.tibeica.com/crawl/index.html");
		seeds.add("http://www.dmoztools.net/index.html");
		Crawler crawler = new Crawler(seeds);
		crawler.crawl();
	}

}
