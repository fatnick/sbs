/*
* Copyright (C) 2014 Ahmet Yildirim
* License:
* http://www.gnu.org/licenses/gpl.html GPL v3
* https://tldrlegal.com/license/gnu-general-public-license-v3-(gpl-3)
*/

package com.ahmet.autoswitch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.frma.sbs.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutoSwitchList extends Activity {
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;
    private ListView list;

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    public static Set<String> SelectedApps;

    Intent watcherService;
    Button Starter;
    boolean status = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_switch_list);

        Starter = (Button) findViewById(R.id.button);
        watcherService = new Intent(AutoSwitchList.this, AppWatcherService.class);
        Starter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!status) {
                    startService(watcherService);
                    Starter.setText("Stop AutoSwitcher");
                }
                else
                {
                    stopService(watcherService);
                    Starter.setText("Start AutoSwitcher");
                }
                status=!status;
            }
        });
        packageManager = getPackageManager();



        list = (ListView) findViewById(R.id.list);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();
        SelectedApps = settings.getStringSet("SelectedApps", new HashSet<String>());
        if(SelectedApps == null) {
            SelectedApps = new HashSet<String>();
            editor.putStringSet("SelectedApps", SelectedApps);
            editor.commit();
        }
        new LoadApplications().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.auto_switch_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAppStatus(String activityPackageName,boolean state)
    {
        if(state) {
            SelectedApps.add(activityPackageName);
        }
        else
        {
            SelectedApps.remove(activityPackageName);
        }

        // Save the list.
        editor.putStringSet("SelectedApps", SelectedApps);
        editor.commit();
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(AutoSwitchList.this,
                    R.layout.snippet_list_row, applist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            list.setAdapter(listadaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(AutoSwitchList.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
