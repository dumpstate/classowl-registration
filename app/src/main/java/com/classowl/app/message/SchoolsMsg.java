package com.classowl.app.message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SchoolsMsg implements Serializable {
    public static class School implements Serializable {
        public static final String ID = "ID";
        public static final String NAME = "NAME";

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

    public School[] mSchools;

    public static SchoolsMsg fromJsonStr(final String jsonStr) {
        return null;
    }

    public SchoolsMsg() {}
    public SchoolsMsg(final School[] schools) {
        mSchools = schools;
    }

    @Override
    public String toString() {
        final JSONArray jsonArray = new JSONArray();
        for(School school: mSchools) {
            jsonArray.put(school);
        }
        return jsonArray.toString();
    }
}