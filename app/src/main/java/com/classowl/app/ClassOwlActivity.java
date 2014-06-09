package com.classowl.app;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.classowl.app.fragment.RegistrationFragment;


public class ClassOwlActivity extends FragmentActivity {

    /**
     * TODO: Activity should have more states. Right now only REGISTRATION were required.
     */
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

    /**
     * Setting ActionBar into custom mode.
     */
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