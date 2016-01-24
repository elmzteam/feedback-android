package design.jsby.feedback.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import design.jsby.feedback.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NearbyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SubmitFragment extends Fragment {
	public interface OnFragmentInteractionListener {
		void submit();
	}

	private OnFragmentInteractionListener mListener;
	private EditText caption;

	public SubmitFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_submit, container, false);
		caption = (EditText) rootView.findViewById(R.id.caption);
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

	public String getCaption() {
		return caption.getText().toString();
	}
}
