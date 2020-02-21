package com.example.voting.scheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class Util {

    public static void schedulejob(Context context){
        ComponentName serviceComponent = new ComponentName(context, TestJobService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobInfo.Builder builder = new JobInfo.Builder(0,serviceComponent);

            builder.setMinimumLatency(1000); // wait at least
            builder.setOverrideDeadline(3000); // maximum delay
            JobScheduler jobScheduler = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M    ) {
                jobScheduler = context.getSystemService(JobScheduler.class);
            }

            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            //builder.setRequiresDeviceIdle(true); // device should be idle
            //builder.setRequiresCharging(false); // we don't care if the device is charging or not
            jobScheduler.schedule(builder.build());
        }
    }
}
