package robot;

import java.util.Calendar;
import java.util.Date;

public class URLInfo {
	public String url;
	public Date date;
	public URLInfo(String url, int ttl) {
		super();
		this.url = url;
		date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND, ttl);
		date = c.getTime();
	}
	
	boolean isValid (Date currentTime) {
		if (currentTime.before(date)) {
			return true;
		}
		return false;
	}
	
	
	
	
}
