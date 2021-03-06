package com.ceti_sb.android.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ceti_sb.android.R;
import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.application.SchoolBusiness;
import com.ceti_sb.android.volley.NetworkVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SignUpActivity extends Activity implements View.OnClickListener
{
	Toast toast;
	String role;
	final String TAG = "SignUp";
    protected ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
	    toast = Toast.makeText(this, Constants.NULL, Toast.LENGTH_SHORT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId()) {
			case R.id.register_teacher:
				role = getString(R.string.teacher);
				break;
			case R.id.register_speaker:
				role = getString(R.string.speaker);
				break;
			case R.id.register_both:
				role = getString(R.string.both);
				break;
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.register_button:
				JSONObject params = getParams();
				JSONObject user = new JSONObject();
				Log.d(TAG, "Button clicked?");
				try {

					if (!comparePassword(params)){
						toaster("Passwords Do Not Match!");
					}
					else if (!validateEmail(params.getString(Constants.EMAIL))){
						toaster("Email is Invalid or Missing");
					} else if (!hasRole()) {
						toaster("Please Select a Role");
					} else if (!hasName(params.getString(Constants.NAME))){
						toaster("Please Enter Your Name");
					} else if (params.getString(Constants.NAME).length() > 255){
						toaster("Name should be under 255 characters");
					}
					else if (params.getString(Constants.NAME).trim().length() < 2){
						toaster("Name should be more than 2 characters");
					}
					else if (params.getString(Constants.EMAIL).length() > 255){
						toaster("Email should be under 255 characters");
					}
					else if (params.getString(Constants.PASSWORD).length() > 255){
						toaster("Password should be under 255 characters");
					}
					else {
						Log.d(TAG, "Parameters are present");

						user.put("user", params);

						findViewById(R.id.register_button).setClickable(false);
						register(user);
						}
				} catch (JSONException e){
					Log.d(TAG, "User Params Failed");
				}
				break;
		}
	}

	private void toaster(String message){
		toast.cancel();
		toast = Toast.makeText(this, message,
				Toast.LENGTH_LONG);
		toast.show();
	}
	private Boolean hasName(String name){
		return !name.equals(Constants.NULL);
	}

	private Boolean hasRole(){
		return  ((RadioButton) findViewById(R.id.register_teacher)).isChecked() ||
				((RadioButton) findViewById(R.id.register_speaker)).isChecked() ||
				((RadioButton) findViewById(R.id.register_both)).isChecked();
	}

	private Boolean validateEmail(String target){
		return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}

	private Boolean comparePassword(JSONObject user) {
		try {
			return user.getString(Constants.PASSWORD).equals(user.getString(Constants.CONFIRM_PASSWORD));
		} catch (JSONException e) {
			return false;
		}
	}


	public JSONObject getParams() {
		JSONObject user = new JSONObject();
		EditText field;
		try {
			field = (EditText) findViewById(R.id.name);
			user.put(Constants.NAME, field.getText());
			field = (EditText) findViewById(R.id.email);
			user.put(Constants.EMAIL, field.getText());
			field = (EditText) findViewById(R.id.password);
			user.put(Constants.PASSWORD, field.getText());
			field = (EditText) findViewById(R.id.confirm_password);
			user.put(Constants.CONFIRM_PASSWORD, field.getText());
			user.put(Constants.ROLE, role);
			user.put("school_id", "1");
		} catch (JSONException e) {
			Log.d(TAG, "Get Params Failed");
		}
		return user;
	}

	public void register(JSONObject obj){
		String user_auth;
		String url = com.ceti_sb.android.application.SchoolBusiness.getTarget() + "users/sign_up";
		RequestQueue queue = NetworkVolley.getInstance(this.getApplicationContext())
				.getRequestQueue();
		Log.d(TAG, "Register?");
        showLoader();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,obj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
                        closeLoader();
						try {
							if (response.getString("state").equals("1")){
								findViewById(R.id.register_button).setClickable(true);
								JSONArray messages = response.getJSONArray("messages");
								for (int i = 0; i < messages.length(); i++ ){
									String message = messages.getString(i);
									toaster(message);
								}
							} else if (response.getString("state").equals("0")) {
								JSONObject user = response.getJSONObject("data");
								SchoolBusiness.setProfile(user);
								SchoolBusiness.saveLogin(getApplicationContext());
								Log.d(TAG, "Registration successful");

								finishSignUp();
							}
						} catch (JSONException e) {
							findViewById(R.id.register_button).setClickable(true);
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error){
				Log.d(TAG + " Volley", error.toString());
                closeLoader();
			}
		}){
			@Override
			public String getBodyContentType(){
				return "application/json";
			}
		};
		queue.add(jsonRequest);
	}

	public void finishSignUp() {
		Intent intent = new Intent(this, com.ceti_sb.android.controller.MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		if (!isFinishing()){
			finish();
		}
	}

    public void showLoader(){
        final SignUpActivity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                try{
                    if (progress == null) {
                        progress = ProgressDialog.show(activity, "Loading", "Please wait...");
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
    public void closeLoader(){
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    if (progress != null) {
                        progress.dismiss();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                progress = null;
            }
        });
    }
}
