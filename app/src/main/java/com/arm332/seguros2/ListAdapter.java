package com.arm332.seguros2;

import android.content.Context;
import android.media.midi.MidiOutputPort;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListAdapter extends BaseAdapter implements Filterable {
    private LayoutInflater mInflater;
    private ListFilter mFilter;
    private List<List<String>> mObjects;
    private List<List<String>> mFiltered;

    ListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mObjects = Utils.loadData(context);
        getFilter().filter(null);
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
            try {
                List<String> values = mFiltered.get(position);
                return values.get(1);
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

    public String getHTMLFromPosition(int position) {
        if (mObjects != null) {
            List<String> headers = mObjects.get(0);

            if (headers != null) {
                List<String> line = mFiltered.get(position);

                if (line != null) {
                    StringBuilder sb = new StringBuilder();

                    // Skip last column (antigo)
                    for (int i = 0; i < headers.size() - 1; i++) {
                        sb.append("<p><b>");
                        sb.append(headers.get(i));
                        sb.append("</b><br />");

                        if (line.size() > i) {
                            sb.append(line.get(i));
                        }

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
            List<List<String>> filtered = new ArrayList<>();
            String needle = "";

            if (constraint != null && constraint.length() != 0) {
                needle = constraint.toString().trim().toUpperCase();
            }

            if (mObjects != null) {
                int blankLines = 0;

                // Skip first line (column headers)
                for (int i = 1; i < mObjects.size(); i++) {
                    List<String> line = mObjects.get(i);

                    // Skip blank lines
                    if (line != null && line.size() > 1) {
                        String title = line.get(1);

                        // Skip with blank second column (title)
                        if (title != null && title.length() != 0) {
                            if (title.startsWith(needle)) {
                                filtered.add(line);
                            }

                            blankLines = 0;
                        }
                        else if (blankLines < 3) {
                            blankLines++;
                        }
                        else {
                            break;
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
            mFiltered = (List<List<String>>) results.values;
            notifyDataSetChanged();
        }
    }

    //

    private static class ViewHolder {
        TextView textView;
    }
}