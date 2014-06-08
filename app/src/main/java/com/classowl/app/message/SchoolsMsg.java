package com.classowl.app.message;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SchoolsMsg implements Serializable {
    public static class School implements Serializable {
        public static final String ID = "id";
        public static final String NAME = "name";

        public static School fromJsonObject(final JSONObject jObject) {
            final School s = new School();
            if(jObject != null) {
                try {
                    s.mId = jObject.getLong(ID);
                    s.mName = jObject.getString(NAME);
                } catch (JSONException e) {
                    Log.e(TAG, "Wrong JSON format", e);
                }
            }
            return s;
        }

        public School() {}
        public School(final long id, final String name) {
            mId = id;
            mName = name;
        }

        public long mId;
        public String mName;

        @Override
        public String toString() {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(ID, mId);
                jsonObject.put(NAME, mName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }
    }

    private static final String TAG = SchoolsMsg.class.getSimpleName();

    private static final String OBJECTS = "objects";

    public School[] mSchools;

    public static SchoolsMsg fromJsonStr(final String jsonStr) {
        final SchoolsMsg schoolsMsg = new SchoolsMsg();
        if(!TextUtils.isEmpty(jsonStr)) {
            try {
                final JSONObject jObject = new JSONObject(jsonStr);
                final JSONArray schoolsArray = jObject.getJSONArray(OBJECTS);
                schoolsMsg.mSchools = new School[schoolsArray.length()];
                for(int i = 0; i < schoolsArray.length(); i++) {
                    schoolsMsg.mSchools[i] = School.fromJsonObject(schoolsArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Not a JSON format", e);
            }
        }
        return schoolsMsg;
    }

    public SchoolsMsg() {}
    public SchoolsMsg(final School[] schools) {
        mSchools = schools;
    }

    @Override
    public String toString() {
        final JSONArray jsonArray = new JSONArray();
        if(mSchools != null) {
            for (School school : mSchools) {
                jsonArray.put(school);
            }
        }
        return jsonArray.toString();
    }
}