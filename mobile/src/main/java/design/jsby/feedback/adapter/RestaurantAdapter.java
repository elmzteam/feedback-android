package design.jsby.feedback.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import design.jsby.feedback.R;
import design.jsby.feedback.model.Restaurant;
import design.jsby.feedback.ui.NearbyFragment;
import design.jsby.feedback.util.AbstractLoadableAdapter;

public class RestaurantAdapter extends AbstractLoadableAdapter<RestaurantAdapter.ViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private NearbyFragment.OnFragmentInteractionListener mListener;
	private ArrayList<Restaurant> mRestaurants;

	// View lookup cache
	public static class ViewHolder extends RecyclerView.ViewHolder {
		CardView card;
		TextView title;
		TextView address;

		public ViewHolder(View itemView) {
			super(itemView);
			card = (CardView) itemView.findViewById(R.id.card);
			title = (TextView) itemView.findViewById(R.id.title);
			address = (TextView) itemView.findViewById(R.id.address);
		}
	}

	public RestaurantAdapter(Activity context) {
		mListener = (NearbyFragment.OnFragmentInteractionListener) context;
		mLayoutInflater = LayoutInflater.from(context);
		mRestaurants = new ArrayList<>();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		final View v = mLayoutInflater.inflate(R.layout.item_restaurant, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		final Restaurant restaurant = mRestaurants.get(position);
		viewHolder.title.setText(restaurant.getName());
		viewHolder.address.setText(restaurant.getAddress());
		viewHolder.card.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.select(restaurant);
			}
		});
	}

	@Override
	public int getItemCount() {
		return super.getItemCount() + mRestaurants.size();
	}

	@Override
	public int getItemViewType(int position) {
		return R.layout.item_restaurant;
	}

	public void update(Restaurant[] restaurants) {
		if (restaurants != null) {
			notifyItemRangeRemoved(0, mRestaurants.size());
			mRestaurants.clear();
			addAll(restaurants);
		}
	}

	public void addAll(Restaurant[] restaurants) {
		if (restaurants != null) {
			final int curSize = mRestaurants.size();
			Collections.addAll(mRestaurants, restaurants);
			// Animation should push loading cell down
			notifyItemRangeInserted(curSize, restaurants.length);
		}
	}

	public int size() {
		return mRestaurants.size();
	}

	public Restaurant get(int position) {
		return mRestaurants.get(position);
	}
}
