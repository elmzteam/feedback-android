package design.jsby.feedback.util;

import android.support.v7.widget.RecyclerView;

import design.jsby.feedback.R;

public abstract class AbstractLoadableAdapter<T extends RecyclerView.ViewHolder>
		extends RecyclerView.Adapter<T> {
	private boolean mLoadable = true;

	@Override
	public int getItemCount() {
		return 1;
	}

	@Override
	public int getItemViewType(int position) {
		return mLoadable ? R.layout.item_loading : R.layout.item_padding;
	}

	public void setLoadable(boolean loadable) {
		mLoadable = loadable;
	}
}
