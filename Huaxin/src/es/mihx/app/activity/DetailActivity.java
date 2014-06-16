package es.mihx.app.activity;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import es.mihx.app.model.Item;
import es.mihx.app.service.WebService;
import es.mihx.app.utils.Constants;
import es.mihx.app.utils.Utils;
import es.mihx.app.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class DetailActivity extends BaseActivity {

	private long id;
	private Item item;

	private ImageView img;
	private TextView txt_category, txt_description, txt_price, txt_location, txt_phone;
	private Button btn_call, btn_fav, btn_share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_detail);

		// Get params
		Bundle b = getIntent().getExtras();
		id = b.getLong(Constants.PARAM_ID);

		item = Constants.getApp().getItems().getItem(id);

		setTitle(item.getTitle());

		initScreen();

		prepareControls();
	}

	private void initScreen() {
		img = (ImageView) findViewById(R.id.img_item);
		txt_category = (TextView) findViewById(R.id.txt_item_category);
		txt_description = (TextView) findViewById(R.id.txt_item_description);
		txt_price = (TextView) findViewById(R.id.txt_item_price);
		txt_location = (TextView) findViewById(R.id.txt_item_location);
		txt_phone = (TextView) findViewById(R.id.txt_phone);
		btn_call = (Button) findViewById(R.id.btn_call);
		btn_fav = (Button) findViewById(R.id.btn_fav);
		btn_share = (Button) findViewById(R.id.btn_share);
	}

	private void prepareControls() {

		UrlImageViewHelper.setUrlDrawable(img,
				Constants.BASEURL + item.getImageUrl());
		txt_category.setText(Constants.getApp().getCategories()
				.getName(item.getCategoryId()));
		txt_description.setText(item.getDescription());
		txt_price.setText(Utils.numberFormat(item.getPrice()) + "€");
		txt_location.setText(item.getLocation());
		txt_phone.setText(item.getPhone());

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
				if (fav) {
					// cambiar el fondo a relleno
					btn_fav.setBackgroundResource(R.drawable.ic_star_on);
				} else {
					// cambiar el fondo a blanco
					btn_fav.setBackgroundResource(R.drawable.ic_star_off);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (Constants.getApp().getUser() != null
				&& item.getAuthorId() == Constants.getApp().getUser().getId()) {
			Log.i("tattt", "item.getAuthorId(): " + item.getAuthorId());
			Log.i("tattt", "app.getUserId(): "
					+ Constants.getApp().getUser().getId());
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.topmenu, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_edit:
			editItem();
			return true;
		case R.id.action_delete:
			deleteItem();
			return true;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);

	}

	private void editItem() {
		Intent intent = new Intent(this, FormActivity.class);
		intent.putExtra(Constants.PARAM_ID, id);
		startActivity(intent);
		finish();
	}

	private void deleteItem() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Please, confirm deletion.");
		builder.setMessage("Are you sure you want to delete? This cannot be undone.");
		builder.setPositiveButton("Delete",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						// LLamada al ws...

						// Todo ok, pedimos resultados y mostramos loader
						Handler handler = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								super.handleMessage(msg);

								showLoading(false);
								if (msg.what == Constants.OK) {
									Utils.makeText(DetailActivity.this,
											(String) msg.obj);
									
									if(Utils.isFavorite(item.getId()))
										Utils.switchFavorite(item.getId());
									
									// vuelta al origen
									loadMyads();
								} else if (msg.obj != null) {
									Utils.makeError(DetailActivity.this,
											(String) msg.obj);
								}
							}
						};

						Messenger messenger = new Messenger(handler);

						Intent intent = new Intent(DetailActivity.this,
								WebService.class);
						intent.putExtra(WebService.PARAM_OPERATION,
								WebService.OPERATION_DELETE);
						intent.putExtra(WebService.PARAM_ITEM_ID, item.getId());
						intent.putExtra(WebService.PARAM_MESSENGER_SERVICE,
								messenger);
						startService(intent);
						
						showLoading(true);

					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.show();
	}
}
