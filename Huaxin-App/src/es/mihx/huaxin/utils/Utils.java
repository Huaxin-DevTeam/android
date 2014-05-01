package es.mihx.huaxin.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import es.mihx.huaxin.R;

public class Utils {

	public static void toast(String message) {
		Toast.makeText(Constants.getApp(), message, Toast.LENGTH_LONG).show();
	}

	public static String numberFormat(double f) {
		NumberFormat nf = new DecimalFormat("##.#");
		return nf.format(f);
	}

	public static void share(Context context, int id, String title) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");

		String shareSubject = context.getString(R.string.share_subject);
		String shareBody = title + " " + Utils.createUrl(id);

		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				shareSubject);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		context.startActivity(Intent.createChooser(sharingIntent,
				context.getString(R.string.share_via)));
	}

	public static String createUrl(int id) {
		return Constants.BASEURL + Constants.VIEWITEMURL + id;
	}

	public static boolean switchFavorite(int _id) {
		
		String id = String.valueOf(_id);
		boolean exist = false;
		
		try {
			
			SharedPreferences prefs = Constants.getApp().getSharedPreferences(
					Constants.PREFS_NAME, Context.MODE_PRIVATE);
			String favs = prefs.getString(Constants.PREFS_FAV_KEY, null);

			JSONObject json;
			if (favs != null) {
				json = new JSONObject(favs);
				exist = json.optBoolean(id);
			} else {
				json = new JSONObject();
			}
			
			json.put(id, !exist);
			
			// Save again
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(Constants.PREFS_FAV_KEY, json.toString());
			editor.commit();

			Log.i("Utils::switchFavorite", "Favorite saved! " + json.toString(1));

		} catch (JSONException e) {
			Log.e("Utils::switchFavorite", "JSONException: " + e.getMessage());
			e.printStackTrace();
		}
		
		return !exist;
	}
	
	public static boolean isFavorite(int _id){
		try {
			String id = String.valueOf(_id);
			SharedPreferences prefs = Constants.getApp().getSharedPreferences(
					Constants.PREFS_NAME, Context.MODE_PRIVATE);
			String favs = prefs.getString(Constants.PREFS_FAV_KEY, null);

			JSONObject json;
			if (favs != null) {
				json = new JSONObject(favs);
				return json.optBoolean(id);
			}

		} catch (JSONException e) {
			Log.e("Utils::isFavorite", "JSONException: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

}
