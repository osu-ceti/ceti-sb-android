package com.school_business.android.school_business;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventCreateFragment.OnEventCreatorListener} interface
 * to handle interaction events.
 * Use the {@link EventCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventCreateFragment extends Fragment implements View.OnClickListener{
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "edit";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private Boolean mEdit;
	private String mEvent;
	private String mId;
	private OnEventCreatorListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param edit Parameter 1.
	 * @return A new instance of fragment CreateEventFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static EventCreateFragment newInstance(Boolean edit, String event) {
		EventCreateFragment fragment = new EventCreateFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_PARAM1, edit);
		args.putString(ARG_PARAM2, event);
		fragment.setArguments(args);
		return fragment;
	}

	public EventCreateFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mEdit = getArguments().getBoolean(ARG_PARAM1);
			mEvent = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_create_event, container, false);
		formatView(view);
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onPostEvent(packageEvent(), mEdit);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnEventCreatorListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnEventCreatorListener");
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
			case R.id.post_event_button:
				mListener.onPostEvent(packageEvent(), mEdit);
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
	public interface OnEventCreatorListener {
		// TODO: Update argument type and name
		public void onPostEvent(JSONObject event, Boolean edit);
	}

	public JSONObject packageEvent(){
		try {
			JSONObject event = new JSONObject();
			if (mId != null){
				event.put("id", mId);
			}
			EditText data;
			int[] resource = {R.id.ET_content, R.id.ET_title, R.id.ET_tags};
			String[] headers = {"content", "title", "tags",};

			int[] start = {R.id.start_year, R.id.start_month, R.id.start_day, R.id.start_hour, R.id.start_minutes};
			int[] end = {R.id.end_year, R.id.end_month, R.id.end_day, R.id.end_hour, R.id.end_minutes};

			String event_start = createDate(getView().getRootView(), start, R.id.start_pm);
			String event_end = createDate(getView().getRootView(), end, R.id.end_pm);

			for (int i = 0; i < resource.length; i++) {
				data = (EditText) getActivity().findViewById(resource[i]);
				event.put(headers[i], data.getText().toString());
			}
			event.put("event_start", event_start);
			event.put("event_end", event_end);
			event.put("school_id", SchoolBusiness.getUserAttr("school_id"));
			return event;
		} catch (JSONException e){
			Log.d("OOPS", "Something went wrong with packaging an event");
			return new JSONObject();
		}
	}
	public String createDate(View view, int[] resource, int cb){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		int[] cal_res = {Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE};
		EditText et;
		Date date;
		Calendar cal = new GregorianCalendar();
		date = cal.getTime();
		Log.d("test", date.toString());
		//TimeZone timeZone = cal.getTimeZone();
		int[] x = {0,0,0,0,0};
		for (int i = 0; i < 5; i++){
			et = (EditText) view.findViewById(resource[i]);
			x[i] = Integer.parseInt(et.getText().toString());
			if (cal_res[i]==Calendar.MONTH){
				x[i] = (x[i]-1) % 12;
			}
			if (cal_res[i] == Calendar.HOUR_OF_DAY && ((CheckBox) view.findViewById(cb)).isChecked()){
				x[i] = (x[i] + 12) % 24;
			}
		}
		cal.set(x[0],x[1],x[2],x[3],x[4],0);
	//	try {
			date = cal.getTime();
		String strdate = dateFormat.format(date);
		Log.d("test", strdate);
		Log.d("test", date.toString());
//		} catch (ParseException e){
//			// TODO Write error code or ignore
//			date = new Date();
//		}
		return strdate;
	}

	public Date parseDate(String data){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date date;
		try {
			date = dateFormat.parse(data);
		} catch (ParseException e){
			Log.d("Parse Exception:", e.getMessage().toString());
			date = new Date();
		}
		return date;
	}
	public void formatView(View view) {
		int[] start_res = {R.id.start_minutes, R.id.start_hour, R.id.start_day, R.id.start_month, R.id.start_year};
		int[] end_res = {R.id.end_minutes, R.id.end_hour, R.id.end_day, R.id.end_month, R.id.end_year};
		String[] time_min = {"00", "1", "1", "1", "2015"};
		String[] time_max = {"59", "12", "31", "12", "2115"};
		int[] cal_res = {Calendar.MINUTE, Calendar.HOUR, Calendar.DATE, Calendar.MONTH, Calendar.YEAR};

		EditText et;

		Date date;
		Calendar cal = Calendar.getInstance();

		if (mEdit){
			try {
				JSONObject event = new JSONObject(mEvent);
				mId = event.getString("id");
				((EditText) view.findViewById(R.id.ET_title)).setText(event.getString("title"));
				((EditText) view.findViewById(R.id.ET_content)).setText(event.getString("content"));
				if (event.has("tags")){
					((EditText) view.findViewById(R.id.ET_tags)).setText(event.getString("tags"));
				}

				cal.setTime(parseDate(event.getString("event_start")));
				setETDate(cal, view, start_res, time_min, time_max, cal_res, R.id.start_pm);

				cal.setTime(parseDate(event.getString("event_end")));
				setETDate(cal, view, end_res, time_min, time_max, cal_res, R.id.end_pm);

				((Button) view.findViewById(R.id.post_event_button)).setText("Update Event");
			} catch (JSONException e){
				Log.d("JSONException", e.getMessage().toString());
			}
		} else {
			cal.setTime(parseDate(""));

			// Set start time
			cal.set(Calendar.HOUR_OF_DAY, (cal.get(Calendar.HOUR_OF_DAY) + 1 % 24));
			cal.set(Calendar.MINUTE, 0);
			setETDate(cal, view, start_res, time_min, time_max, cal_res, R.id.start_pm);
			// Set end time
			cal.set(Calendar.HOUR_OF_DAY, (cal.get(Calendar.HOUR_OF_DAY) + 1 % 24));
			setETDate(cal, view, end_res, time_min, time_max, cal_res, R.id.end_pm);
		}
		((Button) view.findViewById(R.id.post_event_button)).setOnClickListener(EventCreateFragment.this);
	}

	public void setETDate(Calendar cal, View view, int[] resource, String[] begin, String[] end, int[] cal_res, int cbid) {
		CheckBox cb = (CheckBox) view.findViewById(cbid);
		Log.d("time", cal.toString());
		for (int i = 0; i < resource.length; i++) {
			EditText et = (EditText) view.findViewById(resource[i]);
			et.setFilters(new InputFilter[]{new InputFilterNumeric(begin[i], end[i])});
			if (cal_res[i] == Calendar.HOUR_OF_DAY && cal.get(Calendar.HOUR) != cal.get(Calendar.HOUR_OF_DAY)) {
				cb.setChecked(true);
			}
			if (cal_res[i] == Calendar.MONTH){
				et.setText(""+(cal.get(cal_res[i])+1));
			} else if (cal_res[i] == Calendar.HOUR && cal.get(Calendar.HOUR) == 0) {
				et.setText("12");
			}else {
				et.setText("" + cal.get(cal_res[i]));
			}
		}
	}

	public class InputFilterNumeric implements InputFilter {

		private int min, max;

		public InputFilterNumeric(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public InputFilterNumeric(String min, String max) {
			this.min = Integer.parseInt(min);
			this.max = Integer.parseInt(max);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
		                           int dstart, int dend) {
			try {
				int input = Integer.parseInt(dest.toString() + source.toString());
				if (isInRange(min, max, input))
					return null;
			} catch (NumberFormatException nfe) {
				Toast.makeText(getActivity(),
						"Entered value must be between " + min + " and " + max,
						Toast.LENGTH_LONG).show();
			}
			return "";
		}

		private boolean isInRange(int a, int b, int c) {
			return b > a ? c >= a && c <= b : c >= b && c <= a;
		}
	}
}