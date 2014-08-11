package es.mihx.app.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import es.mihx.app.model.Item;
import es.mihx.app.service.WebService;
import es.mihx.app.utils.Constants;
import es.mihx.app.utils.ImgUtils;
import es.mihx.app.utils.Utils;
import es.mihx.app.R;

@SuppressLint("HandlerLeak")
public class FormActivity extends BaseActivity {

	private final int REQUEST_PHOTO = 9;
	protected final String TAG = "FormActivity";

	EditText txt_title, txt_description, txt_location, txt_price, txt_phone;
	TextView total_credits, total_duration, upload_photo, lbl_duration,
			lbl_premium, lbl_credits;
	Spinner spin_categories;
	SeekBar seek_duration;
	ToggleButton toggle_premium;
	Button btn_new_ad;
	ImageView preview;

	int duration;
	int credits;

	Uri uriForCamera;

	String base64img = "";

	Item item;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_form);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			long id = b.getLong(Constants.PARAM_ID);

			item = Constants.getApp().getItems().getItem(id);
		} 

		initScreen();

		prepareControls();

	}

	private void initScreen() {
		txt_title = (EditText) findViewById(R.id.txt_title);
		txt_description = (EditText) findViewById(R.id.txt_description);
		txt_location = (EditText) findViewById(R.id.txt_location);
		txt_price = (EditText) findViewById(R.id.txt_price);
		txt_phone = (EditText) findViewById(R.id.txt_phone);
		total_credits = (TextView) findViewById(R.id.total_credits);
		total_duration = (TextView) findViewById(R.id.total_duration);
		spin_categories = (Spinner) findViewById(R.id.spin_categories);
		seek_duration = (SeekBar) findViewById(R.id.seek_duration);
		toggle_premium = (ToggleButton) findViewById(R.id.toggle_premium);
		btn_new_ad = (Button) findViewById(R.id.btn_new_ad);
		upload_photo = (TextView) findViewById(R.id.upload_photo);
		preview = (ImageView) findViewById(R.id.preview);
		lbl_credits = (TextView) findViewById(R.id.lbl_credits);
		lbl_duration = (TextView) findViewById(R.id.lbl_duration);
		lbl_premium = (TextView) findViewById(R.id.lbl_premium);
	}

	private void prepareControls() {

		// Ponemos categorias
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, Constants
						.getApp().getCategories().getNames());
		spin_categories.setAdapter(adapter);

		if (item != null) {
			txt_title.setText(item.getTitle());
			txt_description.setText(item.getDescription());
			txt_location.setText(item.getLocation());
			txt_price.setText(String.valueOf(item.getPrice()));
			txt_phone.setText(item.getPhone());
			spin_categories.setSelection(Constants.getApp().getCategories()
					.getPos(item.getCategoryId()));
			spin_categories.setVisibility(View.GONE);
			total_duration.setVisibility(View.GONE);
			total_credits.setVisibility(View.GONE);
			seek_duration.setVisibility(View.GONE);
			toggle_premium.setVisibility(View.GONE);
			lbl_credits.setVisibility(View.GONE);
			lbl_duration.setVisibility(View.GONE);
			lbl_premium.setVisibility(View.GONE);
		}

		// SeekBar
		seek_duration.incrementProgressBy(1);
		seek_duration.setMax(4);
		seek_duration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				String str = "";
				switch (progress) {
				case 0:
					str = getResources().getString(R.string.one_week);// "1 semana";
					duration = 7;
					break;
				case 1:
					str = getResources().getString(R.string.two_weeks); // "2 semanas";
					duration = 14;
					break;
				case 2:
					str = getResources().getString(R.string.one_month); // 1 mes
					duration = 30;
					break;
				case 3:
					str = getResources().getString(R.string.two_months); // "2 meses";
					duration = 60;
					break;
				case 4:
					str = getResources().getString(R.string.one_year); // "1 año";
					duration = 365;
					break;
				}

				total_duration.setText(str);

				calculateTotalCredits();
			}
		});
		seek_duration.setProgress(0);

		toggle_premium
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						calculateTotalCredits();
					}
				});

		btn_new_ad.setOnClickListener(clickNewAd);

		upload_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showImageDialog();
			}
		});

	}

	private void calculateTotalCredits() {

		switch (seek_duration.getProgress()) {
		case 0:
			credits = 3;
			break;
		case 1:
			credits = 5;
			break;
		case 2:
			credits = 9;
			break;
		case 3:
			credits = 15;
			break;
		case 4:
			credits = 30;
			break;
		default:
			credits = 0;
		}

		if (toggle_premium.isChecked())
			credits = credits * 3;

		total_credits.setText(String.valueOf(credits));
	}

	OnClickListener clickNewAd = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!Utils.isConnected(FormActivity.this)) {
				Utils.makeInfo(FormActivity.this,
						getString(R.string.no_internet));
			} else {

				// Category selected
				int category_id = Constants.getApp().getCategories()
						.get(spin_categories.getSelectedItemPosition()).getId();

				String title = txt_title.getText().toString();
				String descr = txt_description.getText().toString();
				String phone = txt_phone.getText().toString();
				String pricetxt = txt_price.getText().toString();
				String location = txt_location.getText().toString();
				boolean premium = toggle_premium.isChecked();

				if (title != null && title.trim().compareToIgnoreCase("") != 0
						&& descr != null
						&& descr.trim().compareToIgnoreCase("") != 0
						&& phone != null
						&& phone.trim().compareToIgnoreCase("") != 0
						&& location != null
						&& location.trim().compareToIgnoreCase("") != 0
						&& pricetxt != null
						&& pricetxt.trim().compareToIgnoreCase("") != 0) {

					float price = Float.parseFloat(pricetxt);

					// Todo ok, pedimos resultados y mostramos loader
					Handler handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);

							showLoading(false);
							if (msg.what == Constants.OK) {
								Utils.makeText(FormActivity.this,
										(String) msg.obj);
								loadMyads();
								if (item == null)
									updateCredits(credits, false);
							} else if (msg.obj != null) {
								Utils.makeError(FormActivity.this,
										(String) msg.obj);
							}
						}
					};

					if (item == null) {

						Log.i(TAG, "Llamando al service...");

						Messenger messenger = new Messenger(handler);

						Intent intent = new Intent(FormActivity.this,
								WebService.class);
						intent.putExtra(WebService.PARAM_OPERATION,
								WebService.OPERATION_NEW_AD);
						intent.putExtra(WebService.PARAM_CATEGORY_ID,
								category_id);
						intent.putExtra(WebService.PARAM_TITLE, title);
						intent.putExtra(WebService.PARAM_DESCRIPTION, descr);
						intent.putExtra(WebService.PARAM_PHONE, phone);
						intent.putExtra(WebService.PARAM_PRICE, price);
						intent.putExtra(WebService.PARAM_LOCATION, location);
						intent.putExtra(WebService.PARAM_PREMIUM, premium);
						intent.putExtra(WebService.PARAM_DURATION, duration);
						intent.putExtra(WebService.PARAM_IMAGE, base64img);
						intent.putExtra(WebService.PARAM_MESSENGER_SERVICE,
								messenger);
						startService(intent);
					} else {
						// Edit mode

						Messenger messenger = new Messenger(handler);

						Intent intent = new Intent(FormActivity.this,
								WebService.class);
						intent.putExtra(WebService.PARAM_OPERATION,
								WebService.OPERATION_EDIT);
						intent.putExtra(WebService.PARAM_ITEM_ID, item.getId());
						intent.putExtra(WebService.PARAM_CATEGORY_ID,
								category_id);
						intent.putExtra(WebService.PARAM_TITLE, title);
						intent.putExtra(WebService.PARAM_DESCRIPTION, descr);
						intent.putExtra(WebService.PARAM_PHONE, phone);
						intent.putExtra(WebService.PARAM_PRICE, price);
						intent.putExtra(WebService.PARAM_LOCATION, location);
						intent.putExtra(WebService.PARAM_IMAGE, base64img);
						intent.putExtra(WebService.PARAM_MESSENGER_SERVICE,
								messenger);
						startService(intent);

					}

					showLoading(true);

				} else {
					Utils.makeError(FormActivity.this, getResources()
							.getString(R.string.fill_all_fields));
				}
			}
		}
	};

	private void showImageDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(FormActivity.this);
		builder.setTitle(R.string.choose_image_source);
		builder.setItems(new CharSequence[] { getString(R.string.gallery),
				getString(R.string.camera) },
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent;
						switch (which) {
						case 0: // This case picks an image from the gallery.
							intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_PICK);

							startActivityForResult(Intent.createChooser(intent,
									getString(R.string.select_picture)),
									REQUEST_PHOTO);

							break;
						case 1: // This case picks an image from the camera
								// using an app installed in your device.

							ContentValues values = new ContentValues();
							values.put(MediaStore.Images.Media.TITLE,
									"Image File name");
							uriForCamera = getContentResolver()
									.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
											values);
							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									uriForCamera);
							startActivityForResult(intent, REQUEST_PHOTO);

							break;
						}
					}
				});
		builder.show();

		// @see
		// http://stackoverflow.com/questions/20067508/get-real-path-from-uri-android-kitkat-new-storage-access-framework
		// http://chintankhetiya.wordpress.com/2013/12/14/picture-selection-from-camera-gallery/
		// http://stackoverflow.com/questions/4830711/how-to-convert-a-image-into-base64-string
		// http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample/

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_PHOTO) {
			if (resultCode == RESULT_OK) {
				Uri uri;
				if (data != null)
					uri = data.getData();
				else
					uri = uriForCamera;

				String path = getPath(this, uri);

				// Assign string path to File
				ImgUtils.Default_DIR = new File(path);

				// Create new dir MY_IMAGES_DIR if not created and copy image
				// into that dir and store that image path in valid_photo
				ImgUtils.Create_MY_IMAGES_DIR();

				// Copy your image
				ImgUtils.copyFile(ImgUtils.Default_DIR, ImgUtils.MY_IMG_DIR);

				// Get new image path and decode it
				Bitmap b = ImgUtils.decodeFile(ImgUtils.Paste_Target_Location);

				// use new copied path and use anywhere
				// String valid_photo =
				// ImgUtils.Paste_Target_Location.toString();
				Bitmap small = Bitmap.createScaledBitmap(b, 150, 150, true);

				Bitmap medium = Bitmap.createScaledBitmap(b, b.getWidth() / 2,
						b.getHeight() / 2, true);

				// Save it for ws
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				medium.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				byte[] byteimg = baos.toByteArray();
				base64img = Base64.encodeToString(byteimg, Base64.DEFAULT);

				// set your selected image in image view
				preview.setImageBitmap(small);

				Log.i("URI selected", path);
			}
		}
	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

}
