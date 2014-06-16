package es.mihx.app.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Item {

	private int id;
	private int authorId;
	private int categoryId;
	private String title;
	private String description;
	private double price;
	private String phone;
	private String location;
	private String imageUrl;
	private Date published;
	private Date end;
	private int numViews;
	private boolean premium;

	public Item(int id, int authorId, int categoryId, String title, String description,
			double price, String phone, String location, String imageUrl,
			String published, String end, int numViews, int premium) {
		this.id = id;
		this.authorId = authorId;
		this.categoryId = categoryId;
		this.title = title;
		this.description = description;
		this.price = price;
		this.phone = phone;
		this.location = location;
		this.imageUrl = imageUrl;
		this.published = stringToDate(published);
		this.end = stringToDate(end);
		this.numViews = numViews;
		this.premium = premium == 1 ? true : false;
	}

	private Date stringToDate(String date) {
		
		if (date == null)
			return null;
		
		String format = "yyyy-MM-dd HH:mm:ss";
		Date stringDate = null;
		
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(format,Locale.US);
			stringDate = simpledateformat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return stringDate;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(int catid) {
		this.categoryId = catid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Date getPublished() {
		return published;
	}

	public void setPublished(Date published) {
		this.published = published;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public int getNumViews() {
		return numViews;
	}

	public void setNumViews(int numViews) {
		this.numViews = numViews;
	}

	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
}
