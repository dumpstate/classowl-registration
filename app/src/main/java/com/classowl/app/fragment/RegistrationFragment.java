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
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.classowl.app.R;
import com.classowl.app.adapter.HintSpinnerAdapter;
import com.classowl.app.fragment.data.RegistrationFragmentData;
import com.classowl.app.message.Constants;
import com.classowl.app.message.SchoolsMsg;
import com.classowl.app.model.School;
import com.classowl.app.service.UiDataFeedIntentService;
import com.classowl.app.viewholder.RegistrationViewHolder;

import java.util.Arrays;
import java.util.LinkedList;
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
    public void onStart() {
        super.onStart();
        requestFeedUi();
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

    private void restoreViewState() {
        if(mData.mSchool >= 0) {
            Log.d("ALBERT", "restoreViewState schools: " + mData.mSchool);
            mViewHolder.mSchoolsSpinner.setSelection(mData.mSchool);
        }
        if(mData.mUserType >= 0) {
            Log.d("ALBERT", "restoreViewState user: " + mData.mUserType);
            mViewHolder.mUserSpinner.setSelection(mData.mUserType);
        }
        mViewHolder.mFirstNameEditText.setText(mData.mFirstName);
        mViewHolder.mLastNameEditText.setText(mData.mLastName);
        mViewHolder.mEmailEditText.setText(mData.mEmail);
        mViewHolder.mPasswordEditText.setText(mData.mPassword);
    }

    private void updateData() {
        mData.mSchool = mViewHolder.mSchoolsSpinner.getSelectedItemPosition();
        mData.mUserType = mViewHolder.mUserSpinner.getSelectedItemPosition();
        mData.mFirstName = mViewHolder.mFirstNameEditText.getText().toString();
        mData.mLastName = mViewHolder.mLastNameEditText.getText().toString();
        mData.mEmail = mViewHolder.mEmailEditText.getText().toString();
        mData.mPassword = mViewHolder.mPasswordEditText.getText().toString();
    }

    private void showData() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mData.toJSON().toString());
        builder.show();
    }

    private void enableButton() {
        mViewHolder.mSignUpButton.setEnabled(isFormValid());
    }

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
    }

    private void initOnChangeListeners() {
        mViewHolder.mSchoolsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.w("ALBERT", "schools selected: " + position);
                enableButton();
                if(view != null && mViewHolder.mSchoolsSpinner.isEnabled()) {
                    if (!((HintSpinnerAdapter.Item) mViewHolder.mSchoolsSpinner.getSelectedItem()).isHint())
                        ((CheckedTextView) (view)).setChecked(true);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        mViewHolder.mUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.w("ALBERT", "user selected: " + position);
                enableButton();
                if(view != null && mViewHolder.mUserSpinner.isEnabled()) {
                    if (!((HintSpinnerAdapter.Item) mViewHolder.mUserSpinner.getSelectedItem()).isHint())
                        ((CheckedTextView) (view)).setChecked(true);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        mViewHolder.mFirstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButton();
            }

            @Override public void afterTextChanged(Editable s) {}
        });
        mViewHolder.mLastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButton();
            }

            @Override public void afterTextChanged(Editable s) {}
        });
        mViewHolder.mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButton();
            }

            @Override public void afterTextChanged(Editable s) {}
        });
        mViewHolder.mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButton();
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void initButton() {
        mViewHolder.mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
                showData();
            }
        });
    }

    private void requestFeedUi() {
        if(mData.mSchools == null) {
            Intent intent = new Intent(mContext, UiDataFeedIntentService.class);
            intent.putExtra(Constants.MSG_TYPE, Constants.MSG_GET_SCHOOLS);
            mContext.startService(intent);
            mViewHolder.mSchoolsProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initHyperlink() {
        mViewHolder.mTermsAndCondTextView
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initActionBar() {
        final View actionBarView = getActivity().getActionBar().getCustomView();
        final TextView title = (TextView)actionBarView.findViewById(R.id.action_bar_title);
        title.setText(R.string.registration_action_bar_title);

        final ImageView back = (ImageView)actionBarView.findViewById(R.id.action_bar_back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void initSpinners() {
        initSchoolsSpinner();
        initUserTypeSpinner();
    }

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
                    getSchoolsNames(mData.mSchools),
                    getString(R.string.registration_schools_hint)
            );
        }
    }

    private void initUserTypeSpinner() {
        initSpinner(
                mViewHolder.mUserSpinner,
                R.array.registration_user_type,
                R.string.registration_users_hint);
    }

    private void initSpinner(final Spinner spinner, final int arrRes, final int hintId) {
        HintSpinnerAdapter.setAdapter(
                mContext,
                spinner,
                Arrays.asList(getResources().getStringArray(arrRes)),
                getString(hintId));
    }

    private List<String> getSchoolsNames(final School[] schools) {
        final LinkedList<String> names = new LinkedList<String>();
        if(schools != null) {
            for(School school: schools) {
                if(school != null) {
                    names.add(school.mName);
                }
            }
        }
        return names;
    }

    private BroadcastReceiver mUiDataFeedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final SchoolsMsg schools =
                    (SchoolsMsg)intent.getSerializableExtra(Constants.SCHOOLS_DATA);
            if(schools != null) {
                mData.mSchools = schools.mSchools;

                HintSpinnerAdapter.setAdapter(
                        mContext,
                        mViewHolder.mSchoolsSpinner,
                        getSchoolsNames(schools.mSchools),
                        getString(R.string.registration_schools_hint)
                );

                mViewHolder.mSchoolsSpinner.setEnabled(true);
                mViewHolder.mSchoolsProgressBar.setVisibility(View.GONE);
            }
        }
    };

}