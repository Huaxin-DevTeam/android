package es.mihx.huaxin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import es.mihx.huaxin.R;
import es.mihx.huaxin.adapter.CreditsAdapter;
import es.mihx.huaxin.utils.Constants;
import es.mihx.huaxin.utils.vending.IabHelper;
import es.mihx.huaxin.utils.vending.IabResult;
import es.mihx.huaxin.utils.vending.Inventory;
import es.mihx.huaxin.utils.vending.Purchase;

public class PurchaseActivity extends BaseActivity {

	protected static final String TAG = "PurchaseActivity";
	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;

	IabHelper mHelper;

	String payload = "";

	Button btn_test;

	ListView items;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_purchase);

		mHelper = new IabHelper(this, Constants.APIKEY);

		Log.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Log.d(TAG, "Problem setting up In-app Billing: " + result);
				}
				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

		mHelper.enableDebugLogging(true);

		initScreen();

		prepareControls();
	}

	private void initScreen() {
		items = (ListView) findViewById(R.id.list_credits);
	}

	private void prepareControls() {
		
		CreditsAdapter adapter = new CreditsAdapter(this, Constants.getApp().getCreditOptions(), items);
		items.setAdapter(adapter);
		
		/*btn_test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showLoading(true);
				// TODO generate payload on server side

				Log.d(TAG,
						"Launching purchase flow for infinite gas subscription.");
				mHelper.launchPurchaseFlow(PurchaseActivity.this,
						Constants.SKU1, IabHelper.ITEM_TYPE_INAPP, RC_REQUEST,
						mPurchaseFinishedListener, payload);
			}
		});*/
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

			// Is it a failure?
			if (result.isFailure()) {
				Log.e(TAG, "Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

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
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				Log.e(TAG,
						"Error purchasing. Authenticity verification failed.");
				showLoading(false);
				return;
			}

			Log.d(TAG, "Purchase successful.");

			if (purchase.getSku().equals(Constants.SKU1)) {
				// bought 1/4 tank of gas. So consume it.
				Log.d(TAG, "Purchase is gas. Starting gas consumption.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
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

				Log.w(TAG, "You filled 1/4 tank.");
			} else {
				Log.e(TAG, "Error while consuming: " + result);
			}
			showLoading(false);
			Log.d(TAG, "End consumption flow.");
		}
	};

	protected boolean verifyDeveloperPayload(Purchase purchase) {
		// TODO Auto-generated method stub
		String payload = purchase.getDeveloperPayload();
		Log.w(TAG, "Payload: " + payload);
		return true;
	}

}
