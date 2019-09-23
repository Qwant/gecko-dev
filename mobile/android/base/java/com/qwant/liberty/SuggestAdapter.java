package com.qwant.liberty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

class SuggestAdapter extends ArrayAdapter<String> implements Filterable {
    private final static String LOGTAG = "QwantAssist";

    private ArrayList<String> _data;

    SuggestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        _data = new ArrayList<>();
    }

    @Override public int getCount() {
        return _data.size();
    }
    @Override public String getItem(int index) {
        return _data.get(index);
    }

    // @Override public getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {}
    @Override @NonNull public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    try {
                        _data = SuggestRequest.getSuggestions(constraint.toString());
                    } catch(Exception e) {
                        Log.e(LOGTAG, "suggest adapter filtering failed: " + e.getMessage());
                    }
                    filterResults.values = _data;
                    filterResults.count = _data.size();
                }
                return filterResults;
            }

            @Override protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
