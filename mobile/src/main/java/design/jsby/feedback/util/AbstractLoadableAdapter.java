package design.jsby.feedback.util;

import android.support.v7.widget.RecyclerView;

import design.jsby.feedback.R;

public abstract class AbstractLoadableAdapter<T extends RecyclerView.ViewHolder>
		extends RecyclerView.Adapter<T> {
	private boolean mLoadable = true;

	@Override
	public int getItemCount() {
		return mLoadable ? 1 : 0;
	}

	@Override
	public int getItemViewType(int position) {
		return mLoadable ? R.layout.item_loading : R.layout.item_padding;
	}

	public boolean isLoadable() {
		return mLoadable;
	}

	public void setLoadable(boolean loadable) {
		if (mLoadable == loadable) return;

		if (loadable) {
			mLoadable = true;
			notifyItemInserted(getItemCount() - 2);
		} else {
			mLoadable = false;
			notifyItemRemoved(getItemCount());
		}
	}
}
