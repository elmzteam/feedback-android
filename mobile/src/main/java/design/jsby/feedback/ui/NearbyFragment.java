package design.jsby.feedback.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import design.jsby.feedback.R;
import design.jsby.feedback.adapter.RestaurantAdapter;
import design.jsby.feedback.decoration.CardDecoration;
import design.jsby.feedback.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NearbyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NearbyFragment extends Fragment {
	public interface OnFragmentInteractionListener {
		void refresh();
		void select(Restaurant restaurant);
	}

	private RestaurantAdapter mAdapter;
	private LinearLayoutManager mLayoutManager;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView mRecyclerView;

	private OnFragmentInteractionListener mListener;

	public NearbyFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new RestaurantAdapter(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_nearby, container, false);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.refresh();
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.accent400);
		if (mAdapter.size() == 0) {
			mSwipeRefreshLayout.setEnabled(false); // TODO: make this a sane check
		}

		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
		mLayoutManager = new LinearLayoutManager(inflater.getContext());
		mRecyclerView.addItemDecoration(new CardDecoration());
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setAdapter(mAdapter);

		return rootView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mListener = (OnFragmentInteractionListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public void update(Restaurant[] restaurants) {
		mAdapter.update(restaurants);
		setRefreshing(false);
		mRecyclerView.scrollToPosition(0); // Prevent scrolling to bottom
	}

	public void addAll(Restaurant[] restaurants) {
		mAdapter.addAll(restaurants);
	}

	public void setRefreshing(boolean refreshing) {
		mSwipeRefreshLayout.setEnabled(true);
		mSwipeRefreshLayout.setRefreshing(refreshing);
	}
}
