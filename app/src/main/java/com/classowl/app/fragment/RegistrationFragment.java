package com.classowl.app.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.classowl.app.R;
import com.classowl.app.adapter.HintSpinnerAdapter;
import com.classowl.app.fragment.data.RegistrationFragmentData;
import com.classowl.app.message.Constants;
import com.classowl.app.message.SchoolsMsg;
import com.classowl.app.model.School;
import com.classowl.app.service.UiDataFeedIntentService;
import com.classowl.app.viewholder.RegistrationViewHolder;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class RegistrationFragment extends Fragment {
    private static final String TAG = RegistrationFragment.class.getSimpleName();

    private RegistrationViewHolder mViewHolder;
    private RegistrationFragmentData mData;

    private Context mContext;
    private LocalBroadcastManager mBroadcastManager;

    public RegistrationFragment() { /* Required empty public constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mBroadcastManager = LocalBroadcastManager.getInstance(mContext);

        mData = RegistrationFragmentData.get(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_registation, container, false);
        mViewHolder = RegistrationViewHolder.get(rootView);

        init();
        restoreViewState();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBroadcastManager.registerReceiver(
                mUiDataFeedBroadcastReceiver,
                new IntentFilter(Constants.SCHOOLS_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        mBroadcastManager.unregisterReceiver(mUiDataFeedBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateData();
        mData.appendToBundle(outState);
    }

    /**
     * Restores state of the view. mData object should be initialized first.
     */
    private void restoreViewState() {
        if(mData.mSchool >= 0) {
            mViewHolder.mSchoolsSpinner.setSelection(mData.mSchool);
        }
        if(mData.mUserType >= 0) {
            mViewHolder.mUserSpinner.setSelection(mData.mUserType);
        }
        mViewHolder.mFirstNameEditText.setText(mData.mFirstName);
        mViewHolder.mLastNameEditText.setText(mData.mLastName);
        mViewHolder.mEmailEditText.setText(mData.mEmail);
        mViewHolder.mPasswordEditText.setText(mData.mPassword);
    }

    /**
     * Updates mData object with the entered values.
     */
    private void updateData() {
        mData.mSchool = mViewHolder.mSchoolsSpinner.getSelectedItemPosition();
        mData.mUserType = mViewHolder.mUserSpinner.getSelectedItemPosition();
        mData.mFirstName = mViewHolder.mFirstNameEditText.getText().toString();
        mData.mLastName = mViewHolder.mLastNameEditText.getText().toString();
        mData.mEmail = mViewHolder.mEmailEditText.getText().toString();
        mData.mPassword = mViewHolder.mPasswordEditText.getText().toString();
    }

    /**
     * Displays AlertDialog with the form data.
     */
    private void showData() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        final StringWriter sWriter = new StringWriter();
        final JsonWriter jWriter = new JsonWriter(sWriter);
        jWriter.setIndent("  ");
        try {
            jWriter.beginObject();
            jWriter.name("school_id");
            jWriter.value(
                    ((HintSpinnerAdapter.Item<School>) mViewHolder.mSchoolsSpinner
                            .getSelectedItem()).getValue().mId
            );

            jWriter.name("user_type");
            jWriter.value(
                    ((HintSpinnerAdapter.Item<String>) mViewHolder.mUserSpinner
                            .getSelectedItem()).getValue()
            );

            jWriter.name("first_name");
            jWriter.value(mData.mFirstName);

            jWriter.name("last_name");
            jWriter.value(mData.mLastName);

            jWriter.name("email");
            jWriter.value(mData.mEmail);

            jWriter.name("password");
            jWriter.value(mData.mPassword);

            jWriter.endObject();
            jWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to JSONize data.");
        }

        builder.setMessage(sWriter.toString());
        builder.show();
    }

    /**
     * Enables or disables 'Sign up' button - depending whether for form is valid.
     */
    private void enableButton() {
        mViewHolder.mSignUpButton.setEnabled(isFormValid());
    }

    /**
     * Checks whether the form is valid.
     *
     * @return true if form is valid, false otherwise
     */
    private boolean isFormValid() {
        if(mViewHolder.mSchoolsSpinner.isEnabled()) {
            if (((HintSpinnerAdapter.Item) mViewHolder.mSchoolsSpinner.getSelectedItem()).isHint())
                return false;
        } else return false;
        if(((HintSpinnerAdapter.Item)mViewHolder.mUserSpinner.getSelectedItem()).isHint())
            return false;
        if(mViewHolder.mFirstNameEditText.getText().length() == 0)
            return false;
        if(mViewHolder.mLastNameEditText.getText().length() == 0)
            return false;
        if(mViewHolder.mEmailEditText.getText().length() == 0)
            return false;
        if(mViewHolder.mPasswordEditText.getText().length() == 0)
            return false;

        return true;
    }

    /**
     * Widgets initializer.
     */
    private void init() {
        initSpinners();
        initButton();
        initHyperlink();
        initOnChangeListeners();
        initActionBar();
        requestFeedUi();
    }

    /**
     * Initializing on change listeners on the whole formulae. Each listener should
     * try to enable button, by calling enableButton().
     */
    private void initOnChangeListeners() {
        mViewHolder.mSchoolsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enableButton();
                // Checking, whether current value is a hint. If not, marking view as checked.
                // That has been introduces, because of different text color for hint label.
                if (view != null && mViewHolder.mSchoolsSpinner.isEnabled()) {
                    if (!((HintSpinnerAdapter.Item) mViewHolder.mSchoolsSpinner.getSelectedItem()).isHint())
                        ((CheckedTextView) (view)).setChecked(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mViewHolder.mUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enableButton();
                // Checking, whether current value is a hint. If not, marking view as checked.
                // That has been introduces, because of different text color for hint label.
                if (view != null && mViewHolder.mUserSpinner.isEnabled()) {
                    if (!((HintSpinnerAdapter.Item) mViewHolder.mUserSpinner.getSelectedItem()).isHint())
                        ((CheckedTextView) (view)).setChecked(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mViewHolder.mFirstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mViewHolder.mLastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mViewHolder.mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mViewHolder.mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Initializing 'Sign up' button. Setting onClickListener.
     */
    private void initButton() {
        mViewHolder.mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update the mData variable first
                updateData();
                // display message with the mData content
                showData();
            }
        });
    }

    /**
     * Posts a request to the UiDataFeedIntentService, for retrieving remote data that
     * ui depends on.
     */
    private void requestFeedUi() {
        if(mData.mSchools == null) {
            Intent intent = new Intent(mContext, UiDataFeedIntentService.class);
            intent.putExtra(Constants.MSG_TYPE, Constants.MSG_GET_SCHOOLS);
            mContext.startService(intent);
            // make the progress bar visible
            mViewHolder.mSchoolsProgressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Initializing TextView, such that hyperlink is clickable.
     */
    private void initHyperlink() {
        mViewHolder.mTermsAndCondTextView
                .setMovementMethod(LinkMovementMethod.getInstance());

        // remove TextView underlines
        removeUnderlines(mViewHolder.mTermsAndCondTextView);
    }

    /**
     * Removes underlines from a given TextView.
     * Thanks to Reuben Scratton from Stackoverflow.
     * http://stackoverflow.com/questions/4096851/remove-underline-from-links-in-textview-
     *
     * TODO: probably should be refactored to some util class
     */
    private void removeUnderlines(final TextView tv) {
        class URLSpanNoUnderline extends URLSpan {
            public URLSpanNoUnderline(String url) {
                super(url);
            }

            @Override
            public void updateDrawState(TextPaint tp) {
                super.updateDrawState(tp);
                tp.setUnderlineText(false);
            }
        }

        if(tv != null) {
            final Spannable s = (Spannable) tv.getText();
            final URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
            for(URLSpan span: spans) {
                final int start = s.getSpanStart(span);
                final int end = s.getSpanEnd(span);
                s.removeSpan(span);
                span = new URLSpanNoUnderline(span.getURL());
                s.setSpan(span, start, end, 0);
            }
            tv.setText(s);
        }
    }

    /**
     * Initializing action bar - setting title and implementing on back button clicked handler.
     */
    private void initActionBar() {
        final View actionBarView = getActivity().getActionBar().getCustomView();
        final TextView title = (TextView)actionBarView.findViewById(R.id.action_bar_title);
        title.setText(R.string.registration_action_bar_title);

        // initialize back button clicked handler
        final ImageView back = (ImageView)actionBarView.findViewById(R.id.action_bar_back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close the app
                getActivity().finish();
            }
        });
    }

    /**
     * Initializing shools & user type spinners.
     */
    private void initSpinners() {
        initSchoolsSpinner();
        initUserTypeSpinner();
    }

    /**
     * Initializing schools spinner - if there is no data in the mData.mSchools variable,
     * then school spinner is initialized with the default value only, otherwise the
     * appropriate adapter is being set filled with schools data.
     */
    private void initSchoolsSpinner() {
        if(mData.mSchools == null) {
            mViewHolder.mSchoolsSpinner.setAdapter(
                    new ArrayAdapter<String>(
                            mContext,
                            R.layout.spinner_item,
                            new String[]{getString(R.string.registration_schools_hint)})
            );
            mViewHolder.mSchoolsSpinner.setSelection(0);
            mViewHolder.mSchoolsSpinner.setEnabled(false);
        } else {
            HintSpinnerAdapter.setAdapter(
                    mContext,
                    mViewHolder.mSchoolsSpinner,
                    getString(R.string.registration_schools_hint),
                    getSchoolsItems(mData.mSchools)
            );
        }
    }

    /**
     * Initializing user type spinner - retrieving R.array.registration_user_type, from
     * resources and setting HintSpinnerAdapter onto user spinner.
     */
    private void initUserTypeSpinner() {
        HintSpinnerAdapter.setAdapter(
                mContext,
                mViewHolder.mUserSpinner,
                getString(R.string.registration_users_hint),
                getUserTypeItems(getResources().getStringArray(R.array.registration_user_type))
        );
    }

    /**
     * Mapping array of Schools into List of Item<School> to use with HintSpinnerAdapter.
     *
     * @param schools array of schools
     * @return list of Item<School>
     */
    private List<HintSpinnerAdapter.Item<School>> getSchoolsItems(final School[] schools) {
        final ArrayList<HintSpinnerAdapter.Item<School>> items
                = new ArrayList<HintSpinnerAdapter.Item<School>>();
        if(schools != null) {
            for(School school: schools)
                items.add(new HintSpinnerAdapter.Item<School>(school.mName, school));
        }
        return items;
    }

    /**
     * Mapping array of Strings - user types into List of Item<String>
     * to use with HintSpinnerAdapter.
     *
     * @param userTypes array of userTypes
     * @return list of Item<String>
     */
    private List<HintSpinnerAdapter.Item<String>> getUserTypeItems(final String[] userTypes) {
        final ArrayList<HintSpinnerAdapter.Item<String>> items
                = new ArrayList<HintSpinnerAdapter.Item<String>>();
        if(userTypes != null) {
            for(String userType: userTypes)
                items.add(new HintSpinnerAdapter.Item<String>(userType, userType.toLowerCase()));
        }
        return items;
    }

    /**
     * BroadcastReceiver expecting SchoolMsg passed in the intent serializable extra
     * under Constants.SCHOOLS_DATA identifier. If the schools array is empty,
     * appropriate Toast message is being displayed, otherwise schools spinner
     * is being filled with data and progress bar is being dismissed.
     */
    private BroadcastReceiver mUiDataFeedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final SchoolsMsg schools =
                    (SchoolsMsg)intent.getSerializableExtra(Constants.SCHOOLS_DATA);
            if(schools != null) {
                if(schools.mSchools != null && schools.mSchools.length > 0) {
                    mData.mSchools = schools.mSchools;

                    // fill schools spinner with schools data
                    HintSpinnerAdapter.setAdapter(
                            mContext,
                            mViewHolder.mSchoolsSpinner,
                            getString(R.string.registration_schools_hint),
                            getSchoolsItems(schools.mSchools)
                    );

                    mViewHolder.mSchoolsSpinner.setEnabled(true);
                    mViewHolder.mSchoolsProgressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(
                            mContext,
                            getString(R.string.registration_toast_unable_to_connect),
                            Toast.LENGTH_SHORT).show();
                    mViewHolder.mSchoolsProgressBar.setVisibility(View.GONE);
                }
            }
        }
    };

}