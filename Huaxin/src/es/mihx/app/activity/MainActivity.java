package es.mihx.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import es.mihx.app.service.WebService;
import es.mihx.app.utils.Constants;
import es.mihx.app.utils.Utils;
import es.mihx.app.R;

@SuppressLint("HandlerLeak")
public class MainActivity extends BaseActivity {

	private EditText txt_search;
	private Spinner spin_categories;
	private Button btn_search, btn_new_ad;
	private ImageView img_publi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		initScreen();

		prepareControls();

		Intent intent = getIntent();
		boolean from_splash = intent.getBooleanExtra(Constants.FROM_SPLASH,
				false);
		boolean from_logout = intent.getBooleanExtra(Constants.FROM_LOGOUT,
				false);
		if (!Utils.isConnected(this)) {
			Utils.makeInfo(this, getString(R.string.no_internet));
		} else if (from_splash && Constants.getApp().getUser() != null) {
			Utils.makeText(this, getString(R.string.welcome_back) + " "
					+ Constants.getApp().getUser().getEmail());
		} else if (from_logout) {
			Utils.makeText(this, getString(R.string.logout_ok));
		}
	}

	private void initScreen() {
		txt_search = (EditText) findViewById(R.id.txt_search);
		spin_categories = (Spinner) findViewById(R.id.spin_categories);
		btn_search = (Button) findViewById(R.id.btn_search);
		btn_new_ad = (Button) findViewById(R.id.btn_new_ad);
		img_publi = (ImageView) findViewById(R.id.img_publi);
	}

	private void prepareControls() {
		// Ponemos categorias
		if (Constants.getApp().getCategories() != null) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item, Constants
							.getApp().getCategories().getNames());
			spin_categories.setAdapter(adapter);
		}

		// Ponemos la publi
		if (Constants.getApp().getAd() != null) {
			String url = Uri.encode(Constants.BASEURL
					+ Constants.getApp().getAd().getSrc(), ":./_");
			UrlImageViewHelper.setUrlDrawable(img_publi, url);
			Log.w("setting publi...", url);
			img_publi.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(Constants.getApp().getAd().getLink()));
					startActivity(intent);
				}
			});
		}

		// Botón buscar
		btn_search.setOnClickListener(searchClick);

		// Botón nuevo anuncio
		btn_new_ad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Abrir form para anuncios
				if (!Utils.isConnected(MainActivity.this)) {
					Utils.makeInfo(MainActivity.this,
							getString(R.string.no_internet));
				} else {
					openFormActivity();
				}
			}
		});
	}

	private OnClickListener searchClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!Utils.isConnected(MainActivity.this)
					|| Constants.getApp() == null
					|| Constants.getApp().getCategories() == null) {
				Utils.makeInfo(MainActivity.this,
						getString(R.string.no_internet));
			} else {

				String query = txt_search.getText().toString().trim();

				// Checks...
				// if (query.equalsIgnoreCase("")) {
				// Utils.makeInfo(MainActivity.this,
				// getResources().getString(R.string.query_null));
				// return;
				// }
				//
				// if (query.length() < 2) {
				// Utils.makeInfo(MainActivity.this,
				// getResources().getString(R.string.query_short));
				// return;
				// }

				// Category selected
				int category_id = Constants.getApp().getCategories()
						.get(spin_categories.getSelectedItemPosition()).getId();

				// Todo ok, pedimos resultados y mostramos loader
				Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);

						if (msg.what == Constants.OK) {
							openListActivity();
						}
					}
				};

				Messenger messenger = new Messenger(handler);

				Intent intent = new Intent(MainActivity.this, WebService.class);
				intent.putExtra(WebService.PARAM_OPERATION,
						WebService.OPERATION_SEARCH);
				intent.putExtra(WebService.PARAM_QUERY, query);
				intent.putExtra(WebService.PARAM_CATEGORY_ID, category_id);
				intent.putExtra(WebService.PARAM_MESSENGER_SERVICE, messenger);
				startService(intent);

				showLoading(true);
			}
		}
	};

	private void openFormActivity() {
		if (Constants.getApp().getUser() != null) {
			Intent intent = new Intent(this, FormActivity.class);
			startActivity(intent);
		} else {
			Utils.makeInfo(this, getResources().getString(R.string.need_login));
		}
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
