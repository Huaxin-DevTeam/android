package es.mihx.huaxin.activity;

import es.mihx.huaxin.R;
import es.mihx.huaxin.model.Item;
import es.mihx.huaxin.service.WebService;
import es.mihx.huaxin.utils.Constants;
import es.mihx.huaxin.utils.Utils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

@SuppressLint("HandlerLeak")
public class FormActivity extends BaseActivity {

	EditText txt_title, txt_description, txt_location, txt_price, txt_phone;
	TextView total_credits, total_duration;
	Spinner spin_categories;
	SeekBar seek_duration;
	ToggleButton toggle_premium;
	Button btn_new_ad;

	int duration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_form);

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
	}

	private void prepareControls() {
		// Ponemos categorias
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, Constants
						.getApp().getCategories().getNames());
		spin_categories.setAdapter(adapter);

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

	}

	private void calculateTotalCredits() {
		int credits;
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
			credits = credits * 2;

		total_credits.setText(String.valueOf(credits));
	}

	OnClickListener clickNewAd = new OnClickListener() {

		@Override
		public void onClick(View v) {
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

						if (msg.what == Constants.OK) {
							showLoading(false);
							Utils.makeText(FormActivity.this, (String) msg.obj);
							loadMyads();
						} else if (msg.obj != null) {
							showLoading(false);
							Utils.makeError(FormActivity.this, (String) msg.obj);
						}
					}
				};

				Messenger messenger = new Messenger(handler);

				Intent intent = new Intent(FormActivity.this, WebService.class);
				intent.putExtra(WebService.PARAM_OPERATION,
						WebService.OPERATION_NEW_AD);
				intent.putExtra(WebService.PARAM_CATEGORY_ID, category_id);
				intent.putExtra(WebService.PARAM_TITLE, title);
				intent.putExtra(WebService.PARAM_DESCRIPTION, descr);
				intent.putExtra(WebService.PARAM_PHONE, phone);
				intent.putExtra(WebService.PARAM_PRICE, price);
				intent.putExtra(WebService.PARAM_LOCATION, location);
				intent.putExtra(WebService.PARAM_PREMIUM, premium);
				intent.putExtra(WebService.PARAM_DURATION, duration);
				intent.putExtra(WebService.PARAM_MESSENGER_SERVICE, messenger);
				startService(intent);

				showLoading(true);

			} else {
				Utils.makeError(FormActivity.this,
						getResources().getString(R.string.fill_all_fields));
			}

		}
	};
}
