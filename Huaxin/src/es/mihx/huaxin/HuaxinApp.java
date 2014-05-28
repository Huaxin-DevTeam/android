package es.mihx.huaxin;

import android.app.Application;
import es.mihx.huaxin.list.CategoryList;
import es.mihx.huaxin.list.CreditsList;
import es.mihx.huaxin.list.ItemList;
import es.mihx.huaxin.model.Ad;
import es.mihx.huaxin.model.User;
import es.mihx.huaxin.utils.Constants;

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
