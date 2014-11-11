/*
* Copyright (C) 2014 Ahmet Yildirim
* License:
* http://www.gnu.org/licenses/gpl.html GPL v3
* https://tldrlegal.com/license/gnu-general-public-license-v3-(gpl-3)
*/

package com.ahmet.autoswitch;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class AppWatcherService extends Service {
    private boolean keepRunning = true;
    AutoSwitchList listActivity;
    CheckRunningActivity checkerThread;

    class CheckRunningActivity extends Thread{
        ActivityManager am = null;
        Context context = null;

        public CheckRunningActivity(ActivityManager actm){
            am = actm;
        }
        String last = "";
        public void run(){

            while(keepRunning){
                // Return a list of the tasks that are currently running,
                // with the most recent being first and older ones after in order.
                // Taken 1 inside getRunningTasks method means want to take only
                // top activity from stack and forgot the olders.
                List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);

                String currentRunningActivityName = taskInfo.get(0).topActivity.getPackageName();

                if (!currentRunningActivityName.equals(last)) {
                    // show your activity here on top of PACKAGE_NAME.ACTIVITY_NAME
                    last = currentRunningActivityName;
                    Message ms = new Message();

                    ms.obj=last;

                    handler.sendMessage(ms);
                }
            }
        }
    }
    public void AppWatcherService(AutoSwitchList listActivity)
    {
        this.listActivity = listActivity;
    }
    @Override
    public void onCreate()
    {
        Toast.makeText(this, "AutoSwitcher started!", Toast.LENGTH_LONG).show();
        Log.d("ATS", "AutoSwitcher started!");
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        checkerThread=new CheckRunningActivity(am);
        checkerThread.start();
    }
    @Override
    public void onDestroy()
    {
        keepRunning = false;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(AutoSwitchList.SelectedApps.contains((String)msg.obj))
            {
                Intent intent = new Intent();
                intent.setAction("com.ahmet.autoswitch.DISABLE");
                sendBroadcast(intent);
            }
            else
            {
                Intent intent = new Intent();
                intent.setAction("com.ahmet.autoswitch.ENABLE");
                sendBroadcast(intent);
            }
            Log.d("ATS", "Message Handled");
            return false;
        }
    });
}
