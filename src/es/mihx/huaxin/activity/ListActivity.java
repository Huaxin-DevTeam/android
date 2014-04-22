package es.mihx.huaxin.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import es.mihx.huaxin.R;
import es.mihx.huaxin.fragment.ListFragment;
import es.mihx.huaxin.fragment.PhotoFragment;
import es.mihx.huaxin.utils.Constants;

@SuppressLint("HandlerLeak")
public class ListActivity extends BaseActivity {

	private Fragment fragment;

	private Button btn_list, btn_photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list);
		setTitle(Constants.getApp().getItems().size() + " "
				+ getString(R.string.anuncios));

		initScreen();

		prepareControls();
	}

	private void initScreen() {
		btn_list = (Button) findViewById(R.id.btn_list);
		btn_photo = (Button) findViewById(R.id.btn_photo);
	}

	private void prepareControls() {

		// Botones de cambio
		btn_list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!(fragment instanceof ListFragment))
					switchFragment(new ListFragment());
			}
		});

		btn_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!(fragment instanceof PhotoFragment))
					switchFragment(new PhotoFragment());
			}
		});

		switchFragment(new ListFragment());

		// Ponemos los anuncios
		// ArrayAdapter<String> adapter = new
		// ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constants.getApp().getCategories().getNames());
		// list_items.setAdapter(adapter);

	}

	/*
	 * Elimina la cola de llamadas de fragments que se pueden recuperar con el
	 * boton back
	 */
	private void ClearBackStack() {

		FragmentManager fm = getSupportFragmentManager();
		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}
	}

	private void switchFragment(Fragment fragment) {
		ClearBackStack();

		this.fragment = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, fragment).commit();
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
