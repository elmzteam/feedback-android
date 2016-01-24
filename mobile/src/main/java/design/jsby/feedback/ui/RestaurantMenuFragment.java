package design.jsby.feedback.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import design.jsby.feedback.R;
import design.jsby.feedback.adapter.MenuAdapter;
import design.jsby.feedback.decoration.ListDecoration;
import design.jsby.feedback.model.MenuEntry;
import design.jsby.feedback.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RestaurantMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantMenuFragment extends Fragment {
	public interface OnFragmentInteractionListener {
		// TODO: make this something useful
		void select(Restaurant restaurant);
		void select(MenuEntry entry, int position);
	}

	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_RESTAURANT = "restaurant";

	private Restaurant mRestaurant;

	private MenuAdapter mAdapter;
	private LinearLayoutManager mLayoutManager;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView mRecyclerView;
	private OnFragmentInteractionListener mListener;

	private RecyclerView.ItemAnimator.ItemAnimatorFinishedListener unload =
			new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
				@Override
				public void onAnimationsFinished() {
					mAdapter.setLoadable(false);
				}
			};

	public RestaurantMenuFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param restaurant Preloaded restaurant object
	 * @return A new instance of fragment RestaurantMenuFragment.
	 */
	public static RestaurantMenuFragment newInstance(Restaurant restaurant) {
		final RestaurantMenuFragment fragment = new RestaurantMenuFragment();
		fragment.setArgRestaurant(restaurant);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mRestaurant = getArguments().getParcelable(ARG_RESTAURANT);
			mAdapter = new MenuAdapter(getActivity());
		} else {
			throw new IllegalArgumentException("Wow how mean");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View rootView = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);
				mListener.select(mRestaurant);
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.accent400);
		mSwipeRefreshLayout.setEnabled(false);

		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
		mLayoutManager = new LinearLayoutManager(inflater.getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.addItemDecoration(new ListDecoration(ContextCompat.getDrawable(getActivity(),
				R.drawable.abc_list_divider_mtrl_alpha)));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setAdapter(mAdapter);

		return rootView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	void update(MenuEntry[] entries) {
//		if (entries.length == 0) {
//			mRecyclerView.addItemDecoration(new CardDecoration());
//		} else {
//			mRecyclerView.addItemDecoration(new ListDecoration(ContextCompat.getDrawable(getActivity(),
//					R.drawable.abc_list_divider_mtrl_alpha)));
//		}
//		mRecyclerView.clearAnimation();

		mAdapter.update(entries);
		mSwipeRefreshLayout.setRefreshing(false);
		mRecyclerView.scrollToPosition(0);
		mRecyclerView.getItemAnimator().isRunning(unload);
	}

	public void setArgRestaurant(Restaurant restaurant) {
		final Bundle args = new Bundle();
		args.putParcelable(ARG_RESTAURANT, restaurant);
		setArguments(args);
	}

	void setRefreshing(boolean refreshing) {
		mSwipeRefreshLayout.setRefreshing(refreshing);
	}

	void update(int position) {
		mAdapter.notifyItemChanged(position);
	}

	Restaurant getRestaurant() {
		return mRestaurant;
	}
}
