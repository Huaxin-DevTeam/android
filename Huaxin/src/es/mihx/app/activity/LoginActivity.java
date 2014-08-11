package es.mihx.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import es.mihx.app.service.WebService;
import es.mihx.app.utils.Constants;
import es.mihx.app.utils.Utils;
import es.mihx.app.R;

@SuppressLint("HandlerLeak")
public class LoginActivity extends BaseActivity {

	EditText username, password;
	Button btn_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			String message = b.getString("message");
			if (message != null)
				Utils.makeText(this, message);
		}

		setTitle(getString(R.string.login));

		setContentView(R.layout.activity_login);

		initScreen();

		prepareControls();
	}

	private void initScreen() {
		btn_login = (Button) findViewById(R.id.btn_login);
		username = (EditText) findViewById(R.id.txt_username);
		password = (EditText) findViewById(R.id.txt_password);
	}

	private void prepareControls() {
		btn_login.setOnClickListener(loginClick);
	}

	OnClickListener loginClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!Utils.isConnected(LoginActivity.this)) {
				Utils.makeInfo(LoginActivity.this,
						getString(R.string.no_internet));
			} else {

				// Validation
				String user = username.getText().toString().trim();
				String pass = password.getText().toString().trim();

				if (user.compareTo("") == 0) {
					Utils.toast(getString(R.string.username_notblank));
					return;
				}

				if (pass.compareTo("") == 0) {
					Utils.toast(getString(R.string.password_notblank));
					return;
				}

				// Call to ws...
				Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						if (msg.what == Constants.OK) {
							showLoading(false);
							Intent intent = new Intent();
							intent.putExtra("message", (String) msg.obj);
							setResult(Activity.RESULT_OK, intent);
							finish();
						} else {
							String txt = (String) msg.obj;
							if (txt != null) {
								Utils.makeError(LoginActivity.this, txt);
							}
							showLoading(false);
						}
					}
				};

				Messenger messenger = new Messenger(handler);

				Intent intent = new Intent(LoginActivity.this, WebService.class);
				intent.putExtra(WebService.PARAM_OPERATION,
						WebService.OPERATION_LOGIN);
				intent.putExtra(WebService.PARAM_MESSENGER_SERVICE, messenger);
				intent.putExtra(WebService.PARAM_USERNAME, user);
				intent.putExtra(WebService.PARAM_PASSWORD, pass);

				startService(intent);

				showLoading(true);
			}
		}
	};
}
