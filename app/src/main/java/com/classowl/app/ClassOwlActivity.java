package com.classowl.app;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.classowl.app.fragment.RegistrationFragment;


public class ClassOwlActivity extends FragmentActivity {

    public static enum State {
        REGISTRATION
    };

    private static final State INITIAL_STATE = State.REGISTRATION;

    private State mState = INITIAL_STATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar();
        setContentView(R.layout.activity_classowl);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, getNewFragment())
                    .commit();
        }
    }

    private void setCustomActionBar() {
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.action_bar);
    }

    private Fragment getNewFragment() {
        switch(mState) {
            case REGISTRATION: {
                return new RegistrationFragment();
            }
            default: throw new IllegalStateException("Unknown state.");
        }
    }

}