package es.mihx.huaxin.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Message;
import android.util.Log;
import es.mihx.huaxin.R;
import es.mihx.huaxin.list.CategoryList;
import es.mihx.huaxin.list.ItemList;
import es.mihx.huaxin.model.Ad;
import es.mihx.huaxin.model.Category;
import es.mihx.huaxin.model.Item;
import es.mihx.huaxin.model.User;
import es.mihx.huaxin.utils.Constants;
import es.mihx.huaxin.utils.Utils;

public class WebService extends BaseService {

	private static final String TAG = "WebService";

	/* OPERATIONS */
	public static final int OPERATION_INIT = 1;
	public static final int OPERATION_LOGIN = 2;
	public static final int OPERATION_SEARCH = 3;
	public static final int OPERATION_FAVS = 4;
	public static final int OPERATION_MYADS = 5;
	public static final int OPERATION_NEW_AD = 6;

	/* PARAMETERS */
	public static final String PARAM_OPERATION = "OPERATION";
	public static final String PARAM_QUERY = "QUERY";
	public static final String PARAM_CATEGORY_ID = "CAT_ID";
	public static final String PARAM_USERNAME = "USERNAME";
	public static final String PARAM_PASSWORD = "PASSWORD";
	public static final String PARAM_TOKEN = "TOKEN";
	public static final String PARAM_JSON_LIST = "JSON_LIST";
	public static final String PARAM_TITLE = "TITLE";
	public static final String PARAM_DESCRIPTION = "DESCRIPTION";
	public static final String PARAM_PRICE = "PRICE";
	public static final String PARAM_PHONE = "PHONE";
	public static final String PARAM_LOCATION = "LOCATION";
	public static final String PARAM_PREMIUM = "PREMIUM";
	public static final String PARAM_DURATION = "DURATION";

	@Override
	protected void onHandleIntent(Intent intent) {
		super.onHandleIntent(intent);
		int op = intent.getIntExtra(PARAM_OPERATION, -1);

		switch (op) {
		case OPERATION_INIT:
			String token = intent.getStringExtra(PARAM_TOKEN);
			init(token);
			break;

		case OPERATION_LOGIN:
			String username = intent.getStringExtra(PARAM_USERNAME);
			String password = intent.getStringExtra(PARAM_PASSWORD);
			login(username, password);
			break;

		case OPERATION_SEARCH:
			String query = intent.getStringExtra(PARAM_QUERY);
			int category = intent.getIntExtra(PARAM_CATEGORY_ID, -1);
			search(query, category);
			break;
		case OPERATION_FAVS:
			String favs = intent.getStringExtra(PARAM_JSON_LIST);
			favs(favs);
			break;
		case OPERATION_MYADS:
			myads();
			break;
		case OPERATION_NEW_AD:
			String title = intent.getStringExtra(PARAM_TITLE);
			String description = intent.getStringExtra(PARAM_DESCRIPTION);
			int category_id = intent.getIntExtra(PARAM_CATEGORY_ID, -1);
			float price = intent.getFloatExtra(PARAM_PRICE, -1);
			String phone = intent.getStringExtra(PARAM_PHONE);
			String location = intent.getStringExtra(PARAM_LOCATION);
			int duration = intent.getIntExtra(PARAM_DURATION, -1);
			boolean premium = intent.getBooleanExtra(PARAM_PREMIUM, false);

			newAd(title, description, category_id, price, phone, location,
					duration, premium);

			break;
		default:
			Log.e(TAG, "Operation not found: " + op);
			this.sendMessage(Constants.KO, null);
		}
	}

	private void newAd(String title, String description, int category_id,
			float price, String phone, String location, int duration,
			boolean premium) {

		try {
			JSONObject params = new JSONObject();
			params.put("token", Constants.getApp().getUser().getToken());
			params.put("title", title);
			params.put("description", description);
			params.put("category_id", category_id);
			params.put("price", price);
			params.put("phone", phone);
			params.put("location", location);
			params.put("duration", duration);
			params.put("premium", premium);

			Message msg = this.post(API.NEWAD, params);

			if (msg.what == Constants.HTTP_OK) {

				try {
					JSONObject json = (JSONObject) msg.obj;
					String message = json.getString("message");

					this.sendMessage(Constants.OK, message);

				} catch (JSONException e) {

					// Mostrar error!
					try {

						JSONObject json = (JSONObject) msg.obj;
						JSONObject error = json.getJSONObject("error");

						String errmsg = error.optString("message");
						if (errmsg == null)
							errmsg = error.optString("description");

						this.sendMessage(Constants.KO, errmsg);

					} catch (JSONException e1) {
						Log.e(TAG + "::JSONException", e.getMessage());
						this.sendMessage(Constants.KO, getResources().getString(R.string.unknown_error));
					}
				}

			} else {
				Log.e(TAG, "Communication ERROR");
				this.sendMessage(Constants.KO, getResources().getString(R.string.comm_error));
			}
		} catch (JSONException e1) {
			Log.e(TAG + "::JSONException", e1.getMessage());
		}

	}

	private void init(String token) {

		// Login!?
		if (token != null && token != "") {

			try {
				JSONObject jparams = new JSONObject();
				jparams.put("token", token);
				Message m = this.post(API.AUTOLOGIN, jparams);

				JSONObject json = (JSONObject) m.obj;
				JSONObject error = json.optJSONObject("error");

				if (error == null) {
					User user = new User();
					user.setEmail(json.getString("email"));
					user.setPhone(json.getString("phone"));
					user.setToken(json.getString("token"));
					user.setNum_credits(json.getInt("num_credits"));
					user.setId(json.getInt("id"));

					Constants.getApp().setUser(user);
				} else {
					Log.e(TAG,
							error.getString("description") + ": "
									+ error.getString("message"));
					this.sendMessage(Constants.KO, error.getString("message"));
				}

			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
				this.sendMessage(Constants.KO, null);
			}

		}

		// LOAD CATS, ETC...
		Message msg = this.post(API.INIT);

		if (msg.what == Constants.HTTP_OK) {

			try {
				// Process response
				JSONObject json = (JSONObject) msg.obj;

				// Guardar categorias
				JSONArray categories = json.getJSONArray("categories");
				CategoryList list = new CategoryList();
				for (int i = 0; i < categories.length(); i++) {
					JSONObject cat = categories.getJSONObject(i);
					Category c = new Category(cat.getInt("id"),
							cat.getString("name"));
					Log.v(TAG, "Adding category: " + c.getName());
					list.add(c);
				}
				Constants.getApp().setCategories(list);

				// Guardar anuncio
				JSONObject ad = json.getJSONObject("ad");
				Ad a = new Ad(ad.getString("image_url"), ad.getString("link"));
				Constants.getApp().setAd(a);

				this.sendMessage(Constants.OK, json);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
				this.sendMessage(Constants.KO, null);
			}
		} else {
			Log.e(TAG, "ERROR");
			this.sendMessage(Constants.KO, null);
		}
	}

	private void search(String query, int category_id) {

		JSONObject jparams = new JSONObject();
		try {
			jparams.put("query", query);
			jparams.put("category", category_id);
		} catch (JSONException e1) {
			Log.e(TAG + "::JSONException", e1.getMessage());
		}
		Message msg = this.post(API.SEARCH, jparams);

		if (msg.what == Constants.HTTP_OK) {

			try {
				JSONObject json = (JSONObject) msg.obj;
				JSONArray items = json.getJSONArray("items");
				ItemList list = new ItemList();
				for (int i = 0; i < items.length(); i++) {
					JSONObject o = items.getJSONObject(i);
					Item item = new Item(o.getInt("id"),
							o.getInt("category_id"), o.getString("title"),
							o.getString("description"), o.getDouble("price"),
							o.getString("phone"), o.getString("location"),
							o.getString("image_url"),
							o.getString("date_published"),
							o.getString("date_end"), o.getInt("num_views"),
							o.getInt("premium"));
					list.add(item);
					Log.v(TAG, "Adding item: " + item.getTitle());
				}

				// Guardamos la lista
				Constants.getApp().setItems(list);
				this.sendMessage(Constants.OK, null);

			} catch (JSONException e) {
				Log.e(TAG + "::JSONException", e.getMessage());
				this.sendMessage(Constants.KO, null);
			}

		} else {
			Log.e(TAG, "Communication ERROR");
			this.sendMessage(Constants.KO, null);
		}
	}

	private void login(String username, String password) {
		JSONObject params = new JSONObject();
		try {
			params.put("username", username);
			params.put("password", password);
		} catch (JSONException e1) {
			Log.e(TAG + "::JSONException", e1.getMessage());
		}

		Message msg = this.post(API.LOGIN, params);

		if (msg.what == Constants.HTTP_OK) {

			try {
				JSONObject json = (JSONObject) msg.obj;
				JSONObject error = json.optJSONObject("error");

				if (error == null) {
					User user = new User();
					user.setEmail(json.getString("email"));
					user.setPhone(json.getString("phone"));
					user.setToken(json.getString("token"));
					user.setNum_credits(json.getInt("num_credits"));
					user.setId(json.getInt("id"));

					Constants.getApp().setUser(user);
					Utils.setToken(user.getToken()); // Guardarlo para autologin

					this.sendMessage(Constants.OK, json.getString("message"));

				} else {
					Log.e(TAG,
							error.getString("description") + ": "
									+ error.getString("message"));
					this.sendMessage(Constants.KO, error.getString("message"));
				}
			} catch (JSONException e) {
				Log.e(TAG + "::JSONException", e.getMessage());
				this.sendMessage(Constants.KO, null);
			}

		} else {
			Log.e(TAG, "Communication ERROR");
			this.sendMessage(Constants.KO, null);
		}
	}

	private void favs(String favs) {

		try {
			JSONObject params = new JSONObject(favs);

			Message msg = this.post(API.LIST, params);

			if (msg.what == Constants.HTTP_OK) {

				try {
					JSONObject json = (JSONObject) msg.obj;
					JSONArray items = json.getJSONArray("items");
					ItemList list = new ItemList();
					for (int i = 0; i < items.length(); i++) {
						JSONObject o = items.getJSONObject(i);
						Item item = new Item(o.getInt("id"),
								o.getInt("category_id"), o.getString("title"),
								o.getString("description"),
								o.getDouble("price"), o.getString("phone"),
								o.getString("location"),
								o.getString("image_url"),
								o.getString("date_published"),
								o.getString("date_end"), o.getInt("num_views"),
								o.getInt("premium"));
						list.add(item);
						Log.v(TAG, "Adding item: " + item.getTitle());
					}

					// Guardamos la lista
					Constants.getApp().setItems(list);
					this.sendMessage(Constants.OK, null);

				} catch (JSONException e) {
					Log.e(TAG + "::JSONException", e.getMessage());
					this.sendMessage(Constants.KO, null);
				}

			} else {
				Log.e(TAG, "Communication ERROR");
				this.sendMessage(Constants.KO, null);
			}
		} catch (JSONException e1) {
			Log.e(TAG + "::JSONException", e1.getMessage());
		}
	}

	private void myads() {

		try {
			JSONObject params = new JSONObject();
			params.put("token", Constants.getApp().getUser().getToken());

			Message msg = this.post(API.MYADS, params);

			if (msg.what == Constants.HTTP_OK) {

				try {
					JSONObject json = (JSONObject) msg.obj;
					JSONArray items = json.getJSONArray("items");
					ItemList list = new ItemList();
					for (int i = 0; i < items.length(); i++) {
						JSONObject o = items.getJSONObject(i);
						Item item = new Item(o.getInt("id"),
								o.getInt("category_id"), o.getString("title"),
								o.getString("description"),
								o.getDouble("price"), o.getString("phone"),
								o.getString("location"),
								o.getString("image_url"),
								o.getString("date_published"),
								o.getString("date_end"), o.getInt("num_views"),
								o.getInt("premium"));
						list.add(item);
						Log.v(TAG, "Adding item: " + item.getTitle());
					}

					// Guardamos la lista
					Constants.getApp().setItems(list);
					this.sendMessage(Constants.OK, null);

				} catch (JSONException e) {
					Log.e(TAG + "::JSONException", e.getMessage());
					this.sendMessage(Constants.KO, null);
				}

			} else {
				Log.e(TAG, "Communication ERROR");
				this.sendMessage(Constants.KO, null);
			}
		} catch (JSONException e1) {
			Log.e(TAG + "::JSONException", e1.getMessage());
		}
	}
}
