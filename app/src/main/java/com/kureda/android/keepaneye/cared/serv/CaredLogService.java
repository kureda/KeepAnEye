package com.kureda.android.keepaneye.cared.serv;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.DetectedActivity;
import com.kureda.android.keepaneye.both.util.UserActivity;

public class CaredLogService extends Service {
    private static GoogleApiClient sGoogleApiClient;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        checkGoogleClient();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        checkGoogleClient();
        if (!sGoogleApiClient.isConnecting() && sGoogleApiClient.isConnected()) {
            getSnapshot();
        }
        uploadActivity(); //upload data if no google service available
        return START_STICKY;
    }

    private void checkGoogleClient() {
        if (sGoogleApiClient == null) {
            sGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Awareness.API)
                    .build();
        }
        if (!sGoogleApiClient.isConnected() && !sGoogleApiClient.isConnecting()) {
            sGoogleApiClient.connect();
        }
    }


    private void getSnapshot() {
        Awareness.SnapshotApi.getDetectedActivity(sGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (detectedActivityResult.getStatus().isSuccess()) {
                            DetectedActivity activity = detectedActivityResult
                                    .getActivityRecognitionResult()
                                    .getMostProbableActivity();
                            logActivity(activity);
                        } else {
                        }
                    }

                });
    }


    private void logActivity(DetectedActivity activity) {
        int activityType = activity.getType();
        if (activity.getConfidence() >= 50) {
            if (activityType == DetectedActivity.ON_FOOT) { //walking or running
                UserActivity.logWalking();
            } else if (activityType == DetectedActivity.IN_VEHICLE) {
                UserActivity.logRiding();
            }
        }
    }

    private void uploadActivity() {
        Uploader.upload();
    }

}
