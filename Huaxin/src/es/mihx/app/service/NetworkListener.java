package es.mihx.app.service;

import es.mihx.app.HuaxinApp;
import es.mihx.app.activity.MainActivity;
import es.mihx.app.activity.SplashActivity;
import es.mihx.app.utils.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkListener extends BroadcastReceiver {

	boolean norepeat = true;

	@Override
	public void onReceive(final Context context, Intent intent) {
		boolean isConnected = haveNetworkConnection(context);

		if (context instanceof MainActivity) {
			Log.w("HUAXIN", "Estamos en el mainactivity!");
		} else {
			Log.w("HUAXIN", "NOOOOOOO Estamos en el mainactivity!");
		}

		if (isConnected
				&& (Constants.getApp() == null || Constants.getApp().getCategories() == null) 
				&& norepeat) {

			norepeat = false;

			HuaxinApp app = (HuaxinApp) context.getApplicationContext();
			Intent i = new Intent(context, SplashActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
			app.getActivity().finish();
		}
	}

	private boolean haveNetworkConnection(Context context) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
}