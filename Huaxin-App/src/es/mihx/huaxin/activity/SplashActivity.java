package es.mihx.huaxin.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import es.mihx.huaxin.R;
import es.mihx.huaxin.service.WebService;
import es.mihx.huaxin.utils.Constants;

@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_splash);
		
		Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				if(msg.what == Constants.OK){
					openMainActivity();
				}
			}
		};
		
		Messenger messenger = new Messenger(handler);
		
		Intent intent = new Intent(this,WebService.class);
		intent.putExtra(WebService.PARAM_OPERATION, WebService.OPERATION_INIT);
		intent.putExtra(WebService.PARAM_MESSENGER_SERVICE, messenger);
		this.startService(intent);
	}
	
	private void openMainActivity(){
		Intent i;
		i = new Intent(SplashActivity.this, MainActivity.class);
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
