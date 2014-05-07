package es.mihx.huaxin.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import es.mihx.huaxin.HuaxinApp;
import es.mihx.huaxin.R;
import es.mihx.huaxin.model.User;
import es.mihx.huaxin.utils.Constants;
import es.mihx.huaxin.utils.Utils;

public class BaseActivity extends ActionBarActivity {

	protected SideNavigationView sideNavigationView;
	protected ProgressDialog progressDialog;

	private int REQ_LOGIN = 1;

	public HuaxinApp getApp() {
		return (HuaxinApp) getApplication();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blank);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);

		if (Constants.getApp().getUser() != null) {
			setMenuForUser();
		} else {
			setMenuForAnonymous();
		}

		sideNavigationView.setMode(SideNavigationView.Mode.LEFT);
		sideNavigationView.setMenuClickCallback(new ISideNavigationCallback() {
			@Override
			public void onSideNavigationItemClick(int itemId) {
				// Validation clicking on side navigation item
				switch (itemId) {
				case R.id.sidemenu_search:
					Log.i("BaseActivity", "Click New Search...");
					startActivity(new Intent(getApplicationContext(),
							MainActivity.class));
					//finish();
					break;
				case R.id.sidemenu_login:
					startActivityForResult(new Intent(getApplicationContext(),LoginActivity.class), REQ_LOGIN);
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
	
	protected void setMenuForUser(){
		User user = Constants.getApp().getUser();
		String title = user.getPhone() + " (" + user.getNum_credits() + ")";
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu_username);
		sideNavigationView.setItemText(0, title);
	}
	
	protected void setMenuForAnonymous(){
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu_no_username);
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
		}
	}
	
	protected void logout(){
		Constants.getApp().setUser(null); //Borrar el usuario
		Utils.setToken(null); //Borrar el token
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
		case android.R.id.home:
			sideNavigationView.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
