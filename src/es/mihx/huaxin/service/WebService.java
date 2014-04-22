package es.mihx.huaxin.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Message;
import android.util.Log;
import es.mihx.huaxin.list.CategoryList;
import es.mihx.huaxin.list.ItemList;
import es.mihx.huaxin.model.Ad;
import es.mihx.huaxin.model.Category;
import es.mihx.huaxin.model.Item;
import es.mihx.huaxin.utils.Constants;

public class WebService extends BaseService {
	
	private static final String TAG = "WebService";
	
	/* OPERATIONS */
	public static final int OPERATION_INIT = 1;
	public static final int OPERATION_LOGIN = 2;
	public static final int OPERATION_SEARCH = 3;
	
	
	/* PARAMETERS */
	public static final String PARAM_OPERATION = "OPERATION";
	public static final String PARAM_QUERY = "QUERY";
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		super.onHandleIntent(intent);
		int op = intent.getIntExtra(PARAM_OPERATION, -1);
		
		switch(op){
			case OPERATION_INIT:
				init();
				break;
			
			case OPERATION_LOGIN:
				login();
				break;
				
			case OPERATION_SEARCH:
				String query = intent.getStringExtra(PARAM_QUERY);
				search(query);
				break;
			default:
				Log.e(TAG,"Operation not found: " + op);
		}
	}
	
	private void login(){
		
	}
	
	private void init(){
		
		Message msg = this.post(API.INIT, null);
		
		if(msg.what == Constants.HTTP_OK){
			
			try {
				//Process response
				JSONObject json = (JSONObject)msg.obj;
				//Log.i(TAG,json.toString());
				
				//Guardar categorias
				JSONArray categories = json.getJSONArray("categories");
				CategoryList list = new CategoryList();
				for(int i=0;i<categories.length();i++){
					JSONObject cat = categories.getJSONObject(i);
					Category c = new Category(cat.getInt("id"),cat.getString("name"));
					Log.v(TAG,"Adding category: " + c.getName());
					list.add(c);
				}
				Constants.getApp().setCategories(list);
				
				//Guardar anuncio
				JSONObject ad = json.getJSONObject("ad");
				Ad a = new Ad(ad.getString("image_url"), ad.getString("link"));
				Constants.getApp().setAd(a);
				
				this.sendMessage(Constants.OK, json);
			} catch (JSONException e) {
				Log.e(TAG,e.getMessage());
				this.sendMessage(Constants.KO, null);
			}
		}else{
			Log.e(TAG,"ERROR");
			this.sendMessage(Constants.KO, null);
		}
	}
	
	private void search(String query){

		Message msg = this.post(API.SEARCH + "/" + query, null);
		
		if(msg.what == Constants.HTTP_OK){
			
			try{
			JSONObject json = (JSONObject)msg.obj;
			JSONArray items = json.getJSONArray("items");
			ItemList list = new ItemList();
			for(int i=0;i<items.length();i++){
				JSONObject o = items.getJSONObject(i);
				Item item = new Item(
						o.getInt("id"), o.getInt("category_id"),
						o.getString("title"), o.getString("description"), 
						o.getDouble("price"), o.getString("phone"), 
						o.getString("location"), o.getString("image_url"), 
						o.getString("date_published"), o.getString("date_end"),
						o.getInt("num_views"), o.getInt("premium"));
				list.add(item);
				Log.v(TAG,"Adding item: " + item.getTitle());
			}
			
			//Guardamos la lista
			Constants.getApp().setItems(list);
			this.sendMessage(Constants.OK, null);
			
			}catch (JSONException e) {
				Log.e(TAG + "::JSONException",e.getMessage());
				this.sendMessage(Constants.KO, null);
			}			
			
		}else{
			Log.e(TAG,"Communication ERROR");
			this.sendMessage(Constants.KO, null);
		}
	}

}
