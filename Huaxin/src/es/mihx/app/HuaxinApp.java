package es.mihx.app;

import android.app.Application;
import es.mihx.app.list.CategoryList;
import es.mihx.app.list.CreditsList;
import es.mihx.app.list.ItemList;
import es.mihx.app.model.Ad;
import es.mihx.app.model.User;
import es.mihx.app.utils.Constants;

public class HuaxinApp extends Application {

	public CategoryList categories;
	public Ad ad;
	public ItemList items;
	public User user;
	public CreditsList creds;

	@Override
	public void onCreate() {
		super.onCreate();
		Constants.setApp(this);
	}

	public CategoryList getCategories() {
		return categories;
	}

	public void setCategories(CategoryList categories) {
		this.categories = categories;
	}

	public Ad getAd() {
		return ad;
	}

	public void setAd(Ad ad) {
		this.ad = ad;
	}

	public ItemList getItems() {
		return this.items;
	}

	public void setItems(ItemList items) {
		this.items = items;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setCreditOptions(CreditsList creds) {
		this.creds = creds;
	}

	public CreditsList getCreditOptions() {
		return this.creds;
	}
}
