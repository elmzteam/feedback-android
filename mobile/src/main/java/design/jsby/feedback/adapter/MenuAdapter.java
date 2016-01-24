package design.jsby.feedback.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import design.jsby.feedback.R;
import design.jsby.feedback.model.MenuEntry;
import design.jsby.feedback.ui.RestaurantMenuFragment;
import design.jsby.feedback.util.AbstractLoadableAdapter;

public class MenuAdapter extends AbstractLoadableAdapter<MenuAdapter.ViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private RestaurantMenuFragment.OnFragmentInteractionListener mListener;
	private ArrayList<MenuEntry> mMenuEntries;
	private Context mContext;

	// View lookup cache
	public static class ViewHolder extends RecyclerView.ViewHolder {
		View container;
		TextView title;
		TextView description;
		ImageView circle;
		ProgressBar rating;

		public ViewHolder(View itemView) {
			super(itemView);
			if (itemView.getId() == R.id.loading) return;
			container = itemView.findViewById(R.id.content);
			title = (TextView) container.findViewById(R.id.title);
			description = (TextView) container.findViewById(R.id.description);
			circle = (ImageView) container.findViewById(R.id.circle);
			rating = (ProgressBar) itemView.findViewById(R.id.preference);
		}
	}

	public MenuAdapter(Activity context) {
		mListener = (RestaurantMenuFragment.OnFragmentInteractionListener) context;
		mLayoutInflater = LayoutInflater.from(context);
		mMenuEntries = new ArrayList<>();
		mContext = context;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		final View v = mLayoutInflater.inflate(viewType, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {
		if (isLoadable() && position == getItemCount() - 1) return;
		final MenuEntry menuEntry = mMenuEntries.get(position);
		viewHolder.title.setText(menuEntry.getName());
		viewHolder.description.setText(menuEntry.getDescription());
		viewHolder.circle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.select(menuEntry, position);
			}
		});
		viewHolder.container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.select(menuEntry);
			}
		});
		switch (menuEntry.getRating()) {
			case UP:
				viewHolder.circle.setForeground(ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_up));
				viewHolder.circle.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.green));
				break;
			case OK:
				viewHolder.circle.setForeground(ContextCompat.getDrawable(mContext, R.drawable.ic_mood));
				viewHolder.circle.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.grey));
				break;
			case DOWN:
				viewHolder.circle.setForeground(ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_down));
				viewHolder.circle.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.red));
				break;
			case NONE:
				viewHolder.circle.setForeground(ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_both));
				viewHolder.circle.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.accent400));
		}

		// Rating bar
		viewHolder.rating.setProgress((int) menuEntry.getPreference() * 100);
	}

	@Override
	public int getItemCount() {
		return super.getItemCount() + mMenuEntries.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (isLoadable() && position == getItemCount() - 1) {
			return super.getItemViewType(position);
		}
//		if (mMenuEntries.size() == 0) {
//			return R.layout.item_addition;
//		}
		return R.layout.item_menu_entry;
	}

	public void update(MenuEntry[] menuEntries) {
		if (menuEntries != null) {
			notifyItemRangeRemoved(0, mMenuEntries.size());
			mMenuEntries.clear();
			// Add new items
			Collections.addAll(mMenuEntries, menuEntries);
			// Animation should push loading cell down
			notifyItemRangeInserted(0, menuEntries.length);
		}
	}

	public int size() {
		return mMenuEntries.size();
	}

	public MenuEntry get(int position) {
		return mMenuEntries.get(position);
	}
}
