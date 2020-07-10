/*
v1.1: cleaned
*/
package com.hms.localauth.facerecdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.huawei.facerecognition.FaceManager;
import com.huawei.facerecognition.HwFaceManagerFactory;

public class LoginActivity extends AppCompatActivity {
  private static final int CAMERA_REQUEST_CODE = 1;
  private static final String TAG = LoginActivity.class.getSimpleName();

  private ShowToastUtils showToastUtils = new ShowToastUtils();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      Log.i(TAG, "onCreate");
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);
      setTitle ("Bank of Virtual - Login");

      int permissionCheck = ContextCompat.checkSelfPermission(LoginActivity.this,
                                                              Manifest.permission.CAMERA);
      if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA},
                                            CAMERA_REQUEST_CODE);
      }

      Log.i(TAG, "FaceRecognize begin");
      Button button = (Button) findViewById(R.id.login);
      if (button == null) {
          Log.e(TAG, "button is null");
          return;
      }
      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Log.i(TAG, "Begin to Face Recognized");
              CancellationSignal signal = null;
              if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                  signal = new CancellationSignal();
              }
              FaceManager.AuthenticationCallback callback = new FaceManager.AuthenticationCallback() {
                  @Override
                  public void onAuthenticationError(int errMsgId, CharSequence errString) {
                      Log.i(TAG, "Lowest level return onAuthenticationError" + errString);
                      showToastUtils.showToast(getApplicationContext(), "onAuthenticationError", Toast.LENGTH_SHORT);
                  }
                  @Override
                  public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                      Log.i(TAG, "Lowest level return onAuthenticationHelp");
                      showToastUtils.showToast(getApplicationContext(), "onAuthenticationHelp", Toast.LENGTH_SHORT);
                  }
                  @Override
                  public void onAuthenticationSucceeded(FaceManager.AuthenticationResult result) {
                      Log.i(TAG, "onAuthenticationSucceeded");
                      showToastUtils.showToast(getApplicationContext(), "onAuthenticationSucceeded",
                          Toast.LENGTH_SHORT);

                      Intent newActivity = new Intent(getApplicationContext(), MainActivity.class);
                      startActivity(newActivity);
                  }
                  @Override
                  public void onAuthenticationFailed() {
                      Log.i(TAG, "Lowest level return onAuthenticationFailed");
                      showToastUtils.showToast(getApplicationContext(), "onAuthenticationFailed", Toast.LENGTH_SHORT);
                  }
              };

              doAuthenticate(signal, callback);
          }
      });
  }

  private void doAuthenticate(CancellationSignal signal, FaceManager.AuthenticationCallback callback) {
      Log.i(TAG, "Get FaceManager");

      FaceManager faceManager = HwFaceManagerFactory.getFaceManager(getApplicationContext());
      if (faceManager == null) {
          Log.e(TAG, "The current version does not support face authentication");
          Toast.makeText(LoginActivity.this, "当前版本不支持人脸认证", Toast.LENGTH_SHORT).show();
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
              Toast.makeText(LoginActivity.this, "无人脸录入", Toast.LENGTH_SHORT).show();
          }
      } else {
          Toast.makeText(LoginActivity.this, "当前设备不支持人脸识别", Toast.LENGTH_SHORT).show();
      }
      Log.i(TAG, "Authentication is called successfully");
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      if (requestCode == CAMERA_REQUEST_CODE) {
          if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // TODO: optional: display
          } else {
              /* The user has checked the no longer ask, prompting the user to open the permission manually */
              if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                  Toast.makeText(this, "相机权限已被禁止", Toast.LENGTH_SHORT).show();
              }
          }
      }
  }
}
