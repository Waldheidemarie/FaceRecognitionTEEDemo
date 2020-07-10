/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */
 /*
 v3.1: working and cleaned.
 */
package com.hms.localauth.facerecdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.facerecognition.FaceManager;
import com.huawei.facerecognition.HwFaceManagerFactory;

/**
 * Entrance to face authentication
 *
 * @since 2019-06-27
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int TWO_SECONDS = 1000*2;
    private static volatile int counter = 0;

    private TextView tvPermissionStatus = null;
    private ShowToastUtils showToastUtils = new ShowToastUtils();
    private CountDownTimer mCountDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPermissionStatus = findViewById(R.id.tvPermissionStatus);

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                                                                Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                                              CAMERA_REQUEST_CODE);
        } else {
            tvPermissionStatus.setText("相机权限已申请");
        }

        Log.i(TAG, "FaceRecognize begin");
    } // onCreate()

    @Override
    protected void onStart() {
      super.onStart();
      Log.i(TAG, "[onStart()] called.");
      startCountdownTimer();
    }

    private void startCountdownTimer() {
        mCountDownTimer = new CountDownTimer(Long.MAX_VALUE, TWO_SECONDS) {
          // This is called after every 10 sec interval.
          public void onTick(long millisUntilFinished) {
            //setUi("Using count down timer");
            runOnUiThread(new Runnable() {
              public void run() {
                startFaceRecognition();
              }
            });
          }

          public void onFinish() {
            Log.i(TAG, "[onFinish()]: finished");
          }
        }.start();
    }

    private void stopCountdownTimer() {
        mCountDownTimer.cancel(); // = null;
    }

    @Override
    protected void onStop() {
      super.onStop();
      Log.i(TAG, "[onStop()] called.");

      stopCountdownTimer();
    }

    @Override
    protected void onDestroy() {
      super.onDestroy();
      Log.i(TAG, "[onDestroy()] called.");

      stopCountdownTimer();
    }

    private void startFaceRecognition() {
      Log.i(TAG, "Begin to Face Recognized");

      tvPermissionStatus.setText("");

      CancellationSignal signal = null;
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
          signal = new CancellationSignal();
      }

      FaceManager.AuthenticationCallback callback = new FaceManager.AuthenticationCallback() {
          private static final String POSITIVE = "this is Alan";
          private static final String NEGATIVE = "this is NOT Alan";

          @Override
          public void onAuthenticationError(int errMsgId, CharSequence errString) {
              showToastUtils.showToast(getApplicationContext(), "onAuthenticationError", Toast.LENGTH_SHORT);
              tvPermissionStatus.setText(NEGATIVE);

              startSecurityQuestionActivity();
          }
          @Override
          public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
              showToastUtils.showToast(getApplicationContext(), "onAuthenticationHelp", Toast.LENGTH_SHORT);
          }
          @Override
          public void onAuthenticationSucceeded(FaceManager.AuthenticationResult result) {
              Log.i(TAG, "onAuthenticationSucceeded");
              showToastUtils.showToast(getApplicationContext(), "onAuthenticationSucceeded",
                  Toast.LENGTH_SHORT);
              tvPermissionStatus.setText(POSITIVE);
          }
          @Override
          public void onAuthenticationFailed() {
              showToastUtils.showToast(getApplicationContext(), "onAuthenticationFailed", Toast.LENGTH_SHORT);
              tvPermissionStatus.setText(NEGATIVE);
          }

          private void startSecurityQuestionActivity() {
              Log.i(TAG, "[startSecurityQuestionActivity()]: called.");

              Intent newActivity = new Intent(getApplicationContext(), SecurityQuestionActivity.class);
              startActivity(newActivity);

              overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
              return;
          }
      };

      doAuthenticate(signal, callback);
    }

    private void doAuthenticate(CancellationSignal signal, FaceManager.AuthenticationCallback callback) {
        FaceManager faceManager = HwFaceManagerFactory.getFaceManager(getApplicationContext());
        Log.i(TAG, "Get FaceManager");
        if (faceManager == null) {
            Log.e(TAG, "The current version does not support face authentication");
            Toast.makeText(MainActivity.this, "当前版本不支持人脸认证", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "Get FaceManager successfully");
        /* Query whether the current device supports face recognition */
        if (faceManager.isHardwareDetected()) {
            /* Query whether the current device has facial data */
            if (faceManager.hasEnrolledTemplates()) {
                /* Call face authenticate */
                faceManager.authenticate(null, signal, 0, callback, null);
            } else {
                Toast.makeText(MainActivity.this, "无人脸录入", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "当前设备不支持人脸识别", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "Authentication is called successfully");
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "[onBackPressed()]: pressed");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                tvPermissionStatus.setText("相机权限申请成功");
            } else {
                /* The user has checked the no longer ask, prompting the user to open the permission manually */
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "相机权限已被禁止", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
