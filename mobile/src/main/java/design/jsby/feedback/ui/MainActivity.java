package design.jsby.feedback.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;

import design.jsby.feedback.R;
import design.jsby.feedback.WebRequestService;
import design.jsby.feedback.model.Restaurant;
import design.jsby.feedback.util.DrawerActivity;
import design.jsby.feedback.util.Utils;

public class MainActivity extends DrawerActivity implements NearbyFragment.OnFragmentInteractionListener {
	private static final String TAG = Utils.makeLogTag(MainActivity.class);
	private NearbyFragment mNearbyFragment;
	private WebRequestReceiver mRequestReceiver;
	private Display mActiveDisplay;
	private enum Display {
		NEARBY, LOGIN
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Not implemented yet!", Snackbar.LENGTH_LONG)
						.setAction("Sorry", null).show();
			}
		});

		final IntentFilter filter = new IntentFilter();
		filter.addAction(WebRequestReceiver.ACTION_UPDATE);
		filter.addAction(WebRequestReceiver.ACTION_ADD);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		mRequestReceiver = new WebRequestReceiver();
		registerReceiver(mRequestReceiver, filter);
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
	public void onDestroy() {
		unregisterReceiver(mRequestReceiver);
		super.onDestroy();
	}

	public void refresh() {
		// TODO: stub
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
			switch (intent.getAction()) {
				case ACTION_UPDATE:
					mNearbyFragment.update((Restaurant[]) intent.getParcelableArrayExtra(WebRequestService.EXTRA_OUT));
					break;
				case ACTION_ADD:
					mNearbyFragment.addAll((Restaurant[]) intent.getParcelableArrayExtra(WebRequestService.EXTRA_OUT));
					break;
			}
		}
	}
}
