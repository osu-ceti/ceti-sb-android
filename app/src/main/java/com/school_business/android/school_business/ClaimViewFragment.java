package com.school_business.android.school_business;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by david on 7/8/15.
 */
public class ClaimViewFragment extends Fragment implements View.OnClickListener{
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String JSON_RESPONSE = "response";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private String event_id;
	private String claim_id;
	private OnClaimViewInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @return A new instance of fragment ClaimViewFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ClaimViewFragment newInstance(JSONObject param1) {
		ClaimViewFragment fragment = new ClaimViewFragment();
		Bundle args = new Bundle();
		args.putString(JSON_RESPONSE, param1.toString());
		fragment.setArguments(args);
		return fragment;
	}

	public ClaimViewFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(JSON_RESPONSE);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_claim_view, container, false);
		if (getArguments() != null) {
			renderClaim(view, getArguments().getString(JSON_RESPONSE));
		}
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onClaimViewInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnClaimViewInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onClick(View view){
		switch (view.getId()){
			case R.id.accept_button:
				mListener.onAcceptClaim(event_id, claim_id);
				break;
			case R.id.reject_button:
				mListener.onRejectClaim(event_id, claim_id);
				break;
			case R.id.message_button:
				break;
		}
	}
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnClaimViewInteractionListener {
		// TODO: Update argument type and name
		public void onAcceptClaim(String ev_id, String us_id);
		public void onRejectClaim(String ev_id, String us_id);
	}

	public String get_id(int res){
		switch (res){
			case R.id.TV_School:
				return "school_id";
			default:
				return "";
		}
	}

	private void renderClaim(View view, String str_response){
		try {
			JSONObject response = new JSONObject(str_response);
			event_id = response.getString("event_id");
			claim_id = response.getString("claim_id");
			String str;
			String link = "";
			int[] resource = {R.id.tv_title, R.id.tv_job, R.id.tv_business};
			String[] name = {"user_name", "job_title", "business"};
			TextView tv;
			((Button) view.findViewById(R.id.accept_button)).setOnClickListener(ClaimViewFragment.this);
			((Button) view.findViewById(R.id.reject_button)).setOnClickListener(ClaimViewFragment.this);
			((Button) view.findViewById(R.id.message_button)).setOnClickListener(ClaimViewFragment.this);
			for (int i = 0; i < resource.length; i++) {
				tv = (TextView) view.findViewById(resource[i]);

				link = get_id(resource[i]);
				if (!link.equals("")) {
					link = response.getString(link);
					tv.setOnClickListener(ClaimViewFragment.this);
					tv.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
				} else {
					link = "0";
				}
				str = name[i];
				str = SchoolBusiness.toDisplayCase(response.getString(str));
				tv.setTag(link);
				tv.setText(str);
			}
		} catch (JSONException e){
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
}

