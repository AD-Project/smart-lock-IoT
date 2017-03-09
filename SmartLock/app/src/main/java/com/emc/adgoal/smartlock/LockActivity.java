package com.emc.adgoal.smartlock;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;

public class LockActivity extends AppCompatActivity {

    public LockActivity()
    {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        ActionBar actionBar = (ActionBar) getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setDisplayUseLogoEnabled(false);

        imageView = (ImageView) findViewById(R.id.imageView);
        lockStatus = (TextView) findViewById(R.id.lockStatus);

        PhoneNumber ph = new PhoneNumber(getApplicationContext());
        //PhoneNumber ph = new PhoneNumber("9902444588");
        this.remoteLock = new RemoteLock(ph, "192.168.43.252", 80);
        customToast("Phone Number: " + ph.getPhoneNumber());

        updateView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem Login = menu.findItem(R.id.Login);
        MenuItem Update = menu.findItem(R.id.Update);
        MenuItem Help = menu.findItem(R.id.Help);
        MenuItem Logout = menu.findItem(R.id.Logout);

        menuStyle(Login);
        menuStyle(Update);
        menuStyle(Help);
        menuStyle(Logout);

        SharedPreferences prefs = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);
        boolean loggedIn = prefs.getBoolean("logged_in", false);

        if (loggedIn) {
            Login.setVisible(false);
            Help.setVisible(true);
            Update.setVisible(true);
            Logout.setVisible(true);
        }
        else {
            Login.setVisible(true);
            Help.setVisible(true);
            Update.setVisible(false);
            Logout.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Login:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.Update:
                Intent intent1 = new Intent(this, UpdateActivity.class);
                startActivity(intent1);
                break;
            case R.id.Logout:
                logout();
                final ProgressDialog progressDialog = new ProgressDialog(LockActivity.this, R.style.AppTheme_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Logging out..");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                            }
                        }, 1200);
                break;
            case R.id.Help:
                break;
            case R.id.Refresh:
                updateView();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void lockClicked(View v)
    {
        try {
            if (remoteLock.isLocked(getApplicationContext())) {
                imageView.setImageResource(R.drawable.unlocking);
                customToast("Unlocking");
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (remoteLock.unlock() == true) {
                                    customToast("Unlocked successfully");
                                    imageView.setImageResource(R.drawable.unlocked);
                                    lockStatus.setText("Unlocked");
                                } else {
                                    customToast("Unlock Failed");
                                    imageView.setImageResource(R.drawable.locked);
                                }
                            }
                        }, 1000);

            } else {
                imageView.setImageResource(R.drawable.locking);
                customToast("Locking");
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (remoteLock.lock() == true) {
                                    customToast("Locked successfully");
                                    imageView.setImageResource(R.drawable.locked);
                                    lockStatus.setText("Unlocked");
                                } else {
                                    customToast("Lock Failed");
                                    imageView.setImageResource(R.drawable.unlocked);
                                }
                            }
                        }, 1000);
            }
        }
        catch (Exception e)
        {
            imageView.setImageResource(R.drawable.unauthorized);
            imageView.invalidate();
            imageView.setClickable(false);
        }
    }

    private void menuStyle(MenuItem item)
    {
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new ForegroundColorSpan(Color.rgb(0,191,255)), 0, s.length(), 0);
        item.setTitle(s);
    }

    private void updateView()
    {
        try {
            if (remoteLock.isLocked(getApplicationContext())) {
                imageView.setImageResource(R.drawable.locked);
                lockStatus.setText("Locked");
            } else {
                imageView.setImageResource(R.drawable.unlocked);
                lockStatus.setText("Unlocked");
            }
            invalidate();
        }
        catch (Exception e)
        {
            imageView.setImageResource(R.drawable.unauthorized);
            imageView.invalidate();
            imageView.setClickable(false);
        }
    }

    public void customToast(String text)
    {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
        toastMessage.setTextSize(20);
        toastMessage.setTextColor(Color.rgb(0,191,255));
        toastMessage.setGravity(Gravity.CENTER);
        toastMessage.setCompoundDrawablePadding(10);
        toastView.setBackgroundColor(Color.TRANSPARENT);
        toast.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        logout();
    }

    public void logout()
    {
        SharedPreferences.Editor prefEditor = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE).edit();
        prefEditor.putBoolean ("logged_in", false);
        prefEditor.commit();
    }

    private void invalidate()
    {
        lockStatus.invalidate();
        imageView.invalidate();
    }

    private RemoteLock remoteLock;
    private ImageView  imageView;
    private TextView   lockStatus;
}
