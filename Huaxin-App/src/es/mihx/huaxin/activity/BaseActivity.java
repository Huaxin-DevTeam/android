package es.mihx.huaxin.activity;

import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import es.mihx.huaxin.HuaxinApp;
import es.mihx.huaxin.R;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class BaseActivity extends ActionBarActivity {

	protected SideNavigationView sideNavigationView;
	protected ProgressDialog progressDialog;

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
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu_username);

		sideNavigationView.setMode(SideNavigationView.Mode.LEFT);
		sideNavigationView.setMenuClickCallback(new ISideNavigationCallback() {
			@Override
			public void onSideNavigationItemClick(int itemId) {
				// Validation clicking on side navigation item
				Log.i("BaseActivity", "SideItemClicked: " + itemId);
				switch (itemId) {
				case R.id.sidemenu_username:
					Log.i("BaseActivity", "Click item 1");
					break;
				default:
					Log.i("BaseActivity", "Click other item");
				}
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
