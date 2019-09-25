package com.qwant.liberty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.mozilla.gecko.R;

import java.util.ArrayList;

class SuggestAdapter extends ArrayAdapter<SuggestItem> implements Filterable {
    private final static String LOGTAG = "QwantAssist";

    private ArrayList<SuggestItem> _data;
    private Context _context;

    SuggestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        _data = new ArrayList<>();
        _context = context;
    }

    @Override public int getCount() {
        return _data.size();
    }
    @Override public SuggestItem getItem(int index) {
        return _data.get(index);
    }
    @Override public View getView(int position, @Nullable View listItemView, @NonNull ViewGroup parent) {
        if (listItemView == null) {
            listItemView = LayoutInflater.from(_context).inflate(R.layout.qwant_widget_suggestlist_item, parent, false);
        }

        SuggestItem item = _data.get(position);

        ImageView image = listItemView.findViewById(R.id.suggest_icon);
        image.setImageResource(R.drawable.qwant_icon_search);
        // TODO add icon for history

        TextView name = listItemView.findViewById(R.id.suggest_text);
        String display_text = (item.display_text.length() > Assist.MAX_SUGGEST_TEXT_LENGTH) ? item.display_text.substring(0, Assist.MAX_SUGGEST_TEXT_LENGTH) : item.display_text;
        name.setText(display_text);

        return listItemView;
    }

    // Get suggest data from qwant and local history, filtered by "constraint" string
    @Override @NonNull public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    try {
                        _data = SuggestRequest.getSuggestions(constraint.toString());
                        // TODO add history
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
