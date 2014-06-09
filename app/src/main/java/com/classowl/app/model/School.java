package com.classowl.app.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * School model representation.
 *
 * TODO: only ID and NAME has been implemented. Probably more data 'll be required.
 */
public class School implements Serializable {
    public static final String TAG = School.class.getSimpleName();

    public static final String ID = "id";
    public static final String NAME = "name";

    public long mId;
    public String mName;

    public static School fromJSON(final JSONObject jObject) {
        final School s = new School();
        if(jObject != null) {
            try {
                s.mId = jObject.getLong(ID);
                s.mName = jObject.getString(NAME);
            } catch(JSONException e) {
                Log.e(TAG, "Not a JSON", e);
            }
        }
        return s;
    }

    public School() {}
    public School(final long id, final String name) {
        mId = id;
        mName = name;
    }

    public JSONObject toJSON() {
        final JSONObject jObject = new JSONObject();
        try {
            jObject.put(ID, mId);
            jObject.put(NAME, mName);
        } catch (JSONException e) {
            Log.e(TAG, "Failed creating JSONObject", e);
        }
        return jObject;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}