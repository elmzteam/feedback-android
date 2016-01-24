package design.jsby.feedback.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import design.jsby.feedback.R;
import design.jsby.feedback.WebRequestService;
import design.jsby.feedback.model.Restaurant;
import design.jsby.feedback.util.API;
import design.jsby.feedback.util.DrawerActivity;
import design.jsby.feedback.util.FallbackLocationTracker;
import design.jsby.feedback.util.Utils;

public class MainActivity extends DrawerActivity implements NearbyFragment.OnFragmentInteractionListener {
	private static final String TAG = Utils.makeLogTag(MainActivity.class);
	private NearbyFragment mNearbyFragment;
	private WebRequestReceiver mRequestReceiver;
	private FallbackLocationTracker mLocationTracker;
	private Display mActiveDisplay;
	private enum Display {
		NEARBY, LOGIN
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		display(Display.NEARBY);
		if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{
					android.Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION
			}, 0);
			mLocationTracker = null;
		} else {
			mLocationTracker = new FallbackLocationTracker(this);
			if (mLocationTracker.hasLocation()) {
				refresh();
			}
		}

		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Not implemented yet!", Snackbar.LENGTH_LONG)
						.setAction("Sorry", null).show();
			}
		});

		// Setup web request intent filter
		final IntentFilter filter = new IntentFilter();
		filter.addAction(WebRequestReceiver.ACTION_UPDATE);
		filter.addAction(WebRequestReceiver.ACTION_ADD);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		mRequestReceiver = new WebRequestReceiver();
		registerReceiver(mRequestReceiver, filter);
		// Handle back presses
		getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			public void onBackStackChanged() {
				final FragmentManager manager = getSupportFragmentManager();
				final int count = manager.getBackStackEntryCount();
				if (count == 0) {
					setDrawerIndicatorEnabled(true);
					switch (mActiveDisplay) {
						case NEARBY:
							break;
					}
					return;
				}
				setDrawerIndicatorEnabled(false);
				final Fragment fragment = manager.getFragments().get(count - 1);
				switch (mActiveDisplay) {
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		switch (item.getItemId()) {
			case R.id.nav_nearby:
			case R.id.nav_settings:
				break;
		}

		return super.onNavigationItemSelected(item);
	}

	@Override
	public void onPause() {
		if (mLocationTracker != null) {
			mLocationTracker.stop();
		}
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mLocationTracker != null) {
			mLocationTracker.start();
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mRequestReceiver);
		super.onDestroy();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case 0: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission was granted, yay!
					mLocationTracker = new FallbackLocationTracker(this);
					mLocationTracker.start();
					refresh();

				} else {
					// permission denied, boo!
					// die somehow
				}
				break;
			}
		}
	}

	private void display(Display display) {
		mActiveDisplay = display;
		invalidateOptionsMenu();
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
		switch (display) {
			case NEARBY:
				if (mNearbyFragment == null) {
					mNearbyFragment = new NearbyFragment();
				}
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, mNearbyFragment)
						.commit();
				((AppBarLayout) findViewById(R.id.appbar)).setExpanded(true);
				setTitle(getResources().getString(R.string.title_nearby));
				params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
				toolbar.setLayoutParams(params);
				break;
			case LOGIN:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, mNearbyFragment)
						.addToBackStack(null)
						.commit();
				params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
						AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
				toolbar.setLayoutParams(params);
				break;
		}
	}

	public void refresh() {
		if (mActiveDisplay == Display.NEARBY) {
			if (!mLocationTracker.hasLocation()) {
				mNearbyFragment.setRefreshing(false); // TODO: make this recur
				Log.d(TAG, "No GPS lock");
				Snackbar.make(findViewById(R.id.container), "No GPS lock", Snackbar.LENGTH_SHORT).show();
				return;
			}
			final Intent webRequest = new Intent(WebRequestService.ACTION_LOAD_NEARBY, null, this, WebRequestService.class);
			webRequest.putExtra(WebRequestService.EXTRA_OUT, WebRequestReceiver.ACTION_UPDATE);
			webRequest.putExtra(WebRequestService.EXTRA_URL, API.getNearby(mLocationTracker.getLocation(), 0));
			startService(webRequest);
		}
	}

	public void loadMore(int startIndex) {
		if (mActiveDisplay == Display.NEARBY) {
			final Intent webRequest = new Intent(WebRequestService.ACTION_LOAD_NEARBY, null, this, WebRequestService.class);
			webRequest.putExtra(WebRequestService.EXTRA_OUT, WebRequestReceiver.ACTION_ADD);
			webRequest.putExtra(WebRequestService.EXTRA_URL, API.getNearby(mLocationTracker.getLocation(), startIndex));
			startService(webRequest);
		}
	}

	public void select(Restaurant restaurant) {
		// TODO: stub
	}

	public class WebRequestReceiver extends BroadcastReceiver {
		public static final String ACTION_UPDATE =
				"design.jsby.feedback.action.UPDATE";
		public static final String ACTION_ADD =
				"design.jsby.feedback.action.ADD";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra("error", false)) {
				Snackbar.make(findViewById(R.id.container), "Cannot retrieve from server", Snackbar.LENGTH_LONG).show();
				switch (intent.getAction()) {
					case ACTION_UPDATE:
						mNearbyFragment.setRefreshing(false);
						return;
					default:
						return;
				}
			}
			switch (intent.getAction()) {
				case ACTION_UPDATE:
					mNearbyFragment.update(Utils.parcelableArrayToTypedArray(
							intent.getParcelableArrayExtra(WebRequestService.EXTRA_OUT), Restaurant[].class));
					break;
				case ACTION_ADD:
					mNearbyFragment.addAll(Utils.parcelableArrayToTypedArray(
							intent.getParcelableArrayExtra(WebRequestService.EXTRA_OUT), Restaurant[].class));
					break;
			}
		}
	}
}
