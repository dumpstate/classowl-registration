package com.classowl.app.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.classowl.app.R;

import junit.framework.Assert;

public class RegistrationViewHolder {

    public static RegistrationViewHolder get(View rootView) {
        Assert.assertNotNull(rootView);

        RegistrationViewHolder holder = new RegistrationViewHolder();

        holder.mSchoolsSpinner = (Spinner)rootView.findViewById(R.id.registration_schools_spinner);
        holder.mSchoolsProgressBar = (ProgressBar)rootView.findViewById(R.id.registration_schools_progress_bar);
        holder.mUserSpinner = (Spinner)rootView.findViewById(R.id.registration_user_spinner);
        holder.mFirstNameEditText = (EditText)rootView.findViewById(R.id.registration_firstname_edittext);
        holder.mLastNameEditText = (EditText)rootView.findViewById(R.id.registration_lastname_edittext);
        holder.mEmailEditText = (EditText)rootView.findViewById(R.id.registration_email_edittext);
        holder.mPasswordEditText = (EditText)rootView.findViewById(R.id.registration_password_edittext);
        holder.mSignUpButton = (Button)rootView.findViewById(R.id.registration_signup_button);
        holder.mTermsAndCondTextView = (TextView)rootView.findViewById(R.id.registration_termsandcond_textview);

        Assert.assertNotNull(holder.mSchoolsSpinner);
        Assert.assertNotNull(holder.mSchoolsProgressBar);
        Assert.assertNotNull(holder.mUserSpinner);
        Assert.assertNotNull(holder.mFirstNameEditText);
        Assert.assertNotNull(holder.mLastNameEditText);
        Assert.assertNotNull(holder.mEmailEditText);
        Assert.assertNotNull(holder.mPasswordEditText);
        Assert.assertNotNull(holder.mSignUpButton);
        Assert.assertNotNull(holder.mTermsAndCondTextView);

        return holder;
    }

    public Spinner mSchoolsSpinner;
    public ProgressBar mSchoolsProgressBar;
    public Spinner mUserSpinner;
    public EditText mFirstNameEditText;
    public EditText mLastNameEditText;
    public EditText mEmailEditText;
    public EditText mPasswordEditText;
    public Button mSignUpButton;
    public TextView mTermsAndCondTextView;
}