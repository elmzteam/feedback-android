package design.jsby.feedback;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
		try {
			final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(intent.getStringExtra(EXTRA_URL)).openConnection();
			// Add authKey to header
			urlConnection.setRequestProperty("Authorization", authKey); // TODO: change this field
			urlConnection.setUseCaches(false);

			switch (intent.getAction()) {
				case ACTION_LOAD_NEARBY:
					// TODO:
					urlConnection.connect();
					broadcastIntent.setAction(intent.getStringExtra(EXTRA_OUT));
//					broadcastIntent.putExtra(EXTRA_OUT, );
					break;
			}
		} catch (IOException e) {
			Log.e(TAG, "IO", e);
		}
		sendBroadcast(broadcastIntent);
	}
}
