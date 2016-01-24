package design.jsby.feedback.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cocosw.bottomsheet.BottomSheet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import design.jsby.feedback.R;
import design.jsby.feedback.WebRequestService;
import design.jsby.feedback.model.MenuEntry;
import design.jsby.feedback.model.Restaurant;
import design.jsby.feedback.util.API;
import design.jsby.feedback.util.DrawerActivity;
import design.jsby.feedback.util.FallbackLocationTracker;
import design.jsby.feedback.util.Utils;

public class MainActivity extends DrawerActivity implements NearbyFragment.OnFragmentInteractionListener,
		RestaurantMenuFragment.OnFragmentInteractionListener,
		SubmitFragment.OnFragmentInteractionListener {
	private static final String TAG = Utils.makeLogTag(MainActivity.class);
	private WebRequestReceiver mRequestReceiver;
	private FallbackLocationTracker mLocationTracker;
	private Display mActiveDisplay;
	private FloatingActionButton mFab;
	private int mPrevCount;

	// Fragments
	private NearbyFragment mNearbyFragment;
	private RestaurantMenuFragment mRestaurantMenuFragment;
	private SubmitFragment mSubmitFragment;

	private enum Display {
		NEARBY, LOGIN, MENU, SUBMIT
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFab = (FloatingActionButton) findViewById(R.id.fab);
		mFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mActiveDisplay == Display.MENU) {
					switchTo(Display.SUBMIT);
				} else {
					Snackbar.make(view, "Explore the best restaurants around!", Snackbar.LENGTH_LONG)
							.setAction("Sorry", null).show();
				}
			}
		});

		switchTo(Display.NEARBY);
		if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{
					android.Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION
			}, 0);
			mLocationTracker = null;
		} else {
			mLocationTracker = new FallbackLocationTracker(this);
			if (mLocationTracker.hasPossiblyStaleLocation()) {
				refresh();
			}
		}

		// Setup web request intent filter
		final IntentFilter filter = new IntentFilter();
		filter.addAction(WebRequestReceiver.ACTION_UPDATE_NEARBY);
		filter.addAction(WebRequestReceiver.ACTION_ADD_NEARBY);
		filter.addAction(WebRequestReceiver.ACTION_UPDATE_MENU);
		filter.addAction(WebRequestReceiver.ACTION_NAV_BACK);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		mRequestReceiver = new WebRequestReceiver();
		registerReceiver(mRequestReceiver, filter);
		// Handle back presses
		getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			public void onBackStackChanged() {
				final FragmentManager manager = getSupportFragmentManager();
				final int count = manager.getBackStackEntryCount();
				// Popping
				if (count < mPrevCount) {
					switch (mActiveDisplay) {
						case MENU:
							setDrawerIndicatorEnabled(true);
							mActiveDisplay = Display.NEARBY;
							break;
						case SUBMIT:
							mActiveDisplay = Display.MENU;
							break;
					}
					display(mActiveDisplay);
					mPrevCount--;
					return;
				}
				setDrawerIndicatorEnabled(false);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		MenuItem submit = menu.findItem(R.id.action_submit);
		switch(mActiveDisplay) {
			case SUBMIT:
				submit.setEnabled(true).setVisible(true);
				break;
			case MENU:
			case NEARBY:
				submit.setEnabled(false).setVisible(false);
				break;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.action_submit:
				// TODO: submit?
				submit();
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
				if (mActiveDisplay != Display.NEARBY) {
					switchTo(Display.NEARBY);
				}
				break;
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
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
	                                       @NonNull int[] grantResults) {
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
//			mImageView.setImageBitmap(imageBitmap);
		}
	}

	private void switchTo(Display display) {
		switch (display) {
			case NEARBY:
				if (mNearbyFragment == null) {
					mNearbyFragment = new NearbyFragment();
				}
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, mNearbyFragment)
						.commit();
				mPrevCount = 0;
				break;
			case MENU:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, mRestaurantMenuFragment)
						.addToBackStack(null)
						.commit();
				mPrevCount = 1;
				break;
			case SUBMIT:
				if (mSubmitFragment == null) {
					mSubmitFragment = new SubmitFragment();
				}
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, mSubmitFragment)
						.addToBackStack(null)
						.commit();
				mPrevCount = 2;
				break;
		}
		display(display);
	}

	private void display(Display display) {
		invalidateOptionsMenu();
		mActiveDisplay = display;
		final CollapsingToolbarLayout toolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
		final CoordinatorLayout.LayoutParams fabParams = (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
		switch (display) {
			case NEARBY:
				toolbar.setTitle(getResources().getString(R.string.title_nearby));
				((AppBarLayout) findViewById(R.id.appbar)).setExpanded(false);
				fabParams.setAnchorId(View.NO_ID);
				fabParams.gravity = Gravity.BOTTOM | Gravity.END;
				mFab.setLayoutParams(fabParams);
				mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_explore));
				mFab.setVisibility(View.VISIBLE);
				break;
			case LOGIN:
				break;
			case MENU:
				toolbar.setTitle(mRestaurantMenuFragment.getRestaurant().getName());
				((AppBarLayout) findViewById(R.id.appbar)).setExpanded(true);
				fabParams.setAnchorId(R.id.toolbar_layout);
				fabParams.gravity = Gravity.NO_GRAVITY;
				mFab.setLayoutParams(fabParams);
				mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add));
				mFab.setVisibility(View.VISIBLE);
				break;
			case SUBMIT:
				toolbar.setTitle(getResources().getString(R.string.title_submit));
				((AppBarLayout) findViewById(R.id.appbar)).setExpanded(false);
				mFab.setVisibility(View.GONE);
				break;
		}
	}

	public void submit() {
		final Intent webRequest = new Intent(WebRequestService.ACTION_POST_MENU, null, this, WebRequestService.class);
		webRequest.putExtra(WebRequestService.EXTRA_OUT, WebRequestReceiver.ACTION_NAV_BACK);
		webRequest.putExtra(WebRequestService.EXTRA_URL, API.postMenu());
		webRequest.putExtra(WebRequestService.EXTRA_RESTAURANT_ID, mRestaurantMenuFragment.getRestaurant().getId());
		webRequest.putExtra(WebRequestService.EXTRA_ITEM_NAME, mSubmitFragment.getCaption());
		startService(webRequest);
	}

	public void refresh() {
		if (mActiveDisplay == Display.NEARBY) {
			if (!mLocationTracker.hasPossiblyStaleLocation()) {
				mNearbyFragment.setRefreshing(false); // TODO: make this recur
				Log.d(TAG, "No GPS lock");
				Snackbar.make(findViewById(R.id.container), "Waiting for GPS lock...", Snackbar.LENGTH_SHORT).show();
				return;
			}
			final Intent webRequest = new Intent(WebRequestService.ACTION_LOAD_NEARBY, null, this, WebRequestService.class);
			webRequest.putExtra(WebRequestService.EXTRA_OUT, WebRequestReceiver.ACTION_UPDATE_NEARBY);
			webRequest.putExtra(WebRequestService.EXTRA_URL, API.getNearby(mLocationTracker.getPossiblyStaleLocation(), 0));
			startService(webRequest);
		}
	}

	public void loadMore(int startIndex) {
		if (mActiveDisplay == Display.NEARBY) {
			final Intent webRequest = new Intent(WebRequestService.ACTION_LOAD_NEARBY, null, this, WebRequestService.class);
			webRequest.putExtra(WebRequestService.EXTRA_OUT,
					WebRequestReceiver.ACTION_ADD_NEARBY);
			webRequest.putExtra(WebRequestService.EXTRA_URL,
					API.getNearby(mLocationTracker.getPossiblyStaleLocation(), startIndex));
			startService(webRequest);
		}
	}

	public void select(Restaurant restaurant) {
		if (mActiveDisplay == Display.NEARBY) {
			if (mRestaurantMenuFragment == null) {
				mRestaurantMenuFragment = RestaurantMenuFragment.newInstance(restaurant);
			} else {
				mRestaurantMenuFragment.setArgRestaurant(restaurant);
			}
			switchTo(Display.MENU);
			// Send request
			final Intent webRequest = new Intent(WebRequestService.ACTION_LOAD_MENU, null, this, WebRequestService.class);
			webRequest.putExtra(WebRequestService.EXTRA_OUT, WebRequestReceiver.ACTION_UPDATE_MENU);
			webRequest.putExtra(WebRequestService.EXTRA_URL, API.getMenu(restaurant.getId()));
			startService(webRequest);
		}
	}

	public void select(final MenuEntry entry, final int position) {
		new BottomSheet.Builder(this)
				.title("Rate this meal")
				.sheet(R.menu.sheet_rating)
				.grid()
				.listener(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final MenuEntry.Rating rating;
						switch (which) {
							case R.id.vote_down:
								rating = MenuEntry.Rating.DOWN;
								break;
							case R.id.vote_neutral:
								rating = MenuEntry.Rating.OK;
								break;
							case R.id.vote_up:
								rating = MenuEntry.Rating.UP;
								break;
							default:
								return;
						}
						if (entry.getRating() != rating) {
							entry.setRating(rating);
							mRestaurantMenuFragment.update(position);
							sendRating(entry, rating);
						}
					}
				}).show();
	}

	public void select(final MenuEntry entry) {
		BottomSheet sheet = new BottomSheet.Builder(this)
				.title(entry.getName())
				.build();
		Menu menu = sheet.getMenu();
		int i = 0;
		for (String s : entry.getDescriptionArray()) {
			menu.add(Menu.NONE, i++, Menu.NONE, s);
		}
		sheet.show();
	}

	private void sendRating(MenuEntry entry, MenuEntry.Rating rating) {
		// Send request
		final Intent webRequest = new Intent(WebRequestService.ACTION_PUT_RATING, null, this, WebRequestService.class);
		webRequest.putExtra(WebRequestService.EXTRA_URL, API.putRating());
		webRequest.putExtra(WebRequestService.EXTRA_RATING, (rating.ordinal() - 1f) / 2);
		webRequest.putExtra(WebRequestService.EXTRA_ENTRY_ID, entry.getId());
		webRequest.putExtra(WebRequestService.EXTRA_RESTAURANT_ID, mRestaurantMenuFragment.getRestaurant().getId());
		startService(webRequest);
	}

	private void dispatchCameraIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, 1);
			}
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
//		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}

	public class WebRequestReceiver extends BroadcastReceiver {
		public static final String ACTION_UPDATE_NEARBY =
				"design.jsby.feedback.action.UPDATE_NEARBY";
		public static final String ACTION_ADD_NEARBY =
				"design.jsby.feedback.action.ADD_NEARBY";
		public static final String ACTION_UPDATE_MENU =
				"design.jsby.feedback.action.UPDATE_MENU";
		public static final String ACTION_NAV_BACK =
				"design.jsby.feedback.action.NAV_BACK";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra("error", false)) {
				Snackbar.make(findViewById(R.id.container), "Cannot retrieve from server", Snackbar.LENGTH_LONG).show();
				switch (intent.getAction()) {
					case ACTION_UPDATE_NEARBY:
					case ACTION_ADD_NEARBY:
						mNearbyFragment.setRefreshing(false);
						return;
					case ACTION_UPDATE_MENU:
						mRestaurantMenuFragment.setRefreshing(false);
					default:
						return;
				}
			}
			switch (intent.getAction()) {
				case ACTION_UPDATE_NEARBY:
					mNearbyFragment.update(Utils.parcelableArrayToTypedArray(
							intent.getParcelableArrayExtra(WebRequestService.EXTRA_OUT), Restaurant[].class));
					break;
				case ACTION_ADD_NEARBY:
					mNearbyFragment.addAll(Utils.parcelableArrayToTypedArray(
							intent.getParcelableArrayExtra(WebRequestService.EXTRA_OUT), Restaurant[].class));
					break;
				case ACTION_UPDATE_MENU:
					mRestaurantMenuFragment.update(Utils.parcelableArrayToTypedArray(
							intent.getParcelableArrayExtra(WebRequestService.EXTRA_OUT), MenuEntry[].class));
					break;
				case ACTION_NAV_BACK:
					onBackPressed();
					break;
			}
		}
	}
}
