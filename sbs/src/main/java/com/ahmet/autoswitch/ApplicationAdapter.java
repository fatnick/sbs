/*
* Copyright (C) 2014 Ahmet Yildirim
* License:
* http://www.gnu.org/licenses/gpl.html GPL v3
* https://tldrlegal.com/license/gnu-general-public-license-v3-(gpl-3)
*/

package com.ahmet.autoswitch;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.frma.sbs.R;

import java.util.List;

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
    private List<ApplicationInfo> appsList = null;
    private Context context;
    private AutoSwitchList activity;
    private PackageManager packageManager;

    public ApplicationAdapter(AutoSwitchList activity, int textViewResourceId, List<ApplicationInfo> appsList) {
        super((Context)activity, textViewResourceId, appsList);
        this.context = (Context)activity;
        this.activity = activity;
        this.appsList = appsList;
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.snippet_list_row, null);
        }

        ApplicationInfo data = appsList.get(position);
        if (null != data) {
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            TextView packageName = (TextView) view.findViewById(R.id.app_paackage);
            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);

            appName.setText(data.loadLabel(packageManager));
            packageName.setText(data.packageName);
            iconview.setImageDrawable(data.loadIcon(packageManager));

            CheckBox cBox = (CheckBox) view.findViewById(R.id.checkBox);
            cBox.setTag(Integer.valueOf(position));
            cBox.setChecked(activity.SelectedApps.contains(data.packageName));
            cBox.setOnCheckedChangeListener(mListener);
        }
        return view;
    }
    CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ApplicationInfo data = appsList.get((Integer)buttonView.getTag());
            activity.setAppStatus(data.packageName,isChecked);
        }
    };
};