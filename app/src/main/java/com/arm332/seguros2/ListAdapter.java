package com.arm332.seguros2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private String[] mValues = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

    ListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mValues != null) {
            return mValues.length;
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mValues != null) {
            return mValues[position];
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

    private static class ViewHolder {
        TextView name;
    }
}