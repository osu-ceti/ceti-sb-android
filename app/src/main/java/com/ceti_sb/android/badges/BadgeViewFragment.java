package com.ceti_sb.android.badges;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.volley.NetworkVolley;
import com.ceti_sb.android.R;
import com.ceti_sb.android.application.SchoolBusiness;
//import com.facebook.share.model.ShareLinkContent;
//import com.facebook.share.widget.ShareButton;
import android.graphics.drawable.*;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BadgeViewFragment.OnBadgeReceiveListener} interface
 * to handle interaction events.
 * Use the {@link BadgeViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BadgeViewFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	private static final String ARG_PARAM3 = "param3";
	private static final String ARG_PARAM4 = "param4";
	private static final String ARG_PARAM5 = "param5";
	private static final String ARG_PARAM6 = "param6";
	// TODO: Rename and change types of parameters
	private int user_id;
	private String user_name;
	private String event_owner;
	private int event_owner_id;
	private String event_name;
	private String mBadge;
	private String school_name;
	private int badge_id;
	private Uri badge_uri;
	private String url;
	private Boolean notification;
	private OnBadgeReceiveListener mListener;
    private BadgeImageView badge;
    private ImageLoader imageLoader = null;
	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param badge Parameter 1.
	 * @param notification Parameter 2.
	 * @return A new instance of fragment ReceiveBadgeFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static BadgeViewFragment newInstance(Bundle badge, Boolean notification) {
		BadgeViewFragment fragment = new BadgeViewFragment();
		Bundle args = new Bundle();
		args.putBundle("badge", badge);
		args.putBoolean("notification", notification);
		fragment.setArguments(args);
		return fragment;
	}

	public BadgeViewFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			Bundle badge = getArguments().getBundle("badge");
			notification = getArguments().getBoolean("notification");
			user_id = Integer.parseInt(badge.getString("user_id"));
			user_name = badge.getString("user_name");
			event_owner = badge.getString("event_owner");
			event_owner_id = Integer.parseInt(badge.getString("event_owner_id"));
			event_name = badge.getString("event_name");
			mBadge = badge.getString("badge_url");
			school_name = SchoolBusiness.toDisplayCase(badge.getString("school_name"));
			badge_id = Integer.parseInt(badge.getString("badge_id"));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		String content;
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_badge_view, container, false);
		LinearLayout display = (LinearLayout) view.findViewById(R.id.badge_display);
        imageLoader = NetworkVolley.getInstance(getActivity()
				.getApplicationContext()).getImageLoader();

        badge = new BadgeImageView(getActivity().getApplicationContext());
		display.addView(badge);
		String badge_str = SchoolBusiness.AWS_S3 + badge_id + Constants.SLASH + mBadge;
		badge.setImageUrl(badge_str, imageLoader);
		badge.getLayoutParams().height = 512;
		badge.getLayoutParams().width = 512;
		TextView tv = (TextView) view.findViewById(R.id.badge_message);
		String addressee = (user_id == Integer.parseInt(SchoolBusiness.getUserAttr(Constants.ID))) ? "you" : user_name;
		if (notification) {
			content = event_owner + " awards you a badge for speaking at the event: " + event_name
					+ ", at " + school_name;
			tv.setText(content);
		} else {
			view.findViewById(R.id.title).setVisibility(View.GONE);
			 content = event_owner + " awarded " + user_name + " a badge for speaking at the event:"
					+ " " + event_name + ", at " + school_name;
			tv.setText(content);
		}

		if (user_id == Integer.parseInt(SchoolBusiness.getUserAttr(Constants.ID))) {
			badge_uri = Uri.parse(badge_str);
			url = SchoolBusiness.getUrl() + "/users/" + SchoolBusiness.getUserAttr(Constants.ID) + "/badges/" + badge_id;
//			final ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
//					.setContentUrl(Uri.parse(url))
//					.setContentTitle(SchoolBusiness.getUserAttr(Constants.NAME) +
//							" was awarded a badge!")
//					.setContentDescription("Badge awarded for speaking at the event" + event_name
//							+ ", at " + school_name)
//					.setImageUrl(badge_uri)
//					.build();

//			final ShareButton shareButton = (ShareButton) view.findViewById(R.id.fb_share_button);
//			shareButton.setClickable(true);
//			shareButton.setShareContent(shareLinkContent);
//            Button tweet = (Button) view.findViewById(R.id.tweet_button);
//            tweet.setOnClickListener(this);
            Button share = (Button) view.findViewById(R.id.shareall_button);
            share.setOnClickListener(this);
		} else {
//			view.findViewById(R.id.tweet_button).setVisibility(View.GONE);
//            view.findViewById(R.id.fb_share_button).setVisibility(View.GONE);
            view.findViewById(R.id.shareall_button).setVisibility(View.GONE);
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnBadgeReceiveListener) activity;
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

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, "sharefile.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "sharefile.png");
        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
    private void shareIt() {

        try {

            Bitmap bmp = ((BitmapDrawable) badge.getDrawable()).getBitmap();
            if (bmp != null) {

                File mFile = savebitmap(bmp);
                Uri u = null;
                u = Uri.fromFile(mFile);

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("image/*");

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sharing Badge!");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        "Badge awarded for speaking at the event "+ event_name
							+ ", at " + school_name);
                sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, u);

                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }

        } catch(Exception e) {
            System.out.println(e);
        }
    }

	@Override
	public void onClick(View view){
		switch(view.getId()){
//			case R.id.fb_share_button:
//				break;
//			case R.id.tweet_button:
//				mListener.onTweetBadge(badge_uri, url);
//				break;
            case R.id.shareall_button:
                shareIt();
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
	public interface OnBadgeReceiveListener {
		// TODO: Update argument type and name
//		public void onShareBadgeFacebook(Uri uri);
//		public void onTweetBadge(Uri uri, String url);
	}

}
