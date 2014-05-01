package es.mihx.huaxin.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class BaseService extends IntentService {

	public static final String TAG = "BASE_SRV";

	private static final String NAME = "BaseService";

	protected Message message;
	protected Messenger messenger;
	protected Handler handler;

	static CookieStore cookieStore = new BasicCookieStore();

	/* PARAMETERS */
	public static final String PARAM_MESSENGER_SERVICE = "MESSENGER_SERVICE";

	public BaseService() {
		super(NAME);
		this.messenger = null;
		this.handler = null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.messenger = (Messenger) intent
				.getParcelableExtra(PARAM_MESSENGER_SERVICE);
		this.message = new Message();
	}

	protected void sendMessage(int what, Object obj) {
		try {
			if (messenger != null) {
				this.message.what = what;
				this.message.obj = obj;
				this.messenger.send(this.message);

			} else {
				Log.e(TAG, "No hay Messenger inicializado...");
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/* LLamadas al ws. */
	protected Message post(String url) {
		return post(url, null);
	}

	protected Message post(String url, JSONObject json) {

		try {
			BufferedReader in = null;
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(url);
			Log.i(TAG, "POST " + url);

			// Construimos llamada con parametros
			if (json != null) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
				nameValuePairs.add(new BasicNameValuePair("params",json.toString()));
//				for (int i = 0; i < keys.length; i++) {
//					nameValuePairs.add(new BasicNameValuePair(keys[i], values[i]));
//				}
				HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs);
				request.addHeader(entity.getContentType());
				request.setEntity(entity);
				Log.i("POST data", nameValuePairs.toString());
			}

			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			HttpResponse response = client.execute(request, localContext);

			InputStreamReader ix = new InputStreamReader(response.getEntity()
					.getContent());
			in = new BufferedReader(ix, 4096);

			String accum = "";
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				accum += line + NL;
			}
			
			Log.v(TAG + "::Response", accum);

			Message msg = new Message();
			msg.what = response.getStatusLine().getStatusCode();
			msg.obj = new JSONObject(accum);

			return msg;

		} catch (UnsupportedEncodingException e) {
			Log.e(TAG+"::UnsupportedEncodingException", e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e(TAG+"::ClientProtocolException", e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG+"::IllegalStateException", e.getMessage());
		} catch (IOException e) {
			Log.e(TAG+"::IOException", e.getMessage());
		} catch (JSONException e) {
			Log.e(TAG+"::JSONException", e.getMessage());
		}
		return null;
	}

}
