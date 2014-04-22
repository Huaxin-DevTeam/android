package es.mihx.huaxin.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
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

import es.mihx.huaxin.R;
import es.mihx.huaxin.service.WebService;
import es.mihx.huaxin.utils.Constants;
import es.mihx.huaxin.utils.Utils;

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
	}
	
	private void initScreen(){
		txt_search = (EditText)findViewById(R.id.txt_search);
		spin_categories = (Spinner)findViewById(R.id.spin_categories);
		btn_search = (Button)findViewById(R.id.btn_search);
		btn_new_ad = (Button)findViewById(R.id.btn_new_ad);
		img_publi = (ImageView)findViewById(R.id.img_publi);
	}
	
	private void prepareControls(){
		//Ponemos categorias
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Constants.getApp().getCategories().getNames());
		spin_categories.setAdapter(adapter);
		
		//Ponemos la publi
		UrlImageViewHelper.setUrlDrawable(img_publi, Constants.getApp().getAd().getSrc());
		img_publi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.getApp().getAd().getLink()));
				startActivity(intent);
			}
		});
		
		//Botón buscar
		btn_search.setOnClickListener(searchClick);
		
		//Botón nuevo anuncio
		btn_new_ad.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Abrir form para anuncios
				openFormActivity();
			}
		});
	}
	
	private OnClickListener searchClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String query = txt_search.getText().toString().trim();
			
			//Checks...
			if(query.equalsIgnoreCase("")){
				Utils.toast("You need to write something to search!");
				return;
			}
			
			if(query.length() < 2){
				Utils.toast("Query has to be 2 characters at least");
				return;
			}
			
			//Todo ok, pedimos resultados y mostramos loader
			Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					
					if(msg.what == Constants.OK){
						openListActivity();
					}
				}
			};
			
			Messenger messenger = new Messenger(handler);
			
			Intent intent = new Intent(MainActivity.this,WebService.class);
			intent.putExtra(WebService.PARAM_OPERATION, WebService.OPERATION_SEARCH);
			intent.putExtra(WebService.PARAM_QUERY, query);
			intent.putExtra(WebService.PARAM_MESSENGER_SERVICE, messenger);
			startService(intent);
			
			showLoading(true);
		}
	};
	
	private void openFormActivity(){
//		Intent intent = new Intent(this,FormActivity.class);
//		startActivity(intent);
	}
	
	private void openListActivity(){
		showLoading(false);
		Intent intent = new Intent(this,ListActivity.class);
		startActivity(intent);
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
