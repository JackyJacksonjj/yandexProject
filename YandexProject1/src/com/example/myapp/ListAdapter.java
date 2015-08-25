package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<SomeEntity> {
    private List<SomeEntity> items;
    private int layoutResourceId;
    private Context context;

    public ListAdapter(Context context, int layoutResourceId, List<SomeEntity> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        someEntityHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);
        holder = new someEntityHolder();
        holder.element = items.get(position);

        holder.goNext = (ImageButton) row.findViewById(R.id.goNext);
        holder.removeButton = (ImageButton) row.findViewById(R.id.deleteButton);
        holder.refreshButton = (ImageButton) row.findViewById(R.id.mainRefreshButton);
        holder.createButton = (ImageButton) row.findViewById(R.id.createButton);
        holder.goNext.setTag(holder.element);
        holder.removeButton.setTag(holder.element);
        holder.refreshButton.setTag(holder.element);
        holder.createButton.setTag(holder.element);
        if (holder.element.getValue() > 0) {
            holder.createButton.setVisibility(View.GONE);
            holder.goNext.setBackgroundResource(R.drawable.ic_file);
            row.setTag(holder);
        }

        holder.name = (TextView) row.findViewById(R.id.firstLine);
        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(someEntityHolder holder) {
        if (holder.element.getValue() <= 0) {
            holder.name.setText(holder.element.getName());
        } else {
            holder.name.setText(holder.element.getName() + " / " + holder.element.getValue() + "Mb");
        }
    }

    public static class someEntityHolder {
        SomeEntity element;
        TextView name;
        ImageButton goNext;
        ImageButton removeButton;
        ImageButton refreshButton;
        ImageButton createButton;
    }
}