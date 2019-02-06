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
import java.util.Arrays;
import java.util.List;

public class ListAdapter extends BaseAdapter implements Filterable {
    private LayoutInflater mInflater;
    private ListFilter mFilter;
    private String[] mOriginal;
    private String[] mFiltered;

    ListAdapter(Context context, String[] objects) {
        mInflater = LayoutInflater.from(context);
        mFilter = new ListFilter();
        mOriginal = objects;
        mFiltered = objects;
    }

    @Override
    public int getCount() {
        if (mFiltered != null) {
            return mFiltered.length;
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mFiltered != null) {
            return mFiltered[position];
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();;
        }

        String item = (String) getItem(position);
        viewHolder.name.setText(item);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    //

    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> filtered = new ArrayList<>();

            if (constraint != null && constraint.length() != 0) {
                String needle = constraint.toString().toUpperCase();

                if (mOriginal != null && mOriginal.length != 0) {
                    for (String original : mOriginal) {
                        if (original.toUpperCase().startsWith(needle)) {
                            filtered.add(original);
                        }
                    }
                }
            }
            else {
                filtered = Arrays.asList(mOriginal);
            }

            results.count = filtered.size();
            results.values = filtered.toArray(new String[0]);
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFiltered = (String[]) results.values;
            notifyDataSetChanged();
        }
    }

    //

    private static class ViewHolder {
        TextView name;
    }
}