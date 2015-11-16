package com.ceti_sb.android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ceti_sb.android.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnUserProfileListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnUserProfileListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1
	 * @return A new instance of fragment UserProfileFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static UserProfileFragment newInstance(String param1) {
		UserProfileFragment fragment = new UserProfileFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		//args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public UserProfileFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
		if (getArguments() != null){
			renderUser(view, mParam1);
		}
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_location:
				mListener.onUserViewInteraction((String) view.findViewById(R.id.tv_location).getTag(), "schools");
				break;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnUserProfileListener) activity;
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
	public interface OnUserProfileListener {
		// TODO: Update argument type and name
		public void onUserViewInteraction(String id, String model);
	}

	public String get_id(int res){
		switch (res){
			case R.id.tv_location:
				return "school_id";
			default:
				return "";
		}
	}
	private void renderUser(View view, String str_response){
		try {
			JSONObject response = new JSONObject(str_response);
			String str;
			String link = "";
			JSONObject user = response.getJSONObject("user");
			String user_id = user.getString("id");
			String name = user.getString("name");
			int[] resource = {R.id.tv_location, R.id.tv_grades,R.id.tv_job,R.id.tv_business,R.id.tv_role,R.id.tv_bio};
			String[] names = {"school_name", "grades", "job_title", "business", "role", "biography"};
			TextView tv;
			if (user.getString("role").equals("Teacher")) {
				((LinearLayout) view.findViewById(R.id.layout_user_business)).setVisibility(View.GONE);
			} else if (user.getString("role").equals("Speaker")) {
				((LinearLayout) view.findViewById(R.id.layout_user_teacher)).setVisibility(View.GONE);
			}
			for (int i = 0; i < resource.length; i++) {
				tv = (TextView) view.findViewById(resource[i]);

				link = get_id(resource[i]);
				if (!link.equals("")) {
					link = user.getString(link);
					tv.setOnClickListener(UserProfileFragment.this);
					tv.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
				} else {
					link = "0";
				}
				str = names[i];
				str = SchoolBusiness.toDisplayCase(user.getString(str));
				tv.setTag(link);
				tv.setText(str);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
}