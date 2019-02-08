package com.arm332.seguros2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListAdapter extends BaseAdapter implements Filterable {
    private LayoutInflater mInflater;
    private ListFilter mFilter = null;
    private JSONArray mObjects;
    private JSONArray mFiltered;

    ListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

        try {
            String jsonString = Utils.file2str(context);
            JSONObject jsonObject = new JSONObject(jsonString);
            mObjects = jsonObject.getJSONArray("values");
            // mFiltered = mObjects;
            getFilter().filter(null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        if (mFiltered != null) {
            return mFiltered.length();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mFiltered != null) {
            try {
                JSONArray jsonArray = mFiltered.getJSONArray(position);
                return jsonArray.getString(1);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
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
            viewHolder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();;
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

    public String getHTMLFromPosition(int position) {
        String result = null;

        if (mObjects != null) {
            JSONArray headers = mObjects.optJSONArray(0);

            if (headers != null) {
                JSONArray line = mFiltered.optJSONArray(position);

                if (line != null) {
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < headers.length(); i++) {
                        sb.append("<p><b>");
                        sb.append(headers.optString(i));
                        sb.append("</b><br />");
                        sb.append(line.optString(i));
                        sb.append("</p>");
                    }

                    return sb.toString();
                }
            }
        }

        return null;
    }

    //

    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            JSONArray filtered = new JSONArray();
            String needle = null;

            if (constraint != null && constraint.length() != 0) {
                needle = constraint.toString().trim().toUpperCase();
            }

            if (mObjects != null && mObjects.length() != 0) {
                int blankLines = 0;

                // Skip first line (column headers)
                for (int i = 1; i < mObjects.length();i++) {
                    JSONArray line = mObjects.optJSONArray(i);

                    // Skip blank lines ad stop on 3 consecutive blank lines
                    if (line != null && line.length() != 0) {

                        if (needle != null && needle.length() != 0) {
                            String name = line.optString(1);

                            if (name != null && name.startsWith(needle)) {
                                filtered.put(line);
                                blankLines = 0;
                            }
                        }
                        else {
                            filtered.put(line);
                            blankLines = 0;
                        }
                    }
                    else if (blankLines < 3) {
                        blankLines++;
                    }
                    else {
                        break;
                    }
                }
            }

            results.count = filtered.length();
            results.values = filtered;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFiltered = (JSONArray) results.values;
            notifyDataSetChanged();
        }
    }

    //

    private static class ViewHolder {
        TextView textView;
    }
}