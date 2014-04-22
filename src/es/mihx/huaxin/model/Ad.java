package es.mihx.huaxin.model;

public class Ad {
	
	private String src;
	private String link;
	
	public Ad(String src, String link) {
		this.src = src;
		this.link = link;
	}
	
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}

}
