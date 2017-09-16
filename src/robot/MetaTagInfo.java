package robot;

public class MetaTagInfo {
	// -1 not set
	// 1 the allowing option is set
	// 0 the disallowing option is set
	int follow;
	int index;
	int all;
	
	public MetaTagInfo() {
		follow = 1;
		index = 1;
		all = -1;
	}
	
	public MetaTagInfo(String content) {
		follow = -1;
		index = -1;
		all = -1;
		String[] options = content.split(",");
		for (String option : options) {
			switch (option.toLowerCase()) {
				case "follow" : follow = 1; break;
				case "nofollow" : follow = 0; break;
				case "index" : index = 1; break;
				case "noindex" : index = 0; break;
				case "all" : all = 1; break;
				case "none" : all = 0; break;
			}
		}
	}
	
	

	public void setFollow(int follow) {
		this.follow = follow;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setAll(int all) {
		this.all = all;
	}
	
	boolean canFollow() {
		if (follow == 1 || all == 1 || (all == -1 && follow == -1)) {
			return true;
		}
		return false;
	}
	
	boolean canIndex() {
		if (index == 1 || all == 1 || (all == -1 && index == -1)) {
			return true;
		}
		return false;
	}
	
	
	
	
}
