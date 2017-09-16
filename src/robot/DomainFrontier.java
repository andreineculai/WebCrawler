package robot;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class DomainFrontier {
	Calendar time;
	LinkedHashSet<String> links;

	public DomainFrontier() {
		time = Calendar.getInstance();
		links = new LinkedHashSet<>();
	}

	public boolean canMakeCall() {
		return Calendar.getInstance().after(time);
	}

	public String getNextLink() {
		Iterator<String> it = links.iterator();
		String url = it.next();
		it.remove();
		time = Calendar.getInstance();
		time.add(Calendar.SECOND, 1);
		return url;
	}

	public boolean isEmpty() {
		return links.isEmpty();
	}

	public void addLink(String url) {
		String link = (url.charAt(url.length() - 1) == '/') ? url : url;
		links.add(link);
	}

}
