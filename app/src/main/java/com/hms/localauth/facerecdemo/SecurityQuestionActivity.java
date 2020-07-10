/*
v1.1: cleaned
*/
package com.hms.localauth.facerecdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SecurityQuestionActivity extends AppCompatActivity {
    private static final String TAG = SecurityQuestionActivity.class.getSimpleName();

    private EditText answerEditText;
    private ShowToastUtils showToastUtils = new ShowToastUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_question);
        setTitle("Security Questions");

        answerEditText = (EditText) findViewById(R.id.cityofbirth);

        Button button = (Button) findViewById(R.id.verify);
        if (button == null) {
            Log.e(TAG, "button is null");
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    verifyAnswer();
                  }
                });
            }
        });
    }

    private void verifyAnswer() {
      String message = "Verification Successful";

      String answer = answerEditText.getText().toString();
      //boolean check = "Beijing".equalIgnorecase(answer);
      boolean check = (answer != null && answer.length() > 0 && answer.equalsIgnoreCase("beijing"));

      if (check) {
        NavUtils.navigateUpFromSameTask(this);
      } else {
        message = "Birth city mistached. Try again";
      }

      showToastUtils.showToast(getApplicationContext(), message, Toast.LENGTH_SHORT);
    }

    private void navigateBack() {
      finish();

      Intent newActivity = new Intent(getApplicationContext(), MainActivity.class);
      startActivity(newActivity);
    }

    @Override
    public void onBackPressed() {
      Log.i(TAG, "[onBackPressed()]: pressed"); //  {button is null");
    }
}
