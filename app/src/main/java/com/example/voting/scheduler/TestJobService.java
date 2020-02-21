package com.example.voting.scheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TestJobService extends JobService {

    private static final String TAG = "SyncService";
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("Job"," job is started succefuly");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("Job"," job is stopped succefuly");
        return false;
    }
}
