package es.mihx.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import es.mihx.app.service.WebService;
import es.mihx.app.utils.Constants;
import es.mihx.app.utils.Utils;
import es.mihx.app.R;

@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_splash);

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				if (msg.what == Constants.OK) {
					openMainActivity();
				}
			}
		};

		Messenger messenger = new Messenger(handler);

		// Recuperar token para autologin
		String token = Utils.getToken();
		Log.i("SplashActivity", "TOKEN: " + token);

		if (Utils.isConnected(this)) {

			Intent intent = new Intent(this, WebService.class);
			intent.putExtra(WebService.PARAM_OPERATION,
					WebService.OPERATION_INIT);
			intent.putExtra(WebService.PARAM_MESSENGER_SERVICE, messenger);
			intent.putExtra(WebService.PARAM_TOKEN, token);
			this.startService(intent);
		} else {
			// TODO cargar las categorias previamente guardadas y llevar a la
			// pag principal
		}
	}

	private void openMainActivity() {
		Intent i;
		i = new Intent(SplashActivity.this, MainActivity.class);
		i.putExtra(Constants.FROM_SPLASH, true);
		startActivity(i);

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
