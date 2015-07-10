package com.school_business.android.school_business;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.school_business.android.school_business.R;

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeFragment.onWelcomeInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener{//, FragmentTabHost.OnTabChangeListener
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	private int tab = 0;
//	private FragmentTabHost tabHost;

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnWelcomeInteractionListener mListener;

	/*
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment WelcomeFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static WelcomeFragment newInstance(String param1, String param2) {
		WelcomeFragment fragment = new WelcomeFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public WelcomeFragment() {
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
		View view = inflater.inflate(R.layout.fragment_welcome, container, false);
		((Button) view.findViewById(R.id.create_event)).setOnClickListener(WelcomeFragment.this);
		mListener.onCreateTab(tab);
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onWelcomeInteraction(uri);
		}
	}

//	@Override
//	public void onResume(){
//
//		super.onResume();
//	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnWelcomeInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onWelcomeInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		mListener = null;
		super.onDetach();
	}

	@Override
	public void onClick(View view){
		switch (view.getId()){
			case R.id.create_event:
				mListener.onCreateEvent(false, "");
				break;
		}
	}

	@Override
	public void onPause(){
		mListener.clearTabs();
		super.onPause();
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
	public interface OnWelcomeInteractionListener {
		// TODO: Update argument type and name
		public void onWelcomeInteraction(Uri uri);
		public void onCreateEvent(Boolean edit, String event);
		public void onCreateTab(int tab);
		public void clearTabs();
	}

//	void createTab(View view){
//		tabHost = new FragmentTabHost(getActivity());//(FragmentTabHost) view.findViewById(android.R.id.tabhost);
//
//		tabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
//
//		tabHost.addTab(tabHost.newTabSpec("First Tab").setIndicator("All"), EventAllFragment.class, null);
//		//TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
//		//TabHost.TabSpec tab3 = tabHost.newTabSpec("Third Tab");
//
//		tabHost.setCurrentTab(0);
//	}
}
