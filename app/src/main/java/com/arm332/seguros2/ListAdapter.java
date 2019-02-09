package com.arm332.seguros2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter implements Filterable {
    private LayoutInflater mInflater;
    private ListFilter mFilter;
    private List<List<Object>> mObjects;
    private List<List<Object>> mFiltered;

    ListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mObjects = Utils.getList(context);
        mFiltered = mObjects;
    }

    @Override
    public int getCount() {
        if (mFiltered != null) {
            return mFiltered.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mFiltered != null) {
            List<Object> values = mFiltered.get(position);
            return values.get(Utils.TITLE_COLUMN);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mFiltered != null) {
            List<Object> values = mFiltered.get(position);
            Integer lineNumber = (Integer) values.get(0);
            return Long.valueOf(lineNumber);
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String item = (String) getItem(position);
        viewHolder.textView.setText(item);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ListFilter();
        }

        return mFilter;
    }

    //

    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<List<Object>> filtered = new ArrayList<>();
            String needle = "";

            if (constraint != null && constraint.length() != 0) {
                needle = constraint.toString().trim().toUpperCase();
            }

            if (mObjects != null) {
                for (int i = 0; i < mObjects.size(); i++) {
                    List<Object> line = mObjects.get(i);

                    if (line != null) {
                        String title = (String) line.get(Utils.TITLE_COLUMN);

                        if (title != null && title.startsWith(needle)) {
                            filtered.add(line);
                        }
                    }
                }
            }

            results.count = filtered.size();
            results.values = filtered;

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFiltered = (List<List<Object>>) results.values;
            notifyDataSetChanged();
        }
    }

    //

    private static class ViewHolder {
        TextView textView;
    }
}