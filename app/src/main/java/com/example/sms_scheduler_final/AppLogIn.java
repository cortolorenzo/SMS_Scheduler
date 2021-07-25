package com.example.sms_scheduler_final;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AppLogIn extends AppCompatActivity {

    private EditText password;
    private Button btnLogin;
    private static final String TAG = "Main Activity";
    private String passFinal = "1992";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_log_in);

        password = (EditText)findViewById(R.id.editTextNumberPassword);
        btnLogin = (Button)findViewById(R.id.loginButton);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2)
            {
                Log.d(TAG, "Uruchomiono metodę validacji " + s);

                if (s.toString().equals(passFinal))
                {
                    Log.d(TAG, "Iam in  " + s);
                    validate( s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public void CheckLogIn(View v){
        validate(password.getText().toString());
        Log.d(TAG, "Uruchomiono metodę validacji " + password.getText().toString());

    }
    public void validate(String passwordVar){
       Log.d(TAG, "hasło to: " + passwordVar);

        if(passwordVar.equals(passFinal))
        {
            Log.d(TAG, "hasło2 to: " + passwordVar);
            Intent intent = new Intent(AppLogIn.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(AppLogIn.this, " Permission granted", Toast.LENGTH_SHORT).show();
            password.setText("");
        }
        else

        {
            Toast.makeText(AppLogIn.this, " Try again baby ;)", Toast.LENGTH_SHORT).show();
        }
    }


}