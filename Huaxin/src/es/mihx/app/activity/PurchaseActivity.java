package es.mihx.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import es.mihx.app.adapter.CreditsAdapter;
import es.mihx.app.model.CreditOption;
import es.mihx.app.service.WebService;
import es.mihx.app.utils.Constants;
import es.mihx.app.utils.Utils;
import es.mihx.app.utils.vending.IabHelper;
import es.mihx.app.utils.vending.IabResult;
import es.mihx.app.utils.vending.Inventory;
import es.mihx.app.utils.vending.Purchase;
import es.mihx.app.R;

public class PurchaseActivity extends BaseActivity {

	protected static final String TAG = "PurchaseActivity";
	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;

	IabHelper mHelper;

	String payload = "", sku = "";

	Button btn_test;

	ListView items;

	CreditOption opt = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_purchase);

		if (!Utils.isConnected(PurchaseActivity.this)) {
			Utils.makeInfo(PurchaseActivity.this,
					getString(R.string.no_internet));
		} else {

			mHelper = new IabHelper(this, Constants.APIKEY);

			Log.d(TAG, "Starting setup.");
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
				@Override
				public void onIabSetupFinished(IabResult result) {
					if (!result.isSuccess()) {
						// Oh noes, there was a problem.
						Log.d(TAG, "Problem setting up In-app Billing: "
								+ result);
					}
					// IAB is fully set up. Now, let's get an inventory of stuff
					// we
					// own.
					Log.d(TAG, "Setup successful. Querying inventory.");
					List<String> skus = new ArrayList<String>();
					skus.add("android.test.purchased");
					mHelper.queryInventoryAsync(true, skus,
							mGotInventoryListener);
				}
			});

			mHelper.enableDebugLogging(true);

			initScreen();

			prepareControls();
		}
	}

	private void initScreen() {
		items = (ListView) findViewById(R.id.list_credits);
	}

	private void prepareControls() {

		CreditsAdapter adapter = new CreditsAdapter(this, Constants.getApp()
				.getCreditOptions(), items);
		items.setAdapter(adapter);

		items.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (!Utils.isConnected(PurchaseActivity.this)) {
					Utils.makeInfo(PurchaseActivity.this,
							getString(R.string.no_internet));
				} else {

					opt = (CreditOption) items.getItemAtPosition(position);
					Log.i(TAG, "Click on pos: " + opt.getSku());

					showLoading(true); // TODO generate payload on server side

					Log.d(TAG, "Launching purchase flow for " + opt.getSku());

					Handler handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							if (msg != null) {
								if (msg.what == Constants.OK) {
									payload = (String) msg.obj;
									sku = opt.getSku();
									Log.i(TAG, "payload received: " + payload);
									mHelper.launchPurchaseFlow(
											PurchaseActivity.this, sku,
											IabHelper.ITEM_TYPE_INAPP,
											RC_REQUEST,
											mPurchaseFinishedListener, payload);
								} else {
									showLoading(false);
									Utils.makeError(PurchaseActivity.this,
											(String) msg.obj);
								}
							} else {
								showLoading(false);
								Utils.makeError(
										PurchaseActivity.this,
										getResources().getString(
												R.string.comm_error));
							}
						}
					};

					Messenger messenger = new Messenger(handler);

					Intent intent = new Intent(PurchaseActivity.this,
							WebService.class);
					intent.putExtra(WebService.PARAM_OPERATION,
							WebService.OPERATION_GENERATE_TOKEN);
					intent.putExtra(WebService.PARAM_CREDITS_ID, opt.getId());
					intent.putExtra(WebService.PARAM_MESSENGER_SERVICE,
							messenger);
					startService(intent);
				}

			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
				+ data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			Log.d(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Log.w("SKU?" ,
			// inventory.getSkuDetails(Constants.SKU1).getTitle());

			// Is it a failure?
			if (result.isFailure()) {
				Log.e(TAG, "Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			if (inventory.hasPurchase("android.test.purchased")) {
				mHelper.consumeAsync(
						inventory.getPurchase("android.test.purchased"), null);
			}

			if (inventory.hasPurchase(Constants.SKU1)) {
				mHelper.consumeAsync(inventory.getPurchase(Constants.SKU1),
						null);
			}

			if (inventory.hasPurchase(Constants.SKU2)) {
				mHelper.consumeAsync(inventory.getPurchase(Constants.SKU2),
						null);
			}

			// Purchase purchase = inventory.getPurchase(Constants.SKU1);
			// mHelper.consumeAsync(purchase, mConsumeFinishedListener);

			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}
	};

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d(TAG, "Purchase finished: " + result + ", purchase: "
					+ purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				Log.e(TAG, "Error purchasing: " + result);
				showLoading(false);
				Utils.makeError(PurchaseActivity.this,
						getString(R.string.error_compra));
				return;
			}

			verifyDeveloperPayload(purchase);

		}
	};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(TAG, "Consumption finished. Purchase: " + purchase
					+ ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			// We know this is the "gas" sku because it's the only one we
			// consume,
			// so we don't check which sku was consumed. If you have more than
			// one
			// sku, you probably should check...
			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in
				// our
				// game world's logic, which in our case means filling the gas
				// tank a bit
				Log.d(TAG, "Consumption successful. Provisioning.");
				showLoading(false);
				Utils.makeText(PurchaseActivity.this,
						"Thank you for your purchase!");

				updateCredits(opt.getNum_credits(), true);
			} else {
				Log.e(TAG, "Error while consuming: " + result);
			}
			showLoading(false);
			Log.d(TAG, "End consumption flow.");
		}
	};

	protected boolean verifyDeveloperPayload(final Purchase purchase) {
		// TODO Auto-generated method stub
		String payload = purchase.getDeveloperPayload();
		Log.w(TAG, "Payload: " + payload);

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					if (msg.what == Constants.OK) {
						afterVerify(purchase, true);
						return;
					}
				} else {
					Utils.makeError(PurchaseActivity.this, getResources()
							.getString(R.string.comm_error));
				}

				afterVerify(purchase, false);
			};
		};

		Messenger messenger = new Messenger(handler);

		Intent intent = new Intent(PurchaseActivity.this, WebService.class);
		intent.putExtra(WebService.PARAM_OPERATION,
				WebService.OPERATION_VALIDATE_TOKEN);
		intent.putExtra(WebService.PARAM_TOKEN, purchase.getDeveloperPayload());
		intent.putExtra(WebService.PARAM_PURCHASE_TOKEN, purchase.getToken());
		intent.putExtra(WebService.PARAM_MESSENGER_SERVICE, messenger);
		startService(intent);

		return true;
	}

	private void afterVerify(Purchase purchase, boolean ok) {
		if (!ok) {
			Log.e(TAG, "Error purchasing. Authenticity verification failed.");
			showLoading(false);
			Utils.makeError(PurchaseActivity.this,
					getString(R.string.error_validation));
			return;
		}

		Log.d(TAG, "Purchase successful.");

		if (purchase.getSku().equals(sku)) {
			// bought 1/4 tank of gas. So consume it.
			Log.d(TAG, "Purchase OK. Starting consumption.");
			mHelper.consumeAsync(purchase, mConsumeFinishedListener);
		}

	}
}
