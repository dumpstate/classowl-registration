package com.classowl.app.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

        for(String label: labels) {
            items.add(new Item(label, false));
        }
        items.add(new Item(hint, true));

        final HintSpinnerAdapter adapter =
                new HintSpinnerAdapter(
                        context,
                        R.layout.spinner_item,
                        items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setSelection(items.size() - 1);
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

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }

}