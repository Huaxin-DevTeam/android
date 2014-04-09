package es.mihx.huaxin.activity;

import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import es.mihx.huaxin.HuaxinApp;
import es.mihx.huaxin.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class BaseActivity extends ActionBarActivity {

	protected HuaxinApp app;

	protected SideNavigationView sideNavigationView;

	public HuaxinApp getApp() {
		return (HuaxinApp) getApplication();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);

		sideNavigationView.setMode(SideNavigationView.Mode.LEFT);
		sideNavigationView.setMenuClickCallback(new ISideNavigationCallback() {
			@Override
			public void onSideNavigationItemClick(int itemId) {
				// Validation clicking on side navigation item
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
