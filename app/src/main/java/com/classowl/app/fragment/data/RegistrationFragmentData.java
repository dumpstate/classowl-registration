package com.classowl.app.fragment.data;

import android.os.Bundle;
import android.util.Log;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationFragmentData {
    private static final String TAG = RegistrationFragmentData.class.getSimpleName();

    private static final String ARG_SCHOOL = "reg_school";
    private static final String ARG_USER_TYPE = "reg_user_type";
    private static final String ARG_FIRST_NAME = "reg_first_name";
    private static final String ARG_LAST_NAME = "reg_last_name";
    private static final String ARG_EMAIL = "reg_email";
    private static final String ARG_PASSWORD = "reg_password";

    public static RegistrationFragmentData get(final Bundle bundle) {
        final RegistrationFragmentData data = new RegistrationFragmentData();

        if(bundle != null) {
            data.mSchool = bundle.getInt(ARG_SCHOOL);
            data.mUserType = bundle.getInt(ARG_USER_TYPE);
            data.mFirstName = bundle.getString(ARG_FIRST_NAME);
            data.mLastName = bundle.getString(ARG_LAST_NAME);
            data.mEmail = bundle.getString(ARG_EMAIL);
            data.mPassword = bundle.getString(ARG_PASSWORD);
        }

        return data;
    }

    public Bundle appendToBundle(final Bundle bundle) {
        Assert.assertNotNull(bundle);

        bundle.putInt(ARG_SCHOOL, mSchool);
        bundle.putInt(ARG_USER_TYPE, mUserType);
        bundle.putString(ARG_FIRST_NAME, mFirstName);
        bundle.putString(ARG_LAST_NAME, mLastName);
        bundle.putString(ARG_EMAIL, mEmail);
        bundle.putString(ARG_PASSWORD, mPassword);

        return bundle;
    }

    public JSONObject toJSON() {
        final JSONObject jObject = new JSONObject();

        try {
            jObject.put(ARG_SCHOOL, mSchool);
            jObject.put(ARG_USER_TYPE, mUserType);
            jObject.put(ARG_FIRST_NAME, mFirstName);
            jObject.put(ARG_LAST_NAME, mLastName);
            jObject.put(ARG_EMAIL, mLastName);
            jObject.put(ARG_PASSWORD, mPassword);
        } catch(JSONException e) {
            Log.e(TAG, "JSONException", e);
        }

        return jObject;
    }

    public int mSchool;
    public int mUserType;
    public String mFirstName;
    public String mLastName;
    public String mEmail;
    public String mPassword;
}
