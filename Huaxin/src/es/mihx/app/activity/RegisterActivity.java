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
public class RegisterActivity extends BaseActivity {

	EditText email, phone, password, password2;
	Button btn_register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.register));

		setContentView(R.layout.activity_register);

		initScreen();

		prepareControls();
	}

	private void initScreen() {
		btn_register = (Button) findViewById(R.id.btn_register);
		email = (EditText) findViewById(R.id.txt_email);
		phone = (EditText) findViewById(R.id.txt_phone);
		password = (EditText) findViewById(R.id.txt_password);
		password2 = (EditText) findViewById(R.id.txt_password2);
	}

	private void prepareControls() {
		btn_register.setOnClickListener(registerClick);
	}

	OnClickListener registerClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!Utils.isConnected(RegisterActivity.this)) {
				Utils.makeInfo(RegisterActivity.this,
						getString(R.string.no_internet));
			} else {

				// Validation
				String ph = phone.getText().toString().trim();
				String em = email.getText().toString().trim();
				String pw = password.getText().toString().trim();
				String pw2 = password2.getText().toString().trim();

				if (ph.compareTo("") == 0 || em.compareTo("") == 0
						|| pw.compareTo("") == 0 || pw2.compareTo("") == 0) {
					Utils.makeError(RegisterActivity.this, getResources()
							.getString(R.string.all_fields_required));
					return;
				}

				// Check pwd...
				if (pw.compareTo(pw2) != 0) {
					Utils.makeError(RegisterActivity.this, getResources()
							.getString(R.string.pwd_not_match));
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
							showLoading(false);
							String txt = (String) msg.obj;
							if (txt != null) {
								Utils.makeError(RegisterActivity.this, txt);
							}
						}
					}
				};

				Messenger messenger = new Messenger(handler);

				Intent intent = new Intent(RegisterActivity.this,
						WebService.class);
				intent.putExtra(WebService.PARAM_OPERATION,
						WebService.OPERATION_REGISTER);
				intent.putExtra(WebService.PARAM_MESSENGER_SERVICE, messenger);
				intent.putExtra(WebService.PARAM_PHONE, ph);
				intent.putExtra(WebService.PARAM_EMAIL, em);
				intent.putExtra(WebService.PARAM_PASSWORD, pw);

				startService(intent);

				showLoading(true);
			}
		}
	};
}
