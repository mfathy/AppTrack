package com.mfathy.apptrack.presentation.ui.applist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mfathy.apptrack.R;
import com.mfathy.apptrack.data.model.BlackListedApp;
import com.mfathy.apptrack.data.model.AppEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohammed Fathy on 12/08/2018.
 * dev.mfathy@gmail.com
 *
 * Applications list recyclerView adapter.
 */

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private AppListAdapterInteractions mListener;
    private List<BlackListedApp> mBlackListedApps;
    private List<AppEntry> appEntries;

    AppListAdapter(ArrayList<AppEntry> objects, AppListAdapterInteractions mListener) {
        this.appEntries = objects;
        this.mListener = mListener;
    }

    public void setData(List<AppEntry> data, List<BlackListedApp> appEntities) {
        this.appEntries = data;
        this.mBlackListedApps = appEntities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_icon_text, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppListAdapter.ViewHolder holder, int position) {
        AppEntry item = appEntries.get(position);
        holder.icon.setImageDrawable(item.getIcon());
        holder.text.setText(item.getLabel());

        BlackListedApp blackListedApp = new BlackListedApp(item.getApplicationInfo().packageName, item.getLabel());
        if (mBlackListedApps != null && mBlackListedApps.contains(blackListedApp))
            holder.blackListSwitch.setChecked(true);
        else
            holder.blackListSwitch.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return appEntries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        private ImageView icon;
        private TextView text;
        private Switch blackListSwitch;

        ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            text = view.findViewById(R.id.text);
            blackListSwitch = view.findViewById(R.id.black_list_switch);

            view.setOnClickListener(this);
            blackListSwitch.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            blackListSwitch.toggle();
            mListener.onToggleBlackListButton(appEntries.get(getAdapterPosition()), blackListSwitch.isChecked());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mListener.onToggleBlackListButton(appEntries.get(getAdapterPosition()), isChecked);
        }
    }
}
