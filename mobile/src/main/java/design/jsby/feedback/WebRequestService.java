package design.jsby.feedback;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import design.jsby.feedback.parser.RestaurantHandler;
import design.jsby.feedback.util.Utils;

public class WebRequestService extends IntentService {
	public static final String TAG = Utils.makeLogTag(WebRequestService.class);
	public static final String ACTION_LOAD_NEARBY =
			"design.jsby.feedback.action.LOAD_NEARBY";
	public static final String EXTRA_RESTAURANTS =
			"design.jsby.feedback.extra.RESTAURANTS";
	public static final String EXTRA_URL =
			"design.jsby.feedback.extra.URL";
	public static final String EXTRA_OUT = "output";

	public WebRequestService() {
		super("WebRequestService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final Intent broadcastIntent = new Intent();
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		// Get credentials
		final SharedPreferences preferences = getSharedPreferences(Utils.PREFS_NAME, MODE_PRIVATE);
		final String authKey = Utils.getAuthKey(preferences);
		// Get input URL
		final URL url = (URL) intent.getSerializableExtra(EXTRA_URL);
		HttpURLConnection urlConnection = null;

		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			// Add authKey to header
			// TODO: use login
			urlConnection.setRequestProperty("user", "test");
			urlConnection.setRequestProperty("session", "0d3c1cff2889635bc9f4b731ac32e9458598dc99d459270c4d3f157cdf78df3b"); // TODO: change this field
			urlConnection.setUseCaches(false);

			switch (intent.getAction()) {
				case ACTION_LOAD_NEARBY:
					urlConnection.connect();
					broadcastIntent.setAction(intent.getStringExtra(EXTRA_OUT));
					broadcastIntent.putExtra(EXTRA_OUT, RestaurantHandler.parseAll(urlConnection.getInputStream()));
					break;
			}
		} catch (Exception e) {
			Log.e(TAG, "IO", e);
			broadcastIntent.putExtra("error", true);
			// Log response code
			try {
				if (urlConnection != null) {
					Log.e(TAG, urlConnection.getResponseCode() + ": " + urlConnection.getResponseMessage());
				} else {
					Log.e(TAG, "Null url connection");
				}
			} catch (IOException e1) {
				Log.e(TAG, "Cannot read response code", e1);
			}
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (broadcastIntent.getAction() == null) {
				broadcastIntent.setAction(intent.getStringExtra(EXTRA_OUT));
			}
		}
		sendBroadcast(broadcastIntent);
	}
}
