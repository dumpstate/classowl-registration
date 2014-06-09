package com.classowl.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.classowl.app.R;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

public class HintSpinnerAdapter extends ArrayAdapter<HintSpinnerAdapter.Item> {

    public static void setAdapter(
            final Context context,
            final Spinner spinner,
            final List<String> labels,
            final String hint) {
        Assert.assertNotNull(spinner);
        Assert.assertNotNull(labels);
        Assert.assertNotNull(hint);

        final ArrayList<Item> items = new ArrayList<Item>();

        items.add(new Item(hint, true));
        for(String label: labels) {
            items.add(new Item(label, false));
        }

        final HintSpinnerAdapter adapter =
                new HintSpinnerAdapter(
                        context,
                        R.layout.spinner_item,
                        items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    public static class Item {
        private final String mText;
        private final boolean mIsHint;

        public Item(final String text, final boolean isHint) {
            mText = text;
            mIsHint = isHint;
        }

        public String getText() {
            return mText;
        }

        public boolean isHint() {
            return mIsHint;
        }

        @Override
        public String toString() {
            return mText;
        }

    }

    public HintSpinnerAdapter(Context context, int resource, List<Item> items) {
        super(context, resource, items);
    }

    /**
     * Overriding, because of the workaround for providing default text, when
     * no item is selected. That's not the most efficient solution - the views
     * are not using convert view while being created. The best would be to
     * override a spinner.
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final View view;
        if(position == 0) {
            final TextView textView = new TextView(getContext());
            textView.setVisibility(View.GONE);
            textView.setHeight(0);
            view = textView;
        } else {
            view =super.getDropDownView(position, null, parent);
        }
        return view;
    }
}