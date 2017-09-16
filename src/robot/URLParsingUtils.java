package robot;

public class URLParsingUtils {
	public static String getHost(String url) {
		String urlNoProtocol = removeProtocol(url);
		if (urlNoProtocol.indexOf('/') == -1) { 
			return urlNoProtocol;
		}
		return urlNoProtocol.substring(0, urlNoProtocol.indexOf('/')); 
	}
	
	public static String getLocalAddress(String url) {
		String urlNoProtocol = removeProtocol(url);
		if (urlNoProtocol.indexOf('/') == -1) { 
			return "/";
		}
		return urlNoProtocol.substring(urlNoProtocol.indexOf('/'), urlNoProtocol.length());
	}
	
	public static String createPathToStorePage(String url) {
		String urlNoProtocol = removeProtocol(url);
		if (urlNoProtocol.endsWith("/")) 
			urlNoProtocol = urlNoProtocol + "index.html";
		return "workdir\\" + urlNoProtocol.replace("/", "\\");
	}
	
	public static String removeProtocol(String url) {
		int index = url.indexOf("http://");
    	index = index == -1 ? -1 : 7;
    	if (index == -1) {
    		index = url.indexOf("https://");
    		index = index == -1 ? 0 : 8;
    	}
    	return url.substring(index, url.length());
	}
	
	public static String getParentLink(String url) {
		String urlNoProtocol = removeProtocol(url);
		if (urlNoProtocol.indexOf('/') == -1) {
			return url;
		}
		return url.substring(0, url.lastIndexOf('/')+1);
	}
}
