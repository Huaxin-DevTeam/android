package es.mihx.app.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.bugsense.trace.BugSenseHandler;
import com.crittercism.app.Crittercism;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import es.mihx.app.HuaxinApp;
import es.mihx.app.model.User;
import es.mihx.app.service.WebService;
import es.mihx.app.utils.Constants;
import es.mihx.app.utils.Utils;
import es.mihx.app.R;

@SuppressLint("HandlerLeak")
public class BaseActivity extends ActionBarActivity {

	protected SideNavigationView sideNavigationView;
	protected ProgressDialog progressDialog;

	private int REQ_REG = 0;
	private int REQ_LOGIN = 1;
	private int REQ_PURCHASE = 2;

	public HuaxinApp getApp() {
		return (HuaxinApp) getApplication();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BugSenseHandler.initAndStartSession(BaseActivity.this, "c85f9471");
		Crittercism.initialize(getApplicationContext(), "539f28dab573f121a7000002");

		setContentView(R.layout.activity_blank);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);

		if (Constants.getApp().getUser() != null) {
			setMenuForUser();
			BugSenseHandler.addCrashExtraData("user", Constants.getApp().getUser().getPhone());
		} else {
			setMenuForAnonymous();
		}

		sideNavigationView.setMode(SideNavigationView.Mode.LEFT);
		sideNavigationView.setMenuClickCallback(new ISideNavigationCallback() {
			@Override
			public void onSideNavigationItemClick(int itemId) {
				// Validation clicking on side navigation item
				switch (itemId) {
				case R.id.sidemenu_favorites:
					loadFavorites();
					break;
				case R.id.sidemenu_myads:
					loadMyads();
					break;
				case R.id.sidemenu_search:
					Log.i("BaseActivity", "Click New Search...");
					startActivity(new Intent(getApplicationContext(),
							MainActivity.class));
					// finish();
					break;
				case R.id.sidemenu_new_ad:
					if (Constants.getApp().getUser() != null) {
						startActivity(new Intent(getApplicationContext(),
								FormActivity.class));
					} else {
						sideNavigationView.hideMenu();
						Utils.makeInfo(BaseActivity.this, getResources()
								.getString(R.string.need_login));
					}

					break;
				case R.id.sidemenu_login:
					startActivityForResult(new Intent(getApplicationContext(),
							LoginActivity.class), REQ_LOGIN);
					break;

				case R.id.sidemenu_register:
					startActivityForResult(new Intent(getApplicationContext(),
							RegisterActivity.class), REQ_REG);
					break;

				case R.id.sidemenu_buy:
					startActivityForResult(new Intent(getApplicationContext(),
							PurchaseActivity.class), REQ_PURCHASE);
					break;

				case R.id.sidemenu_logout:
					logout();
					break;
				default:
					Log.i("BaseActivity", "Click other item");
				}
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}

	protected void setMenuForUser() {
		User user = Constants.getApp().getUser();
		String title = user.getPhone() + " (" + user.getNum_credits() + ")";
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu_username);
		sideNavigationView.setItemText(0, title);
	}

	protected void setMenuForAnonymous() {
		sideNavigationView
				.setMenuItems(R.menu.side_navigation_menu_no_username);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == REQ_LOGIN) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				String message = data.getStringExtra("message");
				Utils.makeText(this, message);
				setMenuForUser();
			}
		} else if (requestCode == REQ_REG) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				String message = data.getStringExtra("message");
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				intent.putExtra("message", message);
				// setMenuForUser();
				startActivityForResult(intent, REQ_LOGIN);
			}
		}
	}

	protected void loadFavorites() {
		String favs = Utils.getFavorites();

		if (favs == null) {
			Utils.makeInfo(this, getString(R.string.error_no_favs));
			return;
		}

		// Todo ok, pedimos resultados y mostramos loader
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				showLoading(false);
				if (msg.what == Constants.OK) {
					openListActivity();
				} else if (msg.obj != null) {
					Utils.makeError(BaseActivity.this, (String) msg.obj);
				}
			}
		};

		Messenger messenger = new Messenger(handler);

		Intent intent = new Intent(this, WebService.class);
		intent.putExtra(WebService.PARAM_OPERATION, WebService.OPERATION_FAVS);
		intent.putExtra(WebService.PARAM_JSON_LIST, favs);
		intent.putExtra(WebService.PARAM_MESSENGER_SERVICE, messenger);
		startService(intent);

		showLoading(true);
	}

	protected void loadMyads() {

		// Todo ok, pedimos resultados y mostramos loader
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				showLoading(false);
				if (msg.what == Constants.OK) {

					openListActivity();
				} else if (msg.obj != null) {
					Utils.makeError(BaseActivity.this, (String) msg.obj);
				}
			}
		};

		Messenger messenger = new Messenger(handler);

		Intent intent = new Intent(this, WebService.class);
		intent.putExtra(WebService.PARAM_OPERATION, WebService.OPERATION_MYADS);
		intent.putExtra(WebService.PARAM_MESSENGER_SERVICE, messenger);
		startService(intent);

		showLoading(true);
	}

	protected void openListActivity() {
		showLoading(false);
		Intent intent = new Intent(this, ListActivity.class);
		startActivity(intent);
	}

	protected void logout() {
		Constants.getApp().setUser(null); // Borrar el usuario
		Utils.setToken(null); // Borrar el token
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Constants.FROM_LOGOUT, true);
		startActivity(intent);
	}

	protected void showLoading(boolean show) {
		if (show) {
			this.progressDialog = new ProgressDialog(this);
			this.progressDialog.setMessage(getString(R.string.loading));
			this.progressDialog.setCancelable(false);
			this.progressDialog.show();
		} else {
			if (this.progressDialog.isShowing())
				this.progressDialog.dismiss();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_openMenu:
		case android.R.id.home:
			sideNavigationView.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public void updateCredits(int num, boolean add) {
		User user = Constants.getApp().getUser();
		int creds = user.getNum_credits();

		creds = add ? creds + num : creds - num;
		user.setNum_credits(creds);
		setMenuForUser();
	}

	@Override
	protected void onDestroy() {
		BugSenseHandler.closeSession(BaseActivity.this);
		super.onDestroy();
	}

}
