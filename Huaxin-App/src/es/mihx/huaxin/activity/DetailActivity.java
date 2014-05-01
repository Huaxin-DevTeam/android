package es.mihx.huaxin.activity;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import es.mihx.huaxin.R;
import es.mihx.huaxin.model.Item;
import es.mihx.huaxin.utils.Constants;
import es.mihx.huaxin.utils.Utils;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends BaseActivity {
	
	private long id;
	private Item item;
	
	private ImageView img;
	private TextView txt_category, txt_description, txt_price, txt_location;
	private Button btn_call, btn_fav, btn_share;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detail);
		
		//Get params
		Bundle b = getIntent().getExtras();
		id = b.getLong(Constants.PARAM_ID);
		
		item = Constants.getApp().getItems().getItem(id);
		
		setTitle(item.getTitle());
		
		initScreen();

		prepareControls();
	}
	
	private void initScreen(){
		img = (ImageView)findViewById(R.id.img_item);
		txt_category = (TextView)findViewById(R.id.txt_item_category);
		txt_description = (TextView)findViewById(R.id.txt_item_description);
		txt_price = (TextView)findViewById(R.id.txt_item_price);
		txt_location = (TextView)findViewById(R.id.txt_item_location);
		btn_call = (Button)findViewById(R.id.btn_call);
		btn_fav = (Button)findViewById(R.id.btn_fav);
		btn_share = (Button)findViewById(R.id.btn_share);
	}
	
	private void prepareControls(){
		
		UrlImageViewHelper.setUrlDrawable(img, Constants.BASEURL + item.getImageUrl());
		txt_category.setText(Constants.getApp().getCategories().getName(item.getCategoryId()));
		txt_description.setText(item.getDescription());
		txt_price.setText(Utils.numberFormat(item.getPrice()) + "€");
		txt_location.setText(item.getLocation());
		
		btn_call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String uri = "tel:" + item.getPhone();
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse(uri));
				startActivity(intent);
			}
		});
		btn_fav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean fav = Utils.switchFavorite(item.getId());
				if(fav){
					//cambiar el fondo a relleno
				}else{
					//cambiar el fondo a blanco
				}
			}
		});
		btn_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.share(DetailActivity.this, item.getId(), item.getTitle());
			}
		});
	}
}
