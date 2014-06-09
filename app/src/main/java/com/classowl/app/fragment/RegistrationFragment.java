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
        requestFeedUi();
    }

    private void initOnChangeListeners() {
        mViewHolder.mSchoolsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enableButton();
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
     * under Constants.SHOOLS_DATA identifier. If the schools array is empty,
     * appropriate Toast message is being displayed, otherwise shcools spinner
     * is beeing filled with data and progress bar is being dismissed.
     */
    private BroadcastReceiver mUiDataFeedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final SchoolsMsg schools =
                    (SchoolsMsg)intent.getSerializableExtra(Constants.SCHOOLS_DATA);
            if(schools != null) {
                if(schools.mSchools != null && schools.mSchools.length > 0) {
                    mData.mSchools = schools.mSchools;

                    // fill schools spinner with shools data
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