package com.emc.adgoal.smartlock;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    EditText userIdText;
    EditText pwdText;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userIdText = (EditText) findViewById(R.id.input_username);
        pwdText = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }

    public void loginClicked(View v)
    {
        login();
    }

    public void login()
    {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        btnLogin.setEnabled(false);

        SharedPreferences.Editor prefEditor = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE).edit();
        prefEditor.putBoolean ("logged_in", false);
        prefEditor.commit();

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String username = userIdText.getText().toString();
        final String password = pwdText.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if ((username.equals(getString(R.string.username))) && (password.equals(getString(R.string.password)))){
                            onLoginSuccess();
                        }
                        else {
                            onLoginFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 2000);

    }

    public void onLoginSuccess() {
        btnLogin.setEnabled(true);

        SharedPreferences.Editor prefEditor = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE).edit();
        prefEditor.putBoolean ("logged_in", true);
        prefEditor.commit();

        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);

        SharedPreferences.Editor prefEditor = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE).edit();
        prefEditor.putBoolean ("logged_in", false);
        prefEditor.commit();
    }

    public boolean validate()
    {
        boolean valid = true;

        String username = userIdText.getText().toString();
        String password = pwdText.getText().toString();

        if (username.isEmpty()) {
            userIdText.setError("Enter a valid username");
            valid = false;
        } else {
            userIdText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            pwdText.setError("Password should be between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            pwdText.setError(null);
        }
        return valid;
    }
}
